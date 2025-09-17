package supercritical.common.materials;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.material.Material;
import supercritical.api.unification.SCElements;

import static gregtech.api.unification.material.info.MaterialIconSet.METALLIC;
import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static supercritical.api.unification.material.SCMaterials.*;
import static supercritical.api.unification.material.properties.SCMaterialsFlag.GENERATE_PELLETS;
import static supercritical.api.util.SCUtility.scId;

/**
 * Copyright (C) SymmetricDevs 2025
 * 由 MeowmelMuku 于 2025 修改。
 * 修改内容：添加靶丸。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */
/*
 * Ranges 1-499
 */
public class ElementMaterials {
    static int startID = 1;

    public static int getStartID() {
        return startID++;
    }

    public static void register() {
        Uranium233 = new Material.Builder(getStartID(), scId("uranium_233"))
                .ingot().fluid().dust()
                .color(0x3B3B3B)
                .flags(GENERATE_PELLETS)
                .element(SCElements.U233)
                .build();

        Uranium234 = new Material.Builder(getStartID(), scId("uranium_234"))
                .ingot().fluid().dust()
                .color(0x90EE90)
                .flags(GENERATE_PELLETS)
                .element(SCElements.U234)
                .build();

        Uranium236 = new Material.Builder(getStartID(), scId("uranium_236"))
                .ingot().fluid().dust()
                .color(0x8FBC8F)
                .flags(GENERATE_PELLETS)
                .element(SCElements.U236)
                .build();

        Uranium239 = new Material.Builder(getStartID(), scId("uranium_239"))
                .ingot().dust().fluid()
                .color(0x46FA46).iconSet(SHINY)
                .element(SCElements.U239)
                .flags(GENERATE_PELLETS)
                .build();

        Neptunium235 = new Material.Builder(getStartID(), scId("neptunium_235"))
                .ingot().dust().fluid()
                .color(0x284D7B).iconSet(METALLIC)
                .element(SCElements.Np235)
                .flags(GENERATE_PELLETS)
                .build();

        Neptunium236 = new Material.Builder(getStartID(), scId("neptunium_236"))
                .ingot().dust().fluid()
                .color(0x284D7B).iconSet(METALLIC)
                .element(SCElements.Np236)
                .flags(GENERATE_PELLETS)
                .build();

        Neptunium237 = new Material.Builder(getStartID(), scId("neptunium_237"))
                .ingot().dust().fluid()
                .color(0x284D7B).iconSet(METALLIC)
                .element(SCElements.Np237)
                .flags(GENERATE_PELLETS)
                .build();

        Neptunium239 = new Material.Builder(getStartID(), scId("neptunium_239"))
                .ingot().dust().fluid()
                .color(0x284D7B).iconSet(METALLIC)
                .element(SCElements.Np239)
                .flags(GENERATE_PELLETS)
                .build();

        Plutonium238 = new Material.Builder(getStartID(), scId("plutonium_238"))
                .ingot().dust()
                .liquid(new FluidBuilder().temperature(913))
                .color(0xF03232).iconSet(METALLIC)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Pu238)
                .build();

        Plutonium240 = new Material.Builder(getStartID(), scId("plutonium_240"))
                .ingot().dust()
                .liquid(new FluidBuilder().temperature(913))
                .color(0xF03232).iconSet(METALLIC)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Pu240)
                .build();

        Plutonium242 = new Material.Builder(getStartID(), scId("plutonium_242"))
                .ingot().dust()
                .liquid(new FluidBuilder().temperature(913))
                .flags(GENERATE_PELLETS)
                .color(0xF03232).iconSet(METALLIC)
                .element(SCElements.Pu242)
                .build();

        Plutonium244 = new Material.Builder(getStartID(), scId("plutonium_244"))
                .ingot().dust()
                .liquid(new FluidBuilder().temperature(913))
                .flags(GENERATE_PELLETS)
                .color(0xF03232).iconSet(METALLIC)
                .element(SCElements.Pu244)
                .build();

        Thorium229 = new Material.Builder(getStartID(), scId("thorium_229"))
                .ingot().fluid().dust()
                .color(0xFFD700)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Th229)
                .build();

        Thorium230 = new Material.Builder(getStartID(), scId("thorium_230"))
                .ingot().fluid().dust()
                .color(0xFFA500)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Th230)
                .build();

        Americium241 = new Material.Builder(getStartID(), scId("americium_241"))
                .ingot().fluid().dust()
                .color(0x00868B)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Am241)
                .build();

        Americium242 = new Material.Builder(getStartID(), scId("americium_242"))
                .ingot().fluid().dust()
                .color(0x008B45)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Am242)
                .build();

        Americium243 = new Material.Builder(getStartID(), scId("americium_243"))
                .ingot().fluid().dust()
                .color(0x006400)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Am243)
                .build();

        Curium243 = new Material.Builder(getStartID(), scId("curium_243"))
                .ingot().fluid().dust()
                .color(0x0000FF)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Cm243)
                .build();

        Curium245 = new Material.Builder(getStartID(), scId("curium_245"))
                .ingot().fluid().dust()
                .color(0x0000EE)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Cm245)
                .build();

        Curium246 = new Material.Builder(getStartID(), scId("curium_246"))
                .ingot().fluid().dust()
                .color(0x0000CD)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Cm246)
                .build();

        Curium247 = new Material.Builder(getStartID(), scId("curium_247"))
                .ingot().fluid().dust()
                .color(0x0000AA)
                .flags(GENERATE_PELLETS)
                .element(SCElements.Cm247)
                .build();
    }
}
