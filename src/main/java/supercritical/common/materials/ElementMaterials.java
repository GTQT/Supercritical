package supercritical.common.materials;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.Elements;
import gregtech.api.unification.material.Material;
import supercritical.api.unification.SCElements;

import static gregtech.api.unification.material.Materials.EXT_METAL;
import static gregtech.api.unification.material.info.MaterialIconSet.METALLIC;
import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static gregtech.api.util.GTUtility.gregtechId;
import static supercritical.api.unification.material.SCMaterials.*;
import static supercritical.api.unification.material.properties.SCMaterialsFlag.GENERATE_PELLETS;
import static supercritical.api.util.SCUtility.scId;

/**
 * Copyright (C) SymmetricDevs 2025
 * 由 KeQingSoCute520 于 2025 修改。
 * 修改内容：添加靶丸。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */
/*
 * Ranges 1-499
 */
public class ElementMaterials {

    public static void register() {
        Uranium = new Material.Builder(1, scId("uranium"))
                .ingot(3).dust()
                .liquid(new FluidBuilder().temperature(1405))
                .color(0x32F032).iconSet(METALLIC)
                .flags(EXT_METAL)
                .element(Elements.U)
                .flags(GENERATE_PELLETS)
                .build();

        Uranium239 = new Material.Builder(2, scId("uranium_239"))
                .ingot().dust().fluid()
                .color(0x46FA46).iconSet(SHINY)
                .element(SCElements.U239)
                .flags(GENERATE_PELLETS)
                .build();

        Neptunium235 = new Material.Builder(3, scId("neptunium_235"))
                .ingot().dust().fluid()
                .color(0x284D7B).iconSet(METALLIC)
                .element(SCElements.Np235)
                .flags(GENERATE_PELLETS)
                .build();

        Neptunium236 = new Material.Builder(4, scId("neptunium_236"))
                .ingot().dust().fluid()
                .color(0x284D7B).iconSet(METALLIC)
                .element(SCElements.Np236)
                .flags(GENERATE_PELLETS)
                .build();

        Neptunium237 = new Material.Builder(5, scId("neptunium_237"))
                .ingot().dust().fluid()
                .color(0x284D7B).iconSet(METALLIC)
                .element(SCElements.Np237)
                .flags(GENERATE_PELLETS)
                .build();

        Neptunium239 = new Material.Builder(6, scId("neptunium_239"))
                .ingot().dust().fluid()
                .color(0x284D7B).iconSet(METALLIC)
                .element(SCElements.Np239)
                .flags(GENERATE_PELLETS)
                .build();

        Plutonium238 = new Material.Builder(7, scId("plutonium_238"))
                .ingot().dust()
                .liquid(new FluidBuilder().temperature(913))
                .color(0xF03232).iconSet(METALLIC)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Pu238)
                .build();

        Plutonium240 = new Material.Builder(8, scId("plutonium_240"))
                .ingot().dust()
                .liquid(new FluidBuilder().temperature(913))
                .color(0xF03232).iconSet(METALLIC)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Pu240)
                .build();

        Plutonium242 = new Material.Builder(9, scId("plutonium_242"))
                .ingot().dust()
                .liquid(new FluidBuilder().temperature(913))
                .flags(GENERATE_PELLETS)
                .color(0xF03232).iconSet(METALLIC)
                .element(SCElements.Pu242)
                .build();

        Plutonium244 = new Material.Builder(10, scId("plutonium_244"))
                .ingot().dust()
                .liquid(new FluidBuilder().temperature(913))
                .flags(GENERATE_PELLETS)
                .color(0xF03232).iconSet(METALLIC)
                .element(SCElements.Pu244)
                .build();

        Plutonium = new Material.Builder(11, scId("plutonium"))
                .ingot().dust().fluid()
                .color(0xF03232).iconSet(METALLIC)
                .flags(GENERATE_PELLETS)
                .element(Elements.Pu)
                .build();

        //Additions Nuclear stuff, introduced by KeQingSoCute520
        Uranium233 = new Material.Builder(6500, gregtechId("uranium_233"))
                .ingot().fluid().dust()
                .color(0x3B3B3B)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Uranium233)
                .build();

        Uranium234 = new Material.Builder(6501, gregtechId("uranium_234"))
                .ingot().fluid().dust()
                .color(0x90EE90)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Uranium234)
                .build();

        Uranium236 = new Material.Builder(6502, gregtechId("uranium_236"))
                .ingot().fluid().dust()
                .color(0x8FBC8F)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Uranium236)
                .build();

        Thorium229 = new Material.Builder(6503, gregtechId("thorium_229"))
                .ingot().fluid().dust()
                .color(0xFFD700)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Thorium229)
                .build();

        Thorium230 = new Material.Builder(6504, gregtechId("thorium_230"))
                .ingot().fluid().dust()
                .color(0xFFA500)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Thorium230)
                .build();

        Americium241 = new Material.Builder(6505, gregtechId("americium_241"))
                .ingot().fluid().dust()
                .color(0x00868B)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Americium241)
                .build();

        Americium242 = new Material.Builder(6506, gregtechId("americium_242"))
                .ingot().fluid().dust()
                .color(0x008B45)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Americium242)
                .build();

        Americium243 = new Material.Builder(6507, gregtechId("americium_243"))
                .ingot().fluid().dust()
                .color(0x006400)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Americium243)
                .build();

        Curium243 = new Material.Builder(6508, gregtechId("curium_243"))
                .ingot().fluid().dust()
                .color(0x0000FF)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Curium243)
                .build();

        Curium245 = new Material.Builder(6509, gregtechId("curium_245"))
                .ingot().fluid().dust()
                .color(0x0000EE)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Curium245)
                .build();

        Curium246 = new Material.Builder(6510, gregtechId("curium_246"))
                .ingot().fluid().dust()
                .color(0x0000CD)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Curium246)
                .build();

        Curium247 = new Material.Builder(6511, gregtechId("curium_247"))
                .ingot().fluid().dust()
                .color(0x0000AA)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Curium247)
                .build();
    }
}
