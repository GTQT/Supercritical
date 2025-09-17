package supercritical.api.unification.ore;

import gregtech.api.unification.ore.OrePrefix;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.resources.I18n;
import supercritical.api.unification.material.info.SCMaterialIconType;
import supercritical.api.unification.material.properties.SCPropertyKey;

import java.util.Collections;
import java.util.function.Function;

import static gregtech.api.unification.ore.OrePrefix.Flags.ENABLE_UNIFICATION;
import static supercritical.api.unification.material.properties.SCMaterialsFlag.GENERATE_PELLETS;

/**
 * Copyright (C) SymmetricDevs 2025
 * 由 MeowmelMuku 于 2025 修改。
 * 修改内容：添加靶丸。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */
@ExtensionMethod(OrePrefixExtension.Handler.class)
public class SCOrePrefix {

    // Nuclear stuff, introduced by Zalgo and Bruberu
    public static final OrePrefix fuelRod = new OrePrefix("fuelRod", -1, null, SCMaterialIconType.fuelRod, 0,
            material -> material.hasProperty(SCPropertyKey.FISSION_FUEL),
            mat -> Collections.singletonList(I18n.format("metaitem.nuclear.tooltip.radioactive")));
    public static final OrePrefix fuelRodDepleted = new OrePrefix("fuelRodDepleted", -1, null,
            SCMaterialIconType.fuelRodDepleted, 0, material -> material.hasProperty(SCPropertyKey.FISSION_FUEL),
            mat -> Collections.singletonList(I18n.format("metaitem.nuclear.tooltip.radioactive")));
    public static final OrePrefix fuelRodHotDepleted = new OrePrefix("fuelRodHotDepleted", -1, null,
            SCMaterialIconType.fuelRodHotDepleted, 0, material -> material.hasProperty(SCPropertyKey.FISSION_FUEL),
            mat -> Collections.singletonList(I18n.format("metaitem.nuclear.tooltip.radioactive")));
    public static final OrePrefix fuelPelletRaw = new OrePrefix("fuelPelletRaw", -1, null,
            SCMaterialIconType.fuelPelletRaw, 0, material -> material.hasProperty(SCPropertyKey.FISSION_FUEL),
            mat -> Collections.singletonList(I18n.format("metaitem.nuclear.tooltip.radioactive")));
    public static final OrePrefix fuelPellet = new OrePrefix("fuelPellet", -1, null,
            SCMaterialIconType.fuelPellet, 0, material -> material.hasProperty(SCPropertyKey.FISSION_FUEL),
            mat -> Collections.singletonList(I18n.format("metaitem.nuclear.tooltip.radioactive")));
    public static final OrePrefix fuelPelletDepleted = new OrePrefix("fuelPelletDepleted", -1, null,
            SCMaterialIconType.fuelPelletDepleted, 0, material -> material.hasProperty(SCPropertyKey.FISSION_FUEL),
            mat -> Collections.singletonList(I18n.format("metaitem.nuclear.tooltip.radioactive")));

    public static final OrePrefix dustSpentFuel = new OrePrefix("dustSpentFuel", -1, null,
            SCMaterialIconType.dustSpentFuel, 0, material -> material.hasProperty(SCPropertyKey.FISSION_FUEL));
    public static final OrePrefix dustBredFuel = new OrePrefix("dustBredFuel", -1, null,
            SCMaterialIconType.dustBredFuel, 0, material -> material.hasProperty(SCPropertyKey.FISSION_FUEL));
    public static final OrePrefix dustFissionByproduct = new OrePrefix("dustFissionByproduct", -1, null,
            SCMaterialIconType.dustFissionByproduct, 0,
            material -> material.hasProperty(SCPropertyKey.FISSION_FUEL));

    //Additions Nuclear stuff, introduced by MeowmelMuku
    public static final OrePrefix fuelPebble = new OrePrefix("fuelPebble", -1, null, SCMaterialIconType.fuelPebble, ENABLE_UNIFICATION,
            mat -> mat.hasFlag(GENERATE_PELLETS));
    public static final OrePrefix fuelPebbleDepleted = new OrePrefix("fuelPebbleDepleted", -1, null, SCMaterialIconType.fuelPebbleDepleted, ENABLE_UNIFICATION,
            mat -> mat.hasFlag(GENERATE_PELLETS));

    public static void init() {
        fuelRod.setRadiationDamageFunction(neutrons -> neutrons / 10e23);
        fuelPelletRaw.setRadiationDamageFunction(neutrons -> neutrons / 160e23);
        fuelPellet.setRadiationDamageFunction(neutrons -> neutrons / 160e23);

        fuelRodDepleted.setRadiationDamageFunction(neutrons -> neutrons / 1.5e23);
        fuelRodHotDepleted.setRadiationDamageFunction(neutrons -> neutrons / 1e23);
        fuelRodHotDepleted.heatDamageFunction = x -> 2f;
        setRadiationDamageFunction(fuelPelletDepleted, neutrons -> neutrons / 24e23);
    }

    private static void setRadiationDamageFunction(OrePrefix prefix, Function<Double, Double> function) {
        ((OrePrefixExtension) prefix).setRadiationDamageFunction(function);
    }
}
