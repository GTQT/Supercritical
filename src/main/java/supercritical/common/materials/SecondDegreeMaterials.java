package supercritical.common.materials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.DISABLE_DECOMPOSITION;
import static gregtech.api.unification.material.info.MaterialIconSet.METALLIC;
import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static supercritical.api.unification.material.SCMaterials.*;
import static supercritical.api.util.SCUtility.scId;

import gregtech.api.unification.material.Material;
import supercritical.api.unification.material.properties.FissionFuelProperty;
import supercritical.api.unification.material.properties.SCPropertyKey;

/*
 * Ranges 1000-1499
 */
public class SecondDegreeMaterials {

    public static void register() {
        LEU235 = new Material.Builder(1001, scId("leu_235"))
                .dust(3)
                .color(0x232323).iconSet(METALLIC)
                .flags(DISABLE_DECOMPOSITION)
                .components(HighEnrichedUraniumDioxide, 1, DepletedUraniumDioxide, 19)
                .build()
                .setFormula("UO2", true);

        LEU235.setProperty(SCPropertyKey.FISSION_FUEL, new FissionFuelProperty(
                1500, 750, 55., 1.,
                2500., 0., 3.5, LEU235.getRegistryName()));

        HEU235 = new Material.Builder(1002, scId("heu_235"))
                .dust(3)
                .color(0x424845).iconSet(METALLIC)
                .flags(DISABLE_DECOMPOSITION)
                .components(HighEnrichedUraniumDioxide, 1, DepletedUraniumDioxide, 4)
                .build()
                .setFormula("UO2", true);

        HEU235.setProperty(SCPropertyKey.FISSION_FUEL, new FissionFuelProperty(
                1800, 600, 40., 1.,
                3000., 0., 2.5, HEU235.getRegistryName()));

        LowGradeMOX = new Material.Builder(1003, scId("low_grade_mox"))
                .dust(3)
                .color(0x62C032).iconSet(METALLIC)
                .flags(DISABLE_DECOMPOSITION)
                .components(FissilePlutoniumDioxide, 1, Uraninite, 19)
                .build()
                .setFormula("(U,Pu)O2", true);

        LowGradeMOX.setProperty(SCPropertyKey.FISSION_FUEL, new FissionFuelProperty(
                1600, 1000, 50., 10.,
                4000., 10., 1.5, LowGradeMOX.getRegistryName()));

        HighGradeMOX = new Material.Builder(1004, scId("high_grade_mox"))
                .dust(3)
                .color(0x7EA432).iconSet(METALLIC)
                .flags(DISABLE_DECOMPOSITION)
                .components(FissilePlutoniumDioxide, 1, Uraninite, 4)
                .build()
                .setFormula("(U,Pu)O2", true);

        HighGradeMOX.setProperty(SCPropertyKey.FISSION_FUEL, new FissionFuelProperty(
                2000, 800, 35., 25.,
                5500., 0., 1, HighGradeMOX.getRegistryName()));

        // 钚燃料体系
        FBR = new Material.Builder(1005, scId("fbr"))
                .dust(4)  // 提高处理等级
                .color(0x8A795D).iconSet(SHINY)
                .flags(DISABLE_DECOMPOSITION)
                .components(Plutonium, 1, Oxygen, 2)
                .build()
                .setFormula("PuO2", true);

        FBR.setProperty(SCPropertyKey.FISSION_FUEL, new FissionFuelProperty(
                2200, 500, 30., 50.,  // 更高的能量密度但更短寿命
                4800., 15., 0.8,
                FBR.getRegistryName()));

        // 钍燃料体系
        THOR = new Material.Builder(1006, scId("thor"))
                .dust(3)
                .color(0x5F4B32).iconSet(METALLIC)
                .flags(DISABLE_DECOMPOSITION)
                .components(Thorium, 1, Oxygen, 2)
                .build()
                .setFormula("ThO2", true);

        THOR.setProperty(SCPropertyKey.FISSION_FUEL, new FissionFuelProperty(
                2800, 1500, 80., 5.,  // 长寿命但低能量产出
                1800., 2., 5.2,
                THOR.getRegistryName()));
    }
}
