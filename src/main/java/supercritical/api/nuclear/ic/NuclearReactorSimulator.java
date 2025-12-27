package supercritical.api.nuclear.ic;


import gregtech.api.unification.material.Material;
import gregtech.api.util.GTLog;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

    private static final String NBT_GRID_WIDTH = "GridWidth";
    private static final String NBT_GRID_HEIGHT = "GridHeight";
    private static final String NBT_COMPONENT_GRID = "ComponentGrid";
    private static final String NBT_CURRENT_HEAT = "CurrentHeat";
    // ==================== 反应堆状态 ====================
    private final ItemStack[][] componentGrid;      // 组件网格 (x * y)
    private final int gridWidth;                    // 网格宽度 (3-9)
    private final int gridHeight;                   // 网格高度 (固定为6)

    private int currentHeat = 0;                    // 当前热量 (HU)
    private int maxHeatCapacity = 10000;            // 最大热容量 (HU)
    private long currentOutput = 0;                 // 当前能量产出 (EU/t)
    private int totalFuelRods = 0;                  // 燃料棒总数
    private int totalHeatVents = 0;                 // 散热片总数
    private int totalCoolantCells = 0;              // 冷却单元总数
    private int totalReflectors = 0;                // 反射板总数
    private int totalPlating = 0;                   // 隔板总数

    // ==================== 统计数据 ====================
    private int tickCount = 0;                      // 运行tick数
    private long totalEnergyProduced = 0;           // 总能量产出
    private long totalHeatProduced = 0;             // 总热量产出
    private int maxHeatReached = 0;                 // 达到过的最高热量
    private boolean isActive = false;               // 是否激活状态

    // ==================== 安全参数 ====================
    private float explosionResistance = 0.0f;       // 爆炸抗性 (0.0-1.0)
    private int overheatingThreshold = 8500;        // 过热阈值 (HU)
    private int meltdownThreshold = 9500;           // 熔毁阈值 (HU)
    private boolean hasMeltdown = false;            // 是否已熔毁

    // ==================== 缓存数据 ====================
    private final List<GridPosition> fuelRodPositions = new ArrayList<>();      // 燃料棒位置缓存
    private final List<GridPosition> heatVentPositions = new ArrayList<>();     // 散热片位置缓存
    private final List<GridPosition> coolantCellPositions = new ArrayList<>();  // 冷却单元位置缓存
    private final List<GridPosition> reflectorPositions = new ArrayList<>();    // 反射板位置缓存
    private final boolean[][] adjacencyCache;                                    // 邻接关系缓存

    // ==================== 构建器 ====================
    public NuclearReactorSimulator(int width, int height) {
        this.gridWidth = Math.max(3, Math.min(9, width));
        this.gridHeight = Math.max(1, Math.min(6, height));
        this.componentGrid = new ItemStack[gridWidth][gridHeight];
        this.adjacencyCache = new boolean[gridWidth][gridHeight];

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
     * 第一阶段：收集组件信息
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

        // 计算邻接关系缓存（用于优化性能）
        calculateAdjacencyCache();
    }

    /**
     * 第二阶段：计算反射板加成
     */
    private void calculateReflectorBonus() {
        // 为每个燃料棒计算相邻反射板数量
        for (GridPosition pos : fuelRodPositions) {
            int reflectorCount = countAdjacentReflectors(pos.x, pos.y);
            setReflectorCount(pos.x, pos.y, reflectorCount);
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

            int reflectorCount = getReflectorCount(pos.x, pos.y);

            // 计算基础产出
            int energyOutput = fuelRod.getEnergyOutput();
            int heatOutput = fuelRod.getHeatOutput();

            // 反射板加成：每个反射板增加20%效率
            float reflectorBonus = 1.0f + (reflectorCount * 0.2f);

            // 最终产出
            int finalEnergy = (int)(energyOutput * reflectorBonus);
            int finalHeat = (int)(heatOutput * reflectorBonus);

            totalEnergy += finalEnergy;
            totalHeat += finalHeat;

            // 标记该燃料棒已贡献产出
            setFuelOutput(pos.x, pos.y, finalEnergy, finalHeat);
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
                    // 元件散热片：冷却相邻的燃料棒
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
        // 检查过热
        if (currentHeat > overheatingThreshold) {
            // 过热警告：增加熔毁概率
            float overheatRatio = (float)(currentHeat - overheatingThreshold) /
                    (meltdownThreshold - overheatingThreshold);

            // 基础熔毁概率：每tick 0.1% * 过热比例
            float meltdownChance = 0.001f * overheatRatio;

            // 随机检查是否熔毁
            if (Math.random() < meltdownChance) {
                hasMeltdown = true;
            }
        }

        // 检查熔毁
        if (currentHeat >= meltdownThreshold) {
            hasMeltdown = true;
        }
    }

    /**
     * 第八阶段：更新组件耐久
     */
    private void updateComponentDurability() {
        // 遍历所有组件并应用耐久消耗
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                ItemStack stack = componentGrid[x][y];
                if (stack.isEmpty()) continue;

                NuclearComponentBehavior behavior = getComponentBehavior(stack);
                if (behavior != null) {
                    // 应用耐久消耗
                    behavior.applyDamage(stack, 1);

                    // 检查是否耗尽
                    if (behavior.getPartDamage(stack) >= behavior.getPartMaxDurability(stack)) {
                        // 标记为耗尽（但不移除，保留0耐久）
                        markAsDepleted(x, y);
                    }
                }
            }
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 计算元件散热片对相邻燃料棒的冷却
     */
    private int coolAdjacentFuelRods(int x, int y, ComponentHeatVentBehavior vent) {
        int totalCooling = 0;
        int coolingRate = vent.getCoolingRate();

        // 检查四个方向的相邻格子
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (isValidPosition(nx, ny) && isFuelRod(componentGrid[nx][ny])) {
                // 冷却相邻燃料棒
                totalCooling += Math.min(coolingRate / 4, 100); // 平均分配冷却量
            }
        }

        return Math.min(coolingRate, totalCooling);
    }

    /**
     * 计算相邻反射板数量
     */
    private int countAdjacentReflectors(int x, int y) {
        int count = 0;

        // 检查四个方向的相邻格子
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (isValidPosition(nx, ny) && isNeutronReflector(componentGrid[nx][ny])) {
                count++;
            }
        }

        return count;
    }

    /**
     * 计算邻接关系缓存
     */
    private void calculateAdjacencyCache() {
        // 清空缓存
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                adjacencyCache[x][y] = false;
            }
        }

        // 标记所有有相邻燃料棒的格子
        for (GridPosition pos : fuelRodPositions) {
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : directions) {
                int nx = pos.x + dir[0];
                int ny = pos.y + dir[1];
                if (isValidPosition(nx, ny)) {
                    adjacencyCache[nx][ny] = true;
                }
            }
        }
    }

    /**
     * 清空缓存数据
     */
    private void clearCaches() {
        // 清空燃料棒产出缓存
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                clearCellCache(x, y);
            }
        }
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

    public int getGridWidth() { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
    public int getCurrentHeat() { return currentHeat; }
    public int getMaxHeatCapacity() { return maxHeatCapacity; }
    public long getCurrentOutput() { return currentOutput; }
    public boolean isActive() { return isActive; }
    public boolean hasMeltdown() { return hasMeltdown; }

    public int getTotalFuelRods() { return totalFuelRods; }
    public int getTotalHeatVents() { return totalHeatVents; }
    public int getTotalCoolantCells() { return totalCoolantCells; }
    public int getTotalReflectors() { return totalReflectors; }
    public int getTotalPlating() { return totalPlating; }

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

    // ==================== 网格位置内部类 ====================

    private static class GridPosition {
        public final int x;
        public final int y;

        public GridPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
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

        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        // 基础状态
        currentHeat = nbt.getInteger("CurrentHeat");
        maxHeatCapacity = Math.max(10000, nbt.getInteger("MaxHeatCapacity")); // 防止无效值
        currentOutput = nbt.getLong("CurrentOutput");
        isActive = nbt.getBoolean("IsActive");
        hasMeltdown = nbt.getBoolean("HasMeltdown");
        tickCount = nbt.getInteger("TickCount");
        // ... [其他状态] ...

        // ==================== 安全处理网格数据 ====================
        NBTTagCompound gridNBT = nbt.getCompoundTag("ComponentGrid");

        // 严格验证网格尺寸
        int savedWidth = nbt.getInteger("GridWidth");
        int savedHeight = nbt.getInteger("GridHeight");

        if (savedWidth != gridWidth || savedHeight != gridHeight) {
            GTLog.logger.warn("Reactor grid size mismatch: {}x{} vs {}x{}",
                    savedWidth, savedHeight, gridWidth, gridHeight);
            // 初始化为空网格
            for (int x = 0; x < gridWidth; x++) {
                for (int y = 0; y < gridHeight; y++) {
                    componentGrid[x][y] = ItemStack.EMPTY;
                }
            }
            return;
        }

        // 安全读取每个单元格
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                String key = x + "," + y;
                if (gridNBT.hasKey(key, TAG_COMPOUND)) {
                    try {
                        NBTTagCompound stackNBT = gridNBT.getCompoundTag(key);
                        ItemStack stack = new ItemStack(stackNBT);

                        // 额外验证物品有效性
                        if (stack.isEmpty() || !stack.hasTagCompound()) {
                            componentGrid[x][y] = ItemStack.EMPTY;
                        } else {
                            componentGrid[x][y] = stack;
                        }
                    } catch (Exception e) {
                        GTLog.logger.error("Failed to load component at ({}, {})", x, y, e);
                        componentGrid[x][y] = ItemStack.EMPTY;
                    }
                } else {
                    componentGrid[x][y] = ItemStack.EMPTY;
                }
            }
        }
    }
}