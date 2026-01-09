package supercritical.common.metatileentities.multi.nuclearReactor;

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
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.GTLog;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.items.behaviors.AbstractMaterialPartBehavior;
import gregtech.core.sound.GTSoundEvents;
import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import supercritical.api.capability.INuclearExtend;
import supercritical.api.metatileentity.multiblock.SCMultiblockAbility;
import supercritical.api.nuclear.ic.NuclearReactorSimulator;
import supercritical.client.renderer.textures.SCTextures;
import supercritical.common.blocks.BlockNuclearReactorCasing;
import supercritical.common.blocks.SCMetaBlocks;
import supercritical.common.item.behaviors.NuclearComponentBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;
import static supercritical.SCValues.SYNC_REACTOR_STATE;
import static supercritical.common.metatileentities.multi.nuclearReactor.NuclearAbility.STOP_WORK;

public class MetaTileEntityNuclearReactor extends MetaTileEntityBaseWithControl implements ProgressBarMultiblock {

    private static final int UPDATE_TICK_RATE = 20; // 20 ticks = 1秒更新一次
    private static final int BASE_HEAT_CAPACITY = 10000; // 基础热容量

    // ==================== 核反应堆参数 ====================
    @Getter
    private final int reactorWidth = 9;
    @Getter
    private final int reactorHeight = 6;
    @Getter
    private int extendCount = 0;

    // ==================== 核心组件 ====================
    @Getter
    private NuclearReactorSimulator reactorSimulator;
    @Getter
    private GTItemStackHandler componentHandler;

    // ==================== 状态变量 ====================
    private int tickCounter = 0;
    private int updateTimer = 0;
    private boolean isReactorActive = false;
    private boolean hasMeltdown = false;

    // ==================== 缓存数据 ====================
    @Getter
    private int currentHeat = 0;
    @Getter
    private int maxHeatCapacity = BASE_HEAT_CAPACITY;
    @Getter
    private long currentOutput = 0;
    private float efficiency = 1.0f;

    public MetaTileEntityNuclearReactor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        initializeReactor(reactorWidth, reactorHeight);
    }

    private void initializeReactor(int width, int height) {
        this.reactorSimulator = new NuclearReactorSimulator(width, height);
        this.componentHandler = createComponentHandler(width, height);
    }

    private GTItemStackHandler createComponentHandler(int width, int height) {
        return new GTItemStackHandler(this, width * height) {
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

    public List<INuclearExtend> getExtendHatch() {
        List<INuclearExtend> abilities = getAbilities(SCMultiblockAbility.REACTOR_EXTEND_HATCH);
        return abilities.isEmpty() ? null : abilities;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);

        // 获取扩展仓数量
        List<INuclearExtend> extendHatches = getExtendHatch();
        if(getExtendHatch()!=null) extendCount = extendHatches.size();
    }

    public boolean haveAbilities(NuclearAbility ability){
        if(extendCount > 0){
            for(INuclearExtend extend:getExtendHatch()){
                if(extend.getUpdateAbilities().contains(ability))return true;
            }
        }
        return false;
    }

    private ItemStack[] saveInventory() {
        ItemStack[] items = new ItemStack[componentHandler.getSlots()];
        for (int i = 0; i < items.length; i++) {
            items[i] = componentHandler.getStackInSlot(i).copy();
        }
        return items;
    }

    private void restoreInventory(ItemStack[] savedItems) {
        if (savedItems == null) return;

        for (int i = 0; i < Math.min(savedItems.length, componentHandler.getSlots()); i++) {
            if (savedItems[i] != null && !savedItems[i].isEmpty()) {
                componentHandler.setStackInSlot(i, savedItems[i].copy());
            }
        }
    }

    @Override
    protected void updateFormedValid() {
        if (getWorld().isRemote) return;
        if (!isWorkingEnabled()) return;

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

            // 4. 同步模拟器数据回库存
            syncSimulatorToInventory();

            // 5. 标记需要同步到客户端
            markDirty();
            writeCustomData(SYNC_REACTOR_STATE, buf -> {
                buf.writeInt(currentHeat);
                buf.writeInt(maxHeatCapacity);
                buf.writeLong(currentOutput);
                buf.writeBoolean(isReactorActive);
                buf.writeBoolean(hasMeltdown);
            });
        }
        // 6. 输出能量
        outputEnergy();

        if(haveAbilities(STOP_WORK) && reactorSimulator.isOverHeat())
        {
            setWorkingEnabled(false);
        }

    }

    // ==================== 核心方法 ====================

    /**
     * 同步库存到模拟器
     */
    private void syncInventoryToSimulator() {
        for (int slot = 0; slot < componentHandler.getSlots(); slot++) {
            int x = slot % reactorWidth;
            int y = slot / reactorWidth;

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
        for (int x = 0; x < reactorWidth; x++) {
            for (int y = 0; y < reactorHeight; y++) {
                int slot = y * reactorWidth + x;
                ItemStack simulatorStack = reactorSimulator.getComponent(x, y);

                // 更新库存中的物品状态（只更新耐久变化）
                if (!simulatorStack.isEmpty()) {
                    ItemStack currentStack = componentHandler.getStackInSlot(slot);
                    if (!currentStack.isEmpty() && currentStack.isItemEqual(simulatorStack)) {
                        // 复制NBT数据（主要是耐久）
                        if (simulatorStack.hasTagCompound()) {
                            currentStack.setTagCompound(simulatorStack.getTagCompound().copy());
                        }
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
            // 转换为每tick能量
            long energyPerTick = currentOutput;

            // 添加到能量容器
            if (outEnergyContainer != null) {
                outEnergyContainer.addEnergy(energyPerTick);
            }
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

        // 重新初始化反应堆
        initializeReactor(reactorWidth, reactorHeight);

        // 读取库存
        if (data.hasKey("ComponentInventory")) {
            try {
                componentHandler.deserializeNBT(data.getCompoundTag("ComponentInventory"));
            } catch (Exception e) {
                GTLog.logger.error("Error loading component inventory", e);
            }
        }

        // 读取基本状态
        hasMeltdown = data.getBoolean("HasMeltdown");
        tickCounter = data.getInteger("TickCounter");
        updateTimer = data.getInteger("UpdateTimer");
        currentHeat = data.getInteger("CurrentHeat");
        maxHeatCapacity = data.getInteger("MaxHeatCapacity");
        currentOutput = data.getLong("CurrentOutput");
        efficiency = data.getFloat("Efficiency");
        isReactorActive = data.getBoolean("IsReactorActive");

        // 读取模拟器状态
        if (data.hasKey("ReactorSimulator", TAG_COMPOUND)) {
            try {
                NBTTagCompound simulatorNBT = data.getCompoundTag("ReactorSimulator");
                reactorSimulator.readFromNBT(simulatorNBT);
            } catch (Exception e) {
                GTLog.logger.error("Error loading reactor simulator data", e);
                reactorSimulator.setHeat(currentHeat);
            }
        } else {
            // 没有保存的模拟器数据，使用当前热量
            reactorSimulator.setHeat(currentHeat);
        }

        // 同步状态
        if (getWorld() != null && !getWorld().isRemote && isStructureFormed()) {
            syncInventoryToSimulator();
            updateLocalCache();
        }
    }

    // ==================== 网络同步 ====================

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);

        buf.writeInt(reactorWidth);
        buf.writeInt(reactorHeight);
        buf.writeInt(currentHeat);
        buf.writeInt(maxHeatCapacity);
        buf.writeLong(currentOutput);
        buf.writeBoolean(isReactorActive);
        buf.writeBoolean(hasMeltdown);

        // 写入库存数据
        buf.writeVarInt(componentHandler.getSlots());
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
        int slotCount = buf.readVarInt();
        for (int slot = 0; slot < Math.min(slotCount, componentHandler.getSlots()); slot++) {
            try {
                ItemStack stack = buf.readItemStack();
                componentHandler.setStackInSlot(slot, stack);
            } catch (IOException e) {
                GTLog.logger.error("Error reading inventory from network", e);
            }
        }
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);

        if (dataId == SYNC_REACTOR_STATE) {
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
        // 注册槽位组，使用宽度作为每行的槽位数
        syncManager.registerSlotGroup("reactor_inventory", reactorWidth);

        // 计算面板大小
        int panelHeight = 4 + 20 + (reactorHeight * 18) + 4;
        int panelWidth = 4 + (reactorWidth * 18) + 4;

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
                        .child(IKey.str("核电组件 (" + reactorWidth + "×" + reactorHeight + ")")
                                .asWidget()
                                .heightRel(1.0f)))
                // 组件网格
                .child(Flow.column()
                        .top(24)
                        .left(4)
                        .width(reactorWidth * 18)
                        .height(reactorHeight * 18)
                        .child(createComponentGrid(reactorWidth, reactorHeight, syncManager)));
    }

    /**
     * 创建组件网格
     */
    private Grid createComponentGrid(int width, int height, PanelSyncManager syncManager) {
        int slotCount = width * height;

        return new Grid()
                .minElementMargin(0, 0)
                .minColWidth(18).minRowHeight(18)
                .alignX(0.5f)
                .mapTo(width, slotCount, index -> {

                    // 创建物品槽
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
                });
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
                    boolean working = syncer.syncBoolean(isWorkingEnabled());


                    if (meltdown) {
                        richText.add(IKey.str(TextFormatting.DARK_RED + "✗ 反应堆熔毁"));
                    } else if (active && working) {
                        richText.add(IKey.str(TextFormatting.GREEN + "✓ 运行中"));
                    } else {
                        richText.add(IKey.str(TextFormatting.GRAY + "○ 待机"));
                    }

                    // 大小
                    richText.add(IKey.str(TextFormatting.GRAY + "大小: " + reactorWidth + "×" + reactorHeight));
                    //richText.add(IKey.str(TextFormatting.GRAY + "扩展仓: " + extendCount + "/" + (MAX_SIZE - BASE_SIZE)));

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
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("YYY", "YYY", "YYY")
                .aisle("YYY", "YYY", "YYY")
                .aisle("YYY", "YSY", "YYY")
                .where('S', selfPredicate())
                .where('Y', states(getCasingState())
                        .or(abilities(MultiblockAbility.OUTPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(3))
                        .or(abilities(SCMultiblockAbility.REACTOR_EXTEND_HATCH).setMinGlobalLimited(0).setMaxGlobalLimited(1))
                )
                .where(' ', any())
                .build();
    }

    private IBlockState getCasingState() {
        return SCMetaBlocks.NUCLEAR_REACTOR_CASING.getState(BlockNuclearReactorCasing.NuclearReactorType.NUCLEAR_REACTOR_CASING);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return SCTextures.NUCLEAR_REACTOR_CASING;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), isReactorActive,
                isWorkingEnabled());
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return SCTextures.NUCLEAR_REACTOR_OVERLAY;
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
    public @NotNull List<ITextComponent> getDataInfo() {
        List<ITextComponent> list = new ArrayList<>();

        if (isStructureFormed()) {
            // 添加反应堆状态信息
            list.add(new TextComponentString("大小: " + reactorWidth + "×" + reactorHeight));
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

    @SideOnly(Side.CLIENT)
    @Override
    public SoundEvent getSound() {
        return GTSoundEvents.FURNACE;
    }

    @Override
    public void addInformation(ItemStack stack, World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        InformationHandler.topTooltips("核电之星", tooltip);
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add("多方块核裂变反应堆-通过受控核裂变链式反应产生大量能源");
        tooltip.add(TextFormatting.GREEN + I18n.format("-工作原理："));
        tooltip.add("反应堆核心通过铀/钚燃料棒的核裂变过程产生能量，每次裂变释放大量热能和中子，中子撞击其他燃料原子引发链式反应");
        tooltip.add("产生的热能需要通过散热系统持续移除，否则热量累积将导致反应堆过热甚至熔毁，同时热能会按比例转换为EU电力输出");
        tooltip.add("中子反射板将逃逸的中子反射回燃料棒，提高裂变效率但同时增加热量产生，需要精密的热平衡设计");
        tooltip.add("当热量超过9500HU阈值时，反应堆将发生不可逆的熔毁，摧毁所有内部组件并对周围环境造成严重破坏");
        tooltip.add(TextFormatting.GREEN + I18n.format("-拓展升级："));
        tooltip.add("通过安装燃料拓展仓来扩展核反应堆的功能属性。");
        tooltip.add(TextFormatting.GREEN + I18n.format("-组件功能："));
        tooltip.add("燃料棒-反应堆的核心能源来源，基础输出功率取决于燃料类型，相邻燃料棒会产生额外的链式反应加成");
        tooltip.add("散热片-被动散热组件，每tick移除固定量热量，分为普通散热片(冷却自身)和元件散热片(冷却相邻燃料棒)");
        tooltip.add("冷却单元-高效主动冷却组件，能大量吸收热量但会随使用逐渐消耗，需要定期更换以维持反应堆安全");
        tooltip.add("中子反射板-每个相邻反射板为燃料棒提供20%效率加成，但同样增加20%热量产生，是效率与风险的平衡选择");
        tooltip.add("反应堆隔板-强化反应堆结构，每块隔板增加热容量和爆炸抗性，是防止熔毁的关键安全组件");
        tooltip.add(TextFormatting.GREEN + I18n.format("-设计策略："));
        tooltip.add("最佳配置通常采用'蜂窝式'布局：燃料棒为中心，周围环绕反射板，外层布置散热片和冷却单元，最外层使用隔板加固");
        tooltip.add("高效率设计需要精确计算热量产生与散热比例，理想运行状态应保持热量在40-70%区间，既能高效输出又能留有安全余量");
        tooltip.add("使用多个小型反应堆集群比单一大型反应堆更安全，可以独立维护和控制，避免单点故障导致全系统崩溃");
        tooltip.add("熔毁防护设计原则：确保最大热量产生速率不超过最大散热能力的80%，为突发情况预留缓冲空间");
        tooltip.add(TextFormatting.GREEN + I18n.format("-重要警告："));
        tooltip.add("反应堆熔毁将释放毁灭性爆炸，其威力与内部热量和燃料数量成正比，可能摧毁整个基地! 务必安装应急冷却系统并在设计中预留安全冗余。");
    }
}