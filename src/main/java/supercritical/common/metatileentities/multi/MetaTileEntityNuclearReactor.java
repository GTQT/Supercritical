package supercritical.common.metatileentities.multi;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.FloatSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MetaTileEntityBaseWithControl;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ProgressBarMultiblock;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIFactory;
import gregtech.api.metatileentity.multiblock.ui.TemplateBarBuilder;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuis;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.GTLog;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.behaviors.AbstractMaterialPartBehavior;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import supercritical.api.nuclear.ic.NuclearReactorSimulator;
import supercritical.common.item.behaviors.NuclearComponentBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

public class MetaTileEntityNuclearReactor extends MetaTileEntityBaseWithControl implements ProgressBarMultiblock {

    // ==================== 核反应堆参数 ====================
    private static final int GRID_SIZE = 5;  // 5x5网格
    private static final int UPDATE_TICK_RATE = 20; // 20 ticks = 1秒更新一次
    private static final int BASE_HEAT_CAPACITY = 10000; // 基础热容量

    // ==================== 核心组件 ====================
    private NuclearReactorSimulator reactorSimulator;
    private final GTItemStackHandler componentHandler;

    // ==================== 状态变量 ====================
    private int tickCounter = 0;
    private int updateTimer = 0;
    private boolean isReactorActive = false;
    private boolean hasMeltdown = false;

    // ==================== 缓存数据 ====================
    private int currentHeat = 0;
    private int maxHeatCapacity = BASE_HEAT_CAPACITY;
    private long currentOutput = 0;
    private float efficiency = 1.0f;

    public MetaTileEntityNuclearReactor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);

        // 初始化核反应堆模拟器 (5x5网格)
        this.reactorSimulator = new NuclearReactorSimulator(GRID_SIZE, GRID_SIZE);

        // 初始化组件处理器 (25个槽位，每个槽位只能放1个核电组件)
        this.componentHandler = new GTItemStackHandler(this, GRID_SIZE * GRID_SIZE) {
            @Override
            public int getSlotLimit(int slot) {
                return 1; // 每个槽位只能放1个物品
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                // 只允许核电组件
                return NuclearComponentBehavior.getInstanceFor(stack) != null;
            }

            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                if (!getWorld().isRemote && isStructureFormed()) {
                    // 当物品变化时，标记需要同步到模拟器
                    markDirty();
                }
            }
        };
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityNuclearReactor(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {
        if (getWorld().isRemote) return;

        tickCounter++;
        updateTimer++;

        // 每秒更新一次（20 ticks）
        if (updateTimer >= UPDATE_TICK_RATE) {
            updateTimer = 0;

            // 1. 同步库存到模拟器
            syncInventoryToSimulator();

            // 2. 运行反应堆模拟
            boolean success = reactorSimulator.simulateTick();

            if (!success) {
                // 发生熔毁
                hasMeltdown = true;
                handleMeltdown();
                return;
            }

            // 3. 更新本地缓存
            updateLocalCache();

            // 4. 输出能量
            outputEnergy();

            // 5. 同步模拟器数据回库存
            syncSimulatorToInventory();

            // 6. 标记需要同步到客户端
            markDirty();
            writeCustomData(100, buf -> {
                buf.writeInt(currentHeat);
                buf.writeInt(maxHeatCapacity);
                buf.writeLong(currentOutput);
                buf.writeBoolean(isReactorActive);
                buf.writeBoolean(hasMeltdown);
            });
        }
    }

    // ==================== 核心方法 ====================

    /**
     * 同步库存到模拟器
     */
    private void syncInventoryToSimulator() {
        for (int slot = 0; slot < componentHandler.getSlots(); slot++) {
            int x = slot % GRID_SIZE;
            int y = slot / GRID_SIZE;

            ItemStack stack = componentHandler.getStackInSlot(slot);

            if (stack.isEmpty()) {
                // 如果模拟器中有物品但库存为空，则移除
                reactorSimulator.removeComponent(x, y);
            } else {
                // 如果库存有物品，放置到模拟器
                reactorSimulator.placeComponent(x, y, stack);
            }
        }
    }

    /**
     * 同步模拟器数据回库存
     */
    private void syncSimulatorToInventory() {
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                int slot = y * GRID_SIZE + x;
                ItemStack simulatorStack = reactorSimulator.getComponent(x, y);

                // 更新库存中的物品状态（只更新耐久变化）
                if (!simulatorStack.isEmpty()) {
                    ItemStack currentStack = componentHandler.getStackInSlot(slot);
                    if (!currentStack.isEmpty() && currentStack.isItemEqual(simulatorStack)) {
                        // 复制NBT数据（主要是耐久）
                        currentStack.setTagCompound(simulatorStack.getTagCompound());
                    }
                }
            }
        }
    }

    /**
     * 更新本地缓存数据
     */
    private void updateLocalCache() {
        currentHeat = reactorSimulator.getCurrentHeat();
        maxHeatCapacity = reactorSimulator.getMaxHeatCapacity();
        currentOutput = reactorSimulator.getCurrentOutput();
        isReactorActive = reactorSimulator.isActive() && !hasMeltdown;
        efficiency = reactorSimulator.getEfficiency();
    }

    /**
     * 输出能量
     */
    private void outputEnergy() {
        if (currentOutput > 0 && !hasMeltdown) {
            // 转换为每tick能量（当前输出是每20tick的能量）
            long energyPerTick = currentOutput * UPDATE_TICK_RATE;

            // 添加到能量容器
            outEnergyContainer.addEnergy(energyPerTick);
        }
    }

    /**
     * 处理熔毁
     */
    private void handleMeltdown() {
        // 1. 停止所有输出
        isReactorActive = false;
        currentOutput = 0;

        // 2. 清空所有组件（熔毁后组件全部消失）
        for (int slot = 0; slot < componentHandler.getSlots(); slot++) {
            componentHandler.setStackInSlot(slot, ItemStack.EMPTY);
        }

        // 3. 创建爆炸
        float explosionPower = calculateExplosionPower();
        doExplosion(explosionPower);

        // 4. 同步到模拟器
        syncInventoryToSimulator();
    }

    /**
     * 计算爆炸威力
     */
    private float calculateExplosionPower() {
        float basePower = 4.0f;

        // 根据热量和燃料棒数量增加威力
        int heat = reactorSimulator.getCurrentHeat();
        int fuelRods = reactorSimulator.getTotalFuelRods();

        float heatFactor = Math.min(heat / 10000.0f, 2.0f);
        float fuelFactor = Math.min(fuelRods / 5.0f, 2.0f);

        return basePower * (1.0f + heatFactor + fuelFactor);
    }

    // ==================== NBT数据持久化 ====================

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        // 保存库存
        data.setTag("ComponentInventory", componentHandler.serializeNBT());

        // 保存模拟器状态
        NBTTagCompound simulatorNBT = new NBTTagCompound();
        if (reactorSimulator != null) {
            reactorSimulator.writeToNBT(simulatorNBT);
            data.setTag("ReactorSimulator", simulatorNBT);
        }

        // 保存其他状态
        data.setBoolean("HasMeltdown", hasMeltdown);
        data.setInteger("TickCounter", tickCounter);
        data.setInteger("UpdateTimer", updateTimer);
        data.setInteger("CurrentHeat", currentHeat);
        data.setInteger("MaxHeatCapacity", maxHeatCapacity);
        data.setLong("CurrentOutput", currentOutput);
        data.setFloat("Efficiency", efficiency);
        data.setBoolean("IsReactorActive", isReactorActive);

        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        // ==================== 首先确保关键对象初始化 ====================
        // 立即初始化模拟器，防止NBT加载时出现空指针
        if (reactorSimulator == null) {
            reactorSimulator = new NuclearReactorSimulator(GRID_SIZE, GRID_SIZE);
            GTLog.logger.warn("Reactor simulator was null during NBT load at {}", getPos());
        }

        // ==================== 读取库存 ====================
        if (data.hasKey("ComponentInventory")) {
            componentHandler.deserializeNBT(data.getCompoundTag("ComponentInventory"));
        }

        // ==================== 读取基本状态 ====================
        hasMeltdown = data.getBoolean("HasMeltdown");
        tickCounter = data.getInteger("TickCounter");
        updateTimer = data.getInteger("UpdateTimer");
        currentHeat = data.getInteger("CurrentHeat");
        maxHeatCapacity = data.getInteger("MaxHeatCapacity");
        currentOutput = data.getLong("CurrentOutput");
        efficiency = data.getFloat("Efficiency");
        isReactorActive = data.getBoolean("IsReactorActive");

        // ==================== 读取模拟器状态 (增强防御) ====================
        if (data.hasKey("ReactorSimulator",  TAG_COMPOUND)) {
            NBTTagCompound simulatorNBT = data.getCompoundTag("ReactorSimulator");

            // 严格验证 NBT 结构
            if (simulatorNBT != null && simulatorNBT.hasKey("ComponentGrid", TAG_COMPOUND)) {
                try {
                    reactorSimulator.readFromNBT(simulatorNBT);
                } catch (Exception e) {
                    // 详细记录错误信息
                    GTLog.logger.fatal("Reactor simulator NBT corrupted at {} - Resetting to safe state", getPos(), e);
                    resetSimulatorToSafeState();
                }
            } else {
                GTLog.logger.warn("Incomplete reactor simulator NBT at {}", getPos());
                resetSimulatorToSafeState();
            }
        } else {
            GTLog.logger.info("No reactor simulator data found at {}", getPos());
            resetSimulatorToSafeState();
        }

        // ==================== 同步状态 ====================
        if (getWorld() != null && !getWorld().isRemote) {
            syncInventoryToSimulator();
            updateLocalCache();
        }
    }

    // 重置模拟器到安全状态
    private void resetSimulatorToSafeState() {
        reactorSimulator = new NuclearReactorSimulator(GRID_SIZE, GRID_SIZE);
        reactorSimulator.setHeat(currentHeat); // 保留当前热量
    }

    // ==================== 网络同步 ====================

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);

        buf.writeInt(currentHeat);
        buf.writeInt(maxHeatCapacity);
        buf.writeLong(currentOutput);
        buf.writeBoolean(isReactorActive);
        buf.writeBoolean(hasMeltdown);

        // 写入库存数据
        for (int slot = 0; slot < componentHandler.getSlots(); slot++) {
            buf.writeItemStack(componentHandler.getStackInSlot(slot));
        }
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);

        currentHeat = buf.readInt();
        maxHeatCapacity = buf.readInt();
        currentOutput = buf.readLong();
        isReactorActive = buf.readBoolean();
        hasMeltdown = buf.readBoolean();

        // 读取库存数据
        for (int slot = 0; slot < componentHandler.getSlots(); slot++) {
            try {
                componentHandler.setStackInSlot(slot, buf.readItemStack());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);

        if (dataId == 100) {
            currentHeat = buf.readInt();
            maxHeatCapacity = buf.readInt();
            currentOutput = buf.readLong();
            isReactorActive = buf.readBoolean();
            hasMeltdown = buf.readBoolean();
        }
    }

    @Override
    protected MultiblockUIFactory createUIFactory() {
        return super.createUIFactory()
                // 添加侧边按钮用于打开组件管理面板
                .createFlexButton((guiData, syncManager) -> {
                    var componentPanel = syncManager.panel("component_panel", this::makeComponentPanel, true);
                    return new ButtonWidget<>()
                            .size(18)
                            .overlay(GTGuiTextures.FILTER_SETTINGS_OVERLAY.asIcon().size(16))
                            .addTooltipLine(IKey.str("§7核电组件管理"))
                            .onMousePressed(i -> {
                                if (componentPanel.isPanelOpen()) {
                                    componentPanel.closePanel();
                                } else {
                                    componentPanel.openPanel();
                                }
                                return true;
                            });
                });
    }

    /**
     * 创建组件管理面板
     */
    private ModularPanel makeComponentPanel(PanelSyncManager syncManager, IPanelHandler syncHandler) {
        int gridSize = GRID_SIZE;

        // 注册槽位组
        syncManager.registerSlotGroup("reactor_inventory", gridSize);

        // 计算面板大小
        int panelHeight = 4 + 20 + (gridSize * 18) + 4;
        int panelWidth = 4 + (gridSize * 18) + 4;

        return GTGuis.createPopupPanel("nuclear_components", panelWidth, panelHeight)
                // 标题栏
                .child(Flow.row()
                        .pos(4, 4)
                        .height(16)
                        .coverChildrenWidth()
                        .child(new ItemDrawable(getStackForm())
                                .asWidget()
                                .size(16)
                                .marginRight(4))
                        .child(IKey.str("核电组件 (" + gridSize + "×" + gridSize + ")")
                                .asWidget()
                                .heightRel(1.0f)))
                // 组件网格
                .child(Flow.column()
                        .top(24)
                        .left(4)
                        .width(gridSize * 18)
                        .height(gridSize * 18)
                        .child(new Grid()
                                .minElementMargin(0, 0)
                                .minColWidth(18).minRowHeight(18)
                                .alignX(0.5f)
                                .mapTo(gridSize, gridSize * gridSize, index -> {
                                    return new ItemSlot()
                                            .slot(SyncHandlers.itemSlot(componentHandler, index)
                                                    .slotGroup("reactor_inventory")
                                                    .changeListener((newItem, onlyAmountChanged, client, init) -> {
                                                        if (onlyAmountChanged &&
                                                                componentHandler instanceof GTItemStackHandler) {
                                                            componentHandler.onContentsChanged(index);
                                                        }

                                                        if (!client && isStructureFormed()) {
                                                            syncInventoryToSimulator();
                                                            markDirty();
                                                        }
                                                    })
                                                    .accessibility(true, true));
                                })));
    }

    @Override
    public int getProgressBarCount() {
        return 2; // 两个进度条：热量和效率
    }

    @Override
    public void registerBars(List<UnaryOperator<TemplateBarBuilder>> bars, PanelSyncManager syncManager) {
        // 同步数据
        IntSyncValue heatValue = new IntSyncValue(() -> currentHeat);
        IntSyncValue maxHeatValue = new IntSyncValue(() -> maxHeatCapacity);
        FloatSyncValue efficiencyValue = new FloatSyncValue(() -> efficiency);
        IntSyncValue fuelRodsValue = new IntSyncValue(() -> reactorSimulator.getTotalFuelRods());
        IntSyncValue heatVentsValue = new IntSyncValue(() -> reactorSimulator.getTotalHeatVents());
        IntSyncValue coolantCellsValue = new IntSyncValue(() -> reactorSimulator.getTotalCoolantCells());
        IntSyncValue reflectorsValue = new IntSyncValue(() -> reactorSimulator.getTotalReflectors());

        syncManager.syncValue("heat", heatValue);
        syncManager.syncValue("max_heat", maxHeatValue);
        syncManager.syncValue("efficiency", efficiencyValue);
        syncManager.syncValue("fuel_rods", fuelRodsValue);
        syncManager.syncValue("heat_vents", heatVentsValue);
        syncManager.syncValue("coolant_cells", coolantCellsValue);
        syncManager.syncValue("reflectors", reflectorsValue);

        // 第一个进度条：热量百分比
        bars.add(barBuilder -> barBuilder
                .progress(() -> maxHeatValue.getIntValue() > 0 ?
                        Math.min(1.0, (double) heatValue.getIntValue() / maxHeatValue.getIntValue()) : 0.0)
                .texture(GTGuiTextures.PROGRESS_BAR_FUSION_HEAT) // 使用融合反应堆的热量纹理
                .tooltipBuilder(tooltip -> {
                    if (isStructureFormed()) {
                        int heat = heatValue.getIntValue();
                        int maxHeat = maxHeatValue.getIntValue();
                        int heatPercent = maxHeat > 0 ? (heat * 100) / maxHeat : 0;

                        // 根据热量等级设置颜色
                        TextFormatting color;
                        String status;
                        if (heatPercent >= 90) {
                            color = TextFormatting.RED;
                            status = "危险";
                        } else if (heatPercent >= 70) {
                            color = TextFormatting.YELLOW;
                            status = "警告";
                        } else if (heatPercent >= 40) {
                            color = TextFormatting.GOLD;
                            status = "正常";
                        } else {
                            color = TextFormatting.GREEN;
                            status = "安全";
                        }

                        tooltip.addLine(IKey.str(color + "热量: " + heat + " / " + maxHeat + " HU (" + heatPercent + "%)"));
                        tooltip.addLine(IKey.str(color + "状态: " + status));

                        if (heatPercent >= 85) {
                            tooltip.addLine(IKey.str(TextFormatting.RED + "警告: 接近熔毁阈值!"));
                        }
                    } else {
                        tooltip.addLine(IKey.str(TextFormatting.RED + "结构不完整"));
                    }
                }));

        // 第二个进度条：效率
        bars.add(barBuilder -> barBuilder
                .progress(() -> Math.min(1.0, efficiencyValue.getFloatValue() / 2.0)) // 效率范围0-2，转换为0-1
                .texture(GTGuiTextures.PROGRESS_BAR_FUSION_ENERGY) // 使用箭头纹理表示效率
                .tooltipBuilder(tooltip -> {
                    if (isStructureFormed()) {
                        float eff = efficiencyValue.getFloatValue();
                        int effPercent = (int) (eff * 100);

                        tooltip.addLine(IKey.str("效率: " + effPercent + "%"));
                        tooltip.addLine(IKey.str("基础输出: " + currentOutput + " EU/t"));

                        // 效率影响因素
                        if (reflectorsValue.getIntValue() > 0) {
                            tooltip.addLine(IKey.str("反射板加成: +" + (reflectorsValue.getIntValue() * 20) + "%"));
                        }
                        if (fuelRodsValue.getIntValue() > 1) {
                            tooltip.addLine(IKey.str("燃料棒邻接加成"));
                        }
                    } else {
                        tooltip.addLine(IKey.str(TextFormatting.RED + "结构不完整"));
                    }
                }));
    }

    // ==================== 配置显示文本 ====================

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder
                .setWorkingStatus(isReactorActive && !hasMeltdown, isReactorActive && !hasMeltdown)
                .addEnergyUsageLine(getEnergyContainer())
                .addCustom((richText, syncer) -> {
                    if (!isStructureFormed()) return;

                    // 同步数据
                    int heat = syncer.syncInt(currentHeat);
                    int maxHeat = syncer.syncInt(maxHeatCapacity);
                    long output = syncer.syncLong(currentOutput);
                    int fuelRods = syncer.syncInt(reactorSimulator.getTotalFuelRods());
                    int heatVents = syncer.syncInt(reactorSimulator.getTotalHeatVents());
                    int coolantCells = syncer.syncInt(reactorSimulator.getTotalCoolantCells());
                    int reflectors = syncer.syncInt(reactorSimulator.getTotalReflectors());
                    int plating = syncer.syncInt(reactorSimulator.getTotalPlating());
                    boolean meltdown = syncer.syncBoolean(hasMeltdown);
                    boolean active = syncer.syncBoolean(isReactorActive);

                    // 添加状态行
                    if (meltdown) {
                        richText.add(IKey.str(TextFormatting.DARK_RED + "✗ 反应堆熔毁"));
                    } else if (active) {
                        richText.add(IKey.str(TextFormatting.GREEN + "✓ 运行中"));
                    } else {
                        richText.add(IKey.str(TextFormatting.GRAY + "○ 待机"));
                    }

                    // 添加组件统计
                    richText.add(IKey.str(TextFormatting.GRAY + "燃料棒: " +
                            TextFormatting.WHITE + fuelRods +
                            TextFormatting.GRAY + "  散热片: " +
                            TextFormatting.WHITE + heatVents));

                    richText.add(IKey.str(TextFormatting.GRAY + "冷却单元: " +
                            TextFormatting.WHITE + coolantCells +
                            TextFormatting.GRAY + "  反射板: " +
                            TextFormatting.WHITE + reflectors));

                    // 添加隔板统计
                    if (plating > 0) {
                        int heatBoost = maxHeat - BASE_HEAT_CAPACITY;
                        richText.add(IKey.str(TextFormatting.GRAY + "隔板: " +
                                TextFormatting.WHITE + plating +
                                TextFormatting.GRAY + " (+" + heatBoost + " HU)"));
                    }

                    // 添加热量信息
                    int heatPercent = maxHeat > 0 ? (heat * 100) / maxHeat : 0;
                    richText.add(IKey.str(TextFormatting.GRAY + "热量: " +
                            TextFormatting.WHITE + heat + "/" + maxHeat + " HU" +
                            TextFormatting.GRAY + " (" + heatPercent + "%)"));

                    // 添加输出信息
                    richText.add(IKey.str(TextFormatting.GRAY + "输出: " +
                            TextFormatting.WHITE + output + " EU/t"));
                })
                .addWorkingStatusLine();
    }

    @Override
    protected void configureErrorText(MultiblockUIBuilder builder) {
        super.configureErrorText(builder);
        builder.addCustom((list, syncer) -> {
            if (isStructureFormed()) {
                // 同步熔毁状态
                boolean meltdown = syncer.syncBoolean(hasMeltdown);

                // 熔毁状态显示为错误
                if (meltdown) {
                    list.add(IKey.str("§4✗ 反应堆已熔毁！"));
                }
            }
        });
    }

    @Override
    protected void configureWarningText(MultiblockUIBuilder builder) {
        // 先添加父类的警告
        super.configureWarningText(builder);

        builder.addCustom((list, syncer) -> {
            if (isStructureFormed()) {
                // 同步数据
                int heat = syncer.syncInt(currentHeat);
                int maxHeat = syncer.syncInt(maxHeatCapacity);
                long output = syncer.syncLong(currentOutput);
                int fuelRods = syncer.syncInt(reactorSimulator.getTotalFuelRods());
                int heatVents = syncer.syncInt(reactorSimulator.getTotalHeatVents());
                int coolantCells = syncer.syncInt(reactorSimulator.getTotalCoolantCells());

                // 检查热量警告
                int heatPercent = maxHeat > 0 ? (heat * 100) / maxHeat : 0;

                if (heatPercent >= 90) {
                    list.add(IKey.str("§c⚠ 热量危险：" + heatPercent + "% - 即将熔毁！"));
                } else if (heatPercent >= 80) {
                    list.add(IKey.str("§6⚠ 热量警告：" + heatPercent + "% - 请立即冷却！"));
                } else if (heatPercent >= 70) {
                    list.add(IKey.str("§e⚠ 热量偏高：" + heatPercent + "%"));
                }

                // 检查组件耗尽
                int depletedComponents = syncer.syncInt(countDepletedComponents());
                if (depletedComponents > 0) {
                    list.add(IKey.str("§e⚠ " + depletedComponents + "个组件已耗尽"));
                }

                // 检查散热不足
                boolean noCooling = syncer.syncBoolean(
                        heat > 1000 && heatVents == 0 && coolantCells == 0);

                if (noCooling) {
                    list.add(IKey.str("§e⚠ 未检测到冷却系统"));
                }

                // 检查燃料不足但反应堆试图运行
                boolean noFuel = syncer.syncBoolean(
                        fuelRods == 0 && output > 0);

                if (noFuel) {
                    list.add(IKey.str("§e⚠ 未检测到燃料棒"));
                }
            }
        });
    }

    // 辅助方法：计算耗尽组件数量
    private int countDepletedComponents() {
        int count = 0;
        for (int slot = 0; slot < componentHandler.getSlots(); slot++) {
            ItemStack stack = componentHandler.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                NuclearComponentBehavior behavior = NuclearComponentBehavior.getInstanceFor(stack);
                if (behavior != null) {
                    int damage = AbstractMaterialPartBehavior.getPartDamage(stack);
                    int maxDurability = behavior.getPartMaxDurability(stack);
                    if (damage >= maxDurability) {
                        count++;
                    }
                }
            }
        }
        return count;
    }


    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("YYY", "YYY", "YYY")
                .aisle("YYY", "YYY", "YYY")
                .aisle("YYY", "YSY", "YYY")
                .where('S', selfPredicate())
                .where('Y', states(getCasingState())
                        .or(abilities(MultiblockAbility.OUTPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(3))
                )
                .where(' ', any())
                .build();
    }

    private IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.FROST_PROOF_CASING;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), true,
                isStructureFormed());
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected ICubeRenderer getFrontOverlay() {
        // 可以创建一个专门的核电反应堆覆盖层
        return Textures.LARGE_GAS_TURBINE_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    public List<ITextComponent> getDataInfo() {
        List<ITextComponent> list = new ArrayList<>();

        if (isStructureFormed()) {
            // 添加反应堆状态信息
            list.add(new TextComponentString("热量: " + currentHeat + " / " + maxHeatCapacity + " HU"));
            list.add(new TextComponentString("能量输出: " + currentOutput + " EU/t"));
            list.add(new TextComponentString("效率: " + String.format("%.1f", efficiency * 100) + "%"));

            if (hasMeltdown) {
                list.add(new TextComponentString(TextFormatting.RED + "熔毁状态！"));
            } else if (isReactorActive) {
                list.add(new TextComponentString(TextFormatting.GREEN + "运行中"));
            } else {
                list.add(new TextComponentString(TextFormatting.GRAY + "待机"));
            }

            // 组件统计
            list.add(new TextComponentString("燃料棒: " + reactorSimulator.getTotalFuelRods()));
            list.add(new TextComponentString("散热片: " + reactorSimulator.getTotalHeatVents()));
            list.add(new TextComponentString("冷却单元: " + reactorSimulator.getTotalCoolantCells()));
            list.add(new TextComponentString("反射板: " + reactorSimulator.getTotalReflectors()));
        }

        return list;
    }

    // 进度条相关（用于显示热量百分比）
    @Override
    public int getProgress() {
        if (maxHeatCapacity > 0) {
            return (currentHeat * 100) / maxHeatCapacity;
        }
        return 0;
    }

    @Override
    public int getMaxProgress() {
        return 100; // 百分比
    }

    // ==================== 工具方法 ====================

    /**
     * 获取组件处理器（用于外部访问）
     */
    public GTItemStackHandler getComponentHandler() {
        return componentHandler;
    }

    /**
     * 获取反应堆模拟器（用于GUI数据）
     */
    public NuclearReactorSimulator getReactorSimulator() {
        return reactorSimulator;
    }

    /**
     * 获取当前热量
     */
    public int getCurrentHeat() {
        return currentHeat;
    }

    /**
     * 获取最大热容量
     */
    public int getMaxHeatCapacity() {
        return maxHeatCapacity;
    }

    /**
     * 获取当前能量输出
     */
    public long getCurrentOutput() {
        return currentOutput;
    }

    /**
     * 检查是否熔毁
     */
    public boolean hasMeltdown() {
        return hasMeltdown;
    }

    /**
     * 检查反应堆是否激活
     */
    public boolean isReactorActive() {
        return isReactorActive && !hasMeltdown;
    }
}