package supercritical.api.unification.material;

import gregtech.api.unification.material.Material;
import supercritical.common.materials.ElementMaterials;
import supercritical.common.materials.FirstDegreeMaterials;
import supercritical.common.materials.SecondDegreeMaterials;
import supercritical.common.materials.UnknownCompositionMaterials;

/**
 * Copyright (C) SymmetricDevs 2025
 * 由 KeQingSoCute520 于 2025 修改。
 * 修改内容：添加靶丸。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */
public class SCMaterials {

    /* public static Material Neptunium = Materials.Neptunium; */
    public static Material Thorium229;
    public static Material Thorium230;

    public static Material Neptunium235;
    public static Material Neptunium236;
    public static Material Neptunium237;
    public static Material Neptunium239;

    public static Material Plutonium238;
    /* public static Material Plutonium239 = Materials.Plutonium239; */
    public static Material Plutonium240;
    /* public static Material Plutonium241 = Materials.Plutonium241; */
    public static Material Plutonium242;
    public static Material Plutonium244;

    /* public static Material Uranium235 = Materials.Uranium235; */
    /* public static Material Uranium238 = Materials.Uranium238; */
    public static Material Uranium239;

    //Additions Nuclear stuff, introduced by KeQingSoCute520
    public static Material Uranium233;
    public static Material Uranium234;
    public static Material Uranium236;
    public static Material Americium241;
    public static Material Americium242;
    public static Material Americium243;
    public static Material Curium243;
    public static Material Curium245;
    public static Material Curium246;
    public static Material Curium247;

    public static Material HighEnrichedUraniumHexafluoride;
    /* public static Material LowEnrichedUraniumHexafluoride = Materials.EnrichedUraniumHexafluoride; */

    public static Material LowEnrichedUraniumDioxide;

    public static Material HighEnrichedUraniumDioxide;
    public static Material DepletedUraniumDioxide;
    public static Material HighPressureSteam;
    public static Material FissilePlutoniumDioxide;
    public static Material Zircaloy;
    public static Material Inconel;
    public static Material BoronTrioxide;
    public static Material BoronCarbide;
    public static Material HeavyWater;
    public static Material HighPressureHeavyWater;

    public static Material Corium;
    public static Material SpentUraniumFuelSolution;
    public static Material RadonRichGasMixture;

    public static Material LEU235;
    public static Material HEU235;
    public static Material LowGradeMOX;
    public static Material HighGradeMOX;
    public static Material FBR;
    public static Material THOR;

    public static void register() {
        /*
         * Ranges 1-499
         */
        ElementMaterials.register();

        /*
         * Ranges 500-999
         */
        FirstDegreeMaterials.register();

        /*
         * Ranges 1000-1499
         */
        SecondDegreeMaterials.register();

        /*
         * Ranges 1500-1999
         */
        UnknownCompositionMaterials.register();
    }
}
