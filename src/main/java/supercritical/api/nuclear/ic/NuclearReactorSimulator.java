package supercritical.api.nuclear.ic;

import gregtech.api.util.GTLog;
import gregtech.common.items.behaviors.AbstractMaterialPartBehavior;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import supercritical.common.item.behaviors.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

/**
 * 核反应堆核心模拟器
 * 负责管理反应堆网格、计算热量、能量产出和组件交互
 */
public class NuclearReactorSimulator {
    // ==================== 反应堆状态 ====================
    private final ItemStack[][] componentGrid;      // 组件网格 (x * y)
    @Getter
    private final int gridWidth;                    // 网格宽度 (3-9)
    @Getter
    private final int gridHeight;                   // 网格高度 (3-9)
    // ==================== 缓存数据 (内部缓存，不污染组件NBT) ====================
    private final int[][] reflectorCounts;          // 每个位置的反射板数量缓存
    private final int[][] lastEnergyOutput;          // 每个燃料棒上一tick的能量产出缓存
    private final int[][] lastHeatOutput;            // 每个燃料棒上一tick的热量产出缓存
    private final boolean[][] isDepleted;            // 组件耗尽状态缓存
    private final List<GridPosition> fuelRodPositions = new ArrayList<>();      // 燃料棒位置缓存
    private final List<GridPosition> heatVentPositions = new ArrayList<>();     // 散热片位置缓存
    private final List<GridPosition> coolantCellPositions = new ArrayList<>();  // 冷却单元位置缓存
    private final List<GridPosition> reflectorPositions = new ArrayList<>();    // 反射板位置缓存
    private final int overheatingThreshold = 8500;  // 过热阈值 (HU)
    private final int meltdownThreshold = 9500;     // 熔毁阈值 (HU)
    @Getter
    private int currentHeat = 0;                    // 当前热量 (HU)
    @Getter
    private int maxHeatCapacity = 10000;            // 最大热容量 (HU)
    @Getter
    private long currentOutput = 0;                 // 当前能量产出 (EU/t)
    @Getter
    private int totalFuelRods = 0;                  // 燃料棒总数
    @Getter
    private int totalHeatVents = 0;                 // 散热片总数
    @Getter
    private int totalCoolantCells = 0;              // 冷却单元总数
    @Getter
    private int totalReflectors = 0;                // 反射板总数
    @Getter
    private int totalPlating = 0;                   // 隔板总数
    // ==================== 统计数据 ====================
    private int tickCount = 0;                      // 运行tick数
    private long totalEnergyProduced = 0;           // 总能量产出
    private long totalHeatProduced = 0;             // 总热量产出
    private int maxHeatReached = 0;                 // 达到过的最高热量
    @Getter
    private boolean isActive = false;               // 是否激活状态
    // ==================== 安全参数 ====================
    private float explosionResistance = 0.0f;       // 爆炸抗性 (0.0-1.0)
    private boolean hasMeltdown = false;            // 是否已熔毁

    // ==================== 构建器 ====================
    public NuclearReactorSimulator(int width, int height) {
        this.gridWidth = Math.max(3, Math.min(9, width));
        this.gridHeight = Math.max(3, Math.min(9, height));
        this.componentGrid = new ItemStack[gridWidth][gridHeight];
        // 初始化内部缓存 (不使用组件NBT存储临时数据)
        this.reflectorCounts = new int[gridWidth][gridHeight];
        this.lastEnergyOutput = new int[gridWidth][gridHeight];
        this.lastHeatOutput = new int[gridWidth][gridHeight];
        this.isDepleted = new boolean[gridWidth][gridHeight];

        // 初始化网格
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                componentGrid[x][y] = ItemStack.EMPTY;
            }
        }
    }

    // ==================== 核心模拟方法 ====================

    /**
     * 模拟一个tick的反应堆运行
     *
     * @return 返回true表示正常运行，false表示发生熔毁
     */
    public boolean simulateTick() {
        if (hasMeltdown) return false;

        tickCount++;
        currentOutput = 0;

        // 清空缓存
        clearCaches();

        // 第一阶段：收集组件信息
        collectComponentInfo();

        // 第二阶段：计算反射板加成
        calculateReflectorBonus();

        // 第三阶段：计算燃料棒产出
        calculateFuelRodOutput();

        // 第四阶段：应用散热效果
        applyHeatDissipation();

        // 第五阶段：应用冷却效果
        applyCooling();

        // 第六阶段：计算热平衡
        calculateHeatBalance();

        // 第七阶段：检查安全状态
        checkSafetyStatus();

        // 第八阶段：更新组件耐久
        updateComponentDurability();

        // 更新统计数据
        updateStatistics();

        return !hasMeltdown;
    }

    // ==================== 各阶段详细实现 ====================

    /**
     * 第一阶段：收集组件信息 (跳过耗尽组件)
     */
    private void collectComponentInfo() {
        totalFuelRods = 0;
        totalHeatVents = 0;
        totalCoolantCells = 0;
        totalReflectors = 0;
        totalPlating = 0;
        explosionResistance = 0.0f;
        maxHeatCapacity = 10000; // 基础热容量

        fuelRodPositions.clear();
        heatVentPositions.clear();
        coolantCellPositions.clear();
        reflectorPositions.clear();

        // 遍历网格收集组件
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                ItemStack stack = componentGrid[x][y];
                if (stack.isEmpty()) continue;

                // 检查是否耗尽 (使用内部缓存而不是NBT)
                if (isDepleted[x][y]) continue;

                // 检查组件类型并收集信息
                if (isFuelRod(stack)) {
                    fuelRodPositions.add(new GridPosition(x, y));
                    totalFuelRods++;
                } else if (isHeatVent(stack)) {
                    heatVentPositions.add(new GridPosition(x, y));
                    totalHeatVents++;
                } else if (isCoolantCell(stack)) {
                    coolantCellPositions.add(new GridPosition(x, y));
                    totalCoolantCells++;
                } else if (isNeutronReflector(stack)) {
                    reflectorPositions.add(new GridPosition(x, y));
                    totalReflectors++;
                } else if (isReactorPlating(stack)) {
                    totalPlating++;
                    // 隔板增加热容量和爆炸抗性
                    ReactorPlatingBehavior plating = getPlatingBehavior(stack);
                    if (plating != null) {
                        maxHeatCapacity += plating.getHeatCapacityBoost();
                        explosionResistance = Math.min(1.0f,
                                explosionResistance + plating.getExplosionResistance());
                    }
                }
            }
        }
    }

    /**
     * 第二阶段：计算反射板加成
     */
    private void calculateReflectorBonus() {
        // 为每个燃料棒计算相邻反射板数量
        for (GridPosition pos : fuelRodPositions) {
            int reflectorCount = countAdjacentReflectors(pos.x, pos.y);
            reflectorCounts[pos.x][pos.y] = reflectorCount; // 存储到内部缓存
        }
    }

    /**
     * 第三阶段：计算燃料棒产出
     */
    private void calculateFuelRodOutput() {
        long totalEnergy = 0;
        int totalHeat = 0;

        for (GridPosition pos : fuelRodPositions) {
            ItemStack stack = componentGrid[pos.x][pos.y];
            FuelRodBehavior fuelRod = getFuelRodBehavior(stack);
            if (fuelRod == null) continue;

            int reflectorCount = reflectorCounts[pos.x][pos.y]; // 从内部缓存读取

            // 计算基础产出
            int energyOutput = fuelRod.getEnergyOutput();
            int heatOutput = fuelRod.getHeatOutput();

            // 反射板加成：每个反射板增加20%效率
            float reflectorBonus = 1.0f + (reflectorCount * 0.2f);

            // 最终产出
            int finalEnergy = (int) (energyOutput * reflectorBonus);
            int finalHeat = (int) (heatOutput * reflectorBonus);

            totalEnergy += finalEnergy;
            totalHeat += finalHeat;

            // 缓存产出数据 (内部缓存)
            lastEnergyOutput[pos.x][pos.y] = finalEnergy;
            lastHeatOutput[pos.x][pos.y] = finalHeat;
        }

        currentOutput = totalEnergy;
        currentHeat += totalHeat;
        totalHeatProduced += totalHeat;
    }

    /**
     * 第四阶段：应用散热效果
     */
    private void applyHeatDissipation() {
        int totalDissipation = 0;

        for (GridPosition pos : heatVentPositions) {
            ItemStack stack = componentGrid[pos.x][pos.y];
            if (isHeatVent(stack)) {
                HeatVentBehavior vent = getHeatVentBehavior(stack);
                if (vent != null) {
                    totalDissipation += vent.getHeatDissipation();
                }
            } else if (isComponentHeatVent(stack)) {
                ComponentHeatVentBehavior componentVent = getComponentHeatVentBehavior(stack);
                if (componentVent != null) {
                    // 优化：直接计算冷却效果，避免重复遍历
                    totalDissipation += coolAdjacentFuelRods(pos.x, pos.y, componentVent);
                }
            }
        }

        // 应用散热
        currentHeat = Math.max(0, currentHeat - totalDissipation);
    }

    /**
     * 第五阶段：应用冷却效果
     */
    private void applyCooling() {
        int totalCooling = 0;

        for (GridPosition pos : coolantCellPositions) {
            ItemStack stack = componentGrid[pos.x][pos.y];
            CoolantCellBehavior coolant = getCoolantCellBehavior(stack);
            if (coolant != null) {
                totalCooling += coolant.getCoolingRate();
            }
        }

        // 应用冷却
        currentHeat = Math.max(0, currentHeat - totalCooling);
    }

    /**
     * 第六阶段：计算热平衡
     */
    private void calculateHeatBalance() {
        // 基础热损失：每tick损失1%的热量
        int baseHeatLoss = currentHeat / 100;
        currentHeat = Math.max(0, currentHeat - baseHeatLoss);

        // 更新最高热量记录
        maxHeatReached = Math.max(maxHeatReached, currentHeat);

        // 检查是否激活（有能量产出或热量超过阈值）
        isActive = (currentOutput > 0) || (currentHeat > 1000);
    }

    /**
     * 第七阶段：检查安全状态
     */
    private void checkSafetyStatus() {
        if (hasMeltdown) return; // 已熔毁则跳过检查

        // 检查过热
        if (currentHeat > overheatingThreshold) {
            // 过热警告：增加熔毁概率
            float overheatRatio = (float) (currentHeat - overheatingThreshold) /
                    (meltdownThreshold - overheatingThreshold);

            // 基础熔毁概率：每tick 0.1% * 过热比例
            float meltdownChance = 0.001f * overheatRatio;

            // 随机检查是否熔毁 (添加安全裕度)
            if (Math.random() < meltdownChance && currentHeat > overheatingThreshold + 500) {
                triggerMeltdown();
            }
        }

        // 检查熔毁
        if (currentHeat >= meltdownThreshold) {
            triggerMeltdown();
        }
    }

    private void triggerMeltdown() {
        hasMeltdown = true;
        GTLog.logger.error("Reactor meltdown at {} heat! (Max capacity: {})", currentHeat, maxHeatCapacity);
        // 实际熔毁逻辑应由外部处理 (如爆炸)
    }

    /**
     * 第八阶段：更新组件耐久
     */
    private void updateComponentDurability() {
        // 遍历所有组件并应用耐久消耗
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                ItemStack stack = componentGrid[x][y];
                if (stack.isEmpty() || isDepleted[x][y]) continue;

                NuclearComponentBehavior behavior = getComponentBehavior(stack);
                if (behavior == null) continue;

                // 应用耐久消耗
                behavior.applyDamage(stack, 1);

                // 检查是否耗尽
                int currentDamage = AbstractMaterialPartBehavior.getPartDamage(stack);
                int maxDurability = behavior.getPartMaxDurability(stack);
                if (currentDamage >= maxDurability) {
                    markAsDepleted(x, y); // 标记内部缓存
                }
            }
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 计算元件散热片对相邻燃料棒的冷却 (优化分配)
     */
    private int coolAdjacentFuelRods(int x, int y, ComponentHeatVentBehavior vent) {
        int coolingRate = vent.getCoolingRate();
        int adjacentFuelRods = 0;

        // 检查四个方向的相邻格子
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (isValidPosition(nx, ny) && isFuelRod(componentGrid[nx][ny]) && !isDepleted[nx][ny]) {
                adjacentFuelRods++;
            }
        }

        if (adjacentFuelRods == 0) return 0;

        // 均匀分配冷却量 (不超过单格100)
        int coolingPerRod = Math.min(100, coolingRate / adjacentFuelRods);
        return coolingPerRod * adjacentFuelRods;
    }

    /**
     * 计算相邻反射板数量
     */
    private int countAdjacentReflectors(int x, int y) {
        int count = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (isValidPosition(nx, ny) &&
                    isNeutronReflector(componentGrid[nx][ny]) &&
                    !isDepleted[nx][ny]) {
                count++;
            }
        }

        return count;
    }

    /**
     * 清空内部缓存数据
     */
    private void clearCaches() {
        // 重置内部缓存
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                reflectorCounts[x][y] = 0;
                lastEnergyOutput[x][y] = 0;
                lastHeatOutput[x][y] = 0;
                // 注意: isDepleted 状态需要保留，不在这里重置
            }
        }

        // 清空位置缓存
        fuelRodPositions.clear();
        heatVentPositions.clear();
        coolantCellPositions.clear();
        reflectorPositions.clear();
    }

    // ==================== 组件类型检查 ====================

    private boolean isFuelRod(ItemStack stack) {
        return getFuelRodBehavior(stack) != null;
    }

    private boolean isHeatVent(ItemStack stack) {
        return getHeatVentBehavior(stack) != null;
    }

    private boolean isComponentHeatVent(ItemStack stack) {
        return getComponentHeatVentBehavior(stack) != null;
    }

    private boolean isCoolantCell(ItemStack stack) {
        return getCoolantCellBehavior(stack) != null;
    }

    private boolean isNeutronReflector(ItemStack stack) {
        return getNeutronReflectorBehavior(stack) != null;
    }

    private boolean isReactorPlating(ItemStack stack) {
        return getPlatingBehavior(stack) != null;
    }

    // ==================== 组件行为获取 ====================

    private FuelRodBehavior getFuelRodBehavior(ItemStack stack) {
        return FuelRodBehavior.getInstanceFor(stack);
    }

    private HeatVentBehavior getHeatVentBehavior(ItemStack stack) {
        return HeatVentBehavior.getInstanceFor(stack);
    }

    private ComponentHeatVentBehavior getComponentHeatVentBehavior(ItemStack stack) {
        return ComponentHeatVentBehavior.getInstanceFor(stack);
    }

    private CoolantCellBehavior getCoolantCellBehavior(ItemStack stack) {
        return CoolantCellBehavior.getInstanceFor(stack);
    }

    private NeutronReflectorBehavior getNeutronReflectorBehavior(ItemStack stack) {
        return NeutronReflectorBehavior.getInstanceFor(stack);
    }

    private ReactorPlatingBehavior getPlatingBehavior(ItemStack stack) {
        return ReactorPlatingBehavior.getInstanceFor(stack);
    }

    private NuclearComponentBehavior getComponentBehavior(ItemStack stack) {
        return NuclearComponentBehavior.getInstanceFor(stack);
    }

    // ==================== 网格操作 ====================

    /**
     * 在指定位置放置组件
     */
    public boolean placeComponent(int x, int y, @Nonnull ItemStack stack) {
        if (!isValidPosition(x, y)) return false;
        if (!componentGrid[x][y].isEmpty()) return false;

        componentGrid[x][y] = stack.copy();
        return true;
    }

    /**
     * 移除指定位置的组件
     */
    public ItemStack removeComponent(int x, int y) {
        if (!isValidPosition(x, y)) return ItemStack.EMPTY;

        ItemStack removed = componentGrid[x][y];
        componentGrid[x][y] = ItemStack.EMPTY;
        return removed;
    }

    /**
     * 获取指定位置的组件
     */
    public ItemStack getComponent(int x, int y) {
        if (!isValidPosition(x, y)) return ItemStack.EMPTY;
        return componentGrid[x][y];
    }

    /**
     * 检查位置是否有效
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < gridWidth && y >= 0 && y < gridHeight;
    }

    // ==================== 缓存操作 ====================

    private void setReflectorCount(int x, int y, int count) {
        // 存储在组件的NBT中
        ItemStack stack = componentGrid[x][y];
        if (!stack.isEmpty()) {
            NBTTagCompound nbt = stack.getOrCreateSubCompound("ReactorData");
            nbt.setInteger("ReflectorCount", count);
        }
    }

    private int getReflectorCount(int x, int y) {
        ItemStack stack = componentGrid[x][y];
        if (stack.isEmpty()) return 0;

        NBTTagCompound nbt = stack.getSubCompound("ReactorData");
        if (nbt == null) return 0;

        return nbt.getInteger("ReflectorCount");
    }

    private void setFuelOutput(int x, int y, int energy, int heat) {
        ItemStack stack = componentGrid[x][y];
        if (!stack.isEmpty()) {
            NBTTagCompound nbt = stack.getOrCreateSubCompound("ReactorData");
            nbt.setInteger("LastEnergyOutput", energy);
            nbt.setInteger("LastHeatOutput", heat);
        }
    }

    private void markAsDepleted(int x, int y) {
        ItemStack stack = componentGrid[x][y];
        if (!stack.isEmpty()) {
            NBTTagCompound nbt = stack.getOrCreateSubCompound("ReactorData");
            nbt.setBoolean("Depleted", true);
        }
    }

    private void clearCellCache(int x, int y) {
        ItemStack stack = componentGrid[x][y];
        if (!stack.isEmpty()) {
            NBTTagCompound nbt = stack.getSubCompound("ReactorData");
            if (nbt != null) {
                nbt.removeTag("ReflectorCount");
                nbt.removeTag("LastEnergyOutput");
                nbt.removeTag("LastHeatOutput");
            }
        }
    }

    // ==================== 统计数据更新 ====================

    private void updateStatistics() {
        totalEnergyProduced += currentOutput;
    }

    // ==================== Getter方法 ====================

    public boolean hasMeltdown() {
        return hasMeltdown;
    }

    public int getHeatPercentage() {
        return maxHeatCapacity > 0 ? (currentHeat * 100) / maxHeatCapacity : 0;
    }

    public float getEfficiency() {
        if (totalFuelRods == 0) return 0.0f;

        // 效率 = 实际输出 / 理论最大输出
        // 这里简化为：反射板越多效率越高
        float baseEfficiency = 1.0f + (totalReflectors * 0.1f);
        return Math.min(baseEfficiency, 2.0f);
    }

    public void setHeat(int currentHeat) {
        this.currentHeat = currentHeat;
    }

    // ==================== NBT序列化 ====================

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("GridWidth", gridWidth);
        nbt.setInteger("GridHeight", gridHeight);
        nbt.setInteger("CurrentHeat", currentHeat);
        nbt.setInteger("MaxHeatCapacity", maxHeatCapacity);
        nbt.setLong("CurrentOutput", currentOutput);
        nbt.setBoolean("IsActive", isActive);
        nbt.setBoolean("HasMeltdown", hasMeltdown);
        nbt.setInteger("TickCount", tickCount);
        nbt.setLong("TotalEnergyProduced", totalEnergyProduced);
        nbt.setLong("TotalHeatProduced", totalHeatProduced);
        nbt.setInteger("MaxHeatReached", maxHeatReached);
        nbt.setFloat("ExplosionResistance", explosionResistance);

        // 保存网格数据
        NBTTagCompound gridNBT = new NBTTagCompound();
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if (!componentGrid[x][y].isEmpty()) {
                    String key = x + "," + y;
                    NBTTagCompound stackNBT = new NBTTagCompound();
                    componentGrid[x][y].writeToNBT(stackNBT);
                    gridNBT.setTag(key, stackNBT);
                }
            }
        }
        nbt.setTag("ComponentGrid", gridNBT);

        // 保存耗尽状态
        NBTTagCompound depletedNBT = new NBTTagCompound();
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if (isDepleted[x][y]) {
                    depletedNBT.setBoolean(x + "," + y, true);
                }
            }
        }
        nbt.setTag("DepletedStates", depletedNBT);

        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        // 基础状态
        currentHeat = nbt.getInteger("CurrentHeat");
        maxHeatCapacity = Math.max(10000, nbt.getInteger("MaxHeatCapacity"));
        currentOutput = nbt.getLong("CurrentOutput");
        isActive = nbt.getBoolean("IsActive");
        hasMeltdown = nbt.getBoolean("HasMeltdown");
        tickCount = nbt.getInteger("TickCount");
        totalEnergyProduced = nbt.getLong("TotalEnergyProduced");
        totalHeatProduced = nbt.getLong("TotalHeatProduced");
        maxHeatReached = nbt.getInteger("MaxHeatReached");
        explosionResistance = nbt.getFloat("ExplosionResistance");

        // 安全处理网格数据
        int savedWidth = nbt.getInteger("GridWidth");
        int savedHeight = nbt.getInteger("GridHeight");

        if (savedWidth != gridWidth || savedHeight != gridHeight) {
            GTLog.logger.warn("Reactor grid size mismatch: {}x{} vs {}x{}. Resetting grid.",
                    savedWidth, savedHeight, gridWidth, gridHeight);
            resetGrid();
            return;
        }

        // 读取网格
        NBTTagCompound gridNBT = nbt.getCompoundTag("ComponentGrid");
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                String key = x + "," + y;
                componentGrid[x][y] = ItemStack.EMPTY;
                if (gridNBT.hasKey(key, TAG_COMPOUND)) {
                    try {
                        NBTTagCompound stackNBT = gridNBT.getCompoundTag(key);
                        ItemStack stack = new ItemStack(stackNBT);
                        if (!stack.isEmpty()) {
                            componentGrid[x][y] = stack;
                        }
                    } catch (Exception e) {
                        GTLog.logger.error("Failed to load component at ({}, {})", x, y, e);
                    }
                }
            }
        }

        // 读取耗尽状态
        clearDepletedStates(); // 先重置
        if (nbt.hasKey("DepletedStates")) {
            NBTTagCompound depletedNBT = nbt.getCompoundTag("DepletedStates");
            for (String key : depletedNBT.getKeySet()) {
                if (depletedNBT.getBoolean(key)) {
                    String[] parts = key.split(",");
                    if (parts.length == 2) {
                        try {
                            int x = Integer.parseInt(parts[0]);
                            int y = Integer.parseInt(parts[1]);
                            if (isValidPosition(x, y)) {
                                isDepleted[x][y] = true;
                            }
                        } catch (NumberFormatException e) {
                            GTLog.logger.error("Invalid depleted state key: {}", key);
                        }
                    }
                }
            }
        }
    }

    private void resetGrid() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                componentGrid[x][y] = ItemStack.EMPTY;
            }
        }
        clearDepletedStates();
    }

    private void clearDepletedStates() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                isDepleted[x][y] = false;
            }
        }
    }
}