package supercritical.common.metatileentities;

import static gregtech.api.util.GTUtility.genericGeneratorTankSizeFunction;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static gregtech.common.metatileentities.MetaTileEntities.registerSimpleMetaTileEntity;
import static supercritical.api.util.SCUtility.scId;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import supercritical.api.recipes.SCRecipeMaps;
import supercritical.api.util.SCUtility;
import supercritical.client.renderer.textures.SCTextures;
import supercritical.common.SCConfigHolder;
import supercritical.common.metatileentities.multi.MetaTileEntityFissionReactor;
import supercritical.common.metatileentities.multi.MetaTileEntityHeatExchanger;
import supercritical.common.metatileentities.multi.MetaTileEntitySpentFuelPool;
import supercritical.common.metatileentities.multi.electric.MetaTileEntityGasCentrifuge;
import supercritical.common.metatileentities.multi.multiblockpart.*;
/**
 * Copyright (C) SymmetricDevs 2025
 * 由 KeQingSoCute520 于 2025 修改。
 * 修改内容：添加靶丸。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */
/*
 * Ranges 14000-14499
 */
public class SCMetaTileEntities {

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

    public static final SimpleGeneratorMetaTileEntity[] RTG = new SimpleGeneratorMetaTileEntity[5];
    public static SimpleMachineMetaTileEntity[] DECAY_CHAMBER = new SimpleMachineMetaTileEntity[GTValues.V.length - 1];
    public static void init() {
        /*
         * Singleblocks: 14000-14399
         */
        FUEL_ROD_INPUT = registerMetaTileEntity(14000, new MetaTileEntityFuelRodImportBus(scId("fuel_rod_input")));
        FUEL_ROD_OUTPUT = registerMetaTileEntity(14001, new MetaTileEntityFuelRodExportBus(scId("fuel_rod_output")));
        COOLANT_INPUT = registerMetaTileEntity(14002, new MetaTileEntityCoolantImportHatch(scId("coolant_input")));
        COOLANT_OUTPUT = registerMetaTileEntity(14003, new MetaTileEntityCoolantExportHatch(scId("coolant_output")));
        CONTROL_ROD = registerMetaTileEntity(14004, new MetaTileEntityControlRodPort(scId("control_rod"), false));
        CONTROL_ROD_MODERATED = registerMetaTileEntity(14005,
                new MetaTileEntityControlRodPort(scId("control_rod_moderated"), true));


        //RTG发电机 14300
        RTG[0] = registerMetaTileEntity(14300, new SimpleGeneratorMetaTileEntity(scId("rtg.hv"), SCRecipeMaps.RTG_RECIPES, SCTextures.RTG_OVERLAY, 3, genericGeneratorTankSizeFunction, true));
        RTG[1] = registerMetaTileEntity(14301, new SimpleGeneratorMetaTileEntity(scId("rtg.ev"), SCRecipeMaps.RTG_RECIPES, SCTextures.RTG_OVERLAY, 4, genericGeneratorTankSizeFunction, true));
        RTG[2] = registerMetaTileEntity(14302, new SimpleGeneratorMetaTileEntity(scId("rtg.iv"), SCRecipeMaps.RTG_RECIPES, SCTextures.RTG_OVERLAY, 5, genericGeneratorTankSizeFunction, true));
        RTG[3] = registerMetaTileEntity(14303, new SimpleGeneratorMetaTileEntity(scId("rtg.luv"), SCRecipeMaps.RTG_RECIPES, SCTextures.RTG_OVERLAY, 6, genericGeneratorTankSizeFunction, true));
        RTG[4] = registerMetaTileEntity(14304, new SimpleGeneratorMetaTileEntity(scId("rtg.zpm"), SCRecipeMaps.RTG_RECIPES, SCTextures.RTG_OVERLAY, 7, genericGeneratorTankSizeFunction, true));

        //衰变加速器
        registerSimpleMetaTileEntity(DECAY_CHAMBER, 14330, "decay_chamber", SCRecipeMaps.DECAY_CHAMBER_RECIPES, Textures.CHEMICAL_BATH_OVERLAY, true, SCUtility::scId, GTUtility.hvCappedTankSizeFunction);

        /*
         * Multiblocks: 14400-14499
         */
        if (SCConfigHolder.misc.enableHX) {
            HEAT_EXCHANGER = registerMetaTileEntity(14400, new MetaTileEntityHeatExchanger(scId("heat_exchanger")));
        }
        FISSION_REACTOR = registerMetaTileEntity(14401, new MetaTileEntityFissionReactor(scId("fission_reactor")));
        SPENT_FUEL_POOL = registerMetaTileEntity(14402, new MetaTileEntitySpentFuelPool(scId("spent_fuel_pool")));
        GAS_CENTRIFUGE = registerMetaTileEntity(14403, new MetaTileEntityGasCentrifuge(scId("gas_centrifuge")));
    }
}
