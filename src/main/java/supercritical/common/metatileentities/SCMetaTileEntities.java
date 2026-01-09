package supercritical.common.metatileentities;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import supercritical.api.recipes.SCRecipeMaps;
import supercritical.api.util.SCUtility;
import supercritical.client.renderer.textures.SCTextures;
import supercritical.common.metatileentities.multi.MetaTileEntityFissionReactor;
import supercritical.common.metatileentities.multi.MetaTileEntityHeatExchanger;
import supercritical.common.metatileentities.multi.nuclearReactor.MetaTileEntityNuclearExtend;
import supercritical.common.metatileentities.multi.nuclearReactor.MetaTileEntityNuclearReactor;
import supercritical.common.metatileentities.multi.MetaTileEntitySpentFuelPool;
import supercritical.common.metatileentities.multi.electric.MetaTileEntityGasCentrifuge;
import supercritical.common.metatileentities.multi.multiblockpart.*;

import static gregtech.api.util.GTUtility.genericGeneratorTankSizeFunction;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static gregtech.common.metatileentities.MetaTileEntities.registerSimpleMetaTileEntity;
import static supercritical.api.util.SCUtility.scId;

/**
 * Copyright (C) SymmetricDevs 2025
 * 由 MeowmelMuku 于 2025 修改。
 * 修改内容：添加靶丸。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */
/*
 * Ranges 14000-14499
 */
public class SCMetaTileEntities {

    public static final SimpleGeneratorMetaTileEntity[] RTG = new SimpleGeneratorMetaTileEntity[5];
    public static SimpleMachineMetaTileEntity[] DECAY_CHAMBER = new SimpleMachineMetaTileEntity[GTValues.V.length - 1];

    // Nuclear MTEs
    public static MetaTileEntityHeatExchanger HEAT_EXCHANGER;
    public static MetaTileEntityFissionReactor FISSION_REACTOR;
    public static MetaTileEntityFuelRodImportBus FUEL_ROD_INPUT;
    public static MetaTileEntityFuelRodExportBus FUEL_ROD_OUTPUT;
    public static MetaTileEntityCoolantImportHatch COOLANT_INPUT;
    public static MetaTileEntityCoolantExportHatch COOLANT_OUTPUT;
    public static MetaTileEntityControlRodPort CONTROL_ROD;
    public static MetaTileEntityControlRodPort CONTROL_ROD_MODERATED;
    public static MetaTileEntitySpentFuelPool SPENT_FUEL_POOL;
    public static MetaTileEntityGasCentrifuge GAS_CENTRIFUGE;
    public static MetaTileEntityModeratorPort MODERATOR_PORT;

    public static MetaTileEntityNuclearReactor NUCLEAR_REACTOR;
    public static MetaTileEntityNuclearExtend NUCLEAR_EXTEND_HATCH;

    public static void init() {
        //多方块部分
        HEAT_EXCHANGER = registerMetaTileEntity(1, new MetaTileEntityHeatExchanger(scId("heat_exchanger")));
        FISSION_REACTOR = registerMetaTileEntity(2, new MetaTileEntityFissionReactor(scId("fission_reactor")));
        SPENT_FUEL_POOL = registerMetaTileEntity(3, new MetaTileEntitySpentFuelPool(scId("spent_fuel_pool")));
        GAS_CENTRIFUGE = registerMetaTileEntity(4, new MetaTileEntityGasCentrifuge(scId("gas_centrifuge")));
        //IO
        FUEL_ROD_INPUT = registerMetaTileEntity(10, new MetaTileEntityFuelRodImportBus(scId("fuel_rod_input")));
        FUEL_ROD_OUTPUT = registerMetaTileEntity(11, new MetaTileEntityFuelRodExportBus(scId("fuel_rod_output")));
        COOLANT_INPUT = registerMetaTileEntity(12, new MetaTileEntityCoolantImportHatch(scId("coolant_input")));
        COOLANT_OUTPUT = registerMetaTileEntity(13, new MetaTileEntityCoolantExportHatch(scId("coolant_output")));
        CONTROL_ROD = registerMetaTileEntity(14, new MetaTileEntityControlRodPort(scId("control_rod"), false));
        CONTROL_ROD_MODERATED = registerMetaTileEntity(15, new MetaTileEntityControlRodPort(scId("control_rod_moderated"), true));
        MODERATOR_PORT = registerMetaTileEntity(16, new MetaTileEntityModeratorPort(scId("moderator_port")));
        //单方块发电机
        //RTG发电机
        RTG[0] = registerMetaTileEntity(20, new SimpleGeneratorMetaTileEntity(scId("rtg.hv"), SCRecipeMaps.RTG_RECIPES, SCTextures.RTG_OVERLAY, 3, genericGeneratorTankSizeFunction,1));
        RTG[1] = registerMetaTileEntity(21, new SimpleGeneratorMetaTileEntity(scId("rtg.ev"), SCRecipeMaps.RTG_RECIPES, SCTextures.RTG_OVERLAY, 4, genericGeneratorTankSizeFunction, 1));
        RTG[2] = registerMetaTileEntity(22, new SimpleGeneratorMetaTileEntity(scId("rtg.iv"), SCRecipeMaps.RTG_RECIPES, SCTextures.RTG_OVERLAY, 5, genericGeneratorTankSizeFunction, 1));
        RTG[3] = registerMetaTileEntity(23, new SimpleGeneratorMetaTileEntity(scId("rtg.luv"), SCRecipeMaps.RTG_RECIPES, SCTextures.RTG_OVERLAY, 6, genericGeneratorTankSizeFunction, 1));
        RTG[4] = registerMetaTileEntity(24, new SimpleGeneratorMetaTileEntity(scId("rtg.zpm"), SCRecipeMaps.RTG_RECIPES, SCTextures.RTG_OVERLAY, 7, genericGeneratorTankSizeFunction, 1));
        //可以随便再来几个


        //单方块设备
        //衰变加速器 30-45
        registerSimpleMetaTileEntity(DECAY_CHAMBER, 30, "decay_chamber", SCRecipeMaps.DECAY_CHAMBER_RECIPES, Textures.CHEMICAL_BATH_OVERLAY, true, SCUtility::scId, GTUtility.hvCappedTankSizeFunction);

        //核反应堆 100
        NUCLEAR_REACTOR = registerMetaTileEntity(100, new MetaTileEntityNuclearReactor(scId("nuclear_reactor")));
        NUCLEAR_EXTEND_HATCH = registerMetaTileEntity(101, new MetaTileEntityNuclearExtend(scId("nuclear_extend_hatch")));
    }
}
