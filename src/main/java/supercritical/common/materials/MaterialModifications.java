package supercritical.common.materials;

import static gregtech.api.unification.material.Materials.*;
import static supercritical.api.unification.material.SCMaterials.HighPressureSteam;
import static supercritical.api.unification.material.properties.SCMaterialsFlag.GENERATE_PELLETS;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.info.MaterialIconSet;
import gregtech.api.unification.material.properties.BlastProperty;
import gregtech.api.unification.material.properties.FluidProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import supercritical.api.unification.material.properties.CoolantProperty;
import supercritical.api.unification.material.properties.FissionFuelProperty;
import supercritical.api.unification.material.properties.SCPropertyKey;

public class MaterialModifications {

    public static void init() {
        Uranium.addFlags(GENERATE_PELLETS);
        Plutonium.addFlags(GENERATE_PELLETS);

        // Uranium-238
        Uranium238.setMaterialRGB(0x46FA46);
        Uranium238.setMaterialIconSet(MaterialIconSet.SHINY);

        // Salt
        Salt.setProperty(PropertyKey.FLUID,
                new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder().translation("gregtech.fluid.molten")));

        // Stainless Steel
        StainlessSteel.addFlags(MaterialFlags.GENERATE_ROUND);

        // Uraninite
        // Uraninite. // TODO: How???
        Uraninite.setProperty(SCPropertyKey.FISSION_FUEL, new FissionFuelProperty(
                1800, 300, 55., 1.,
                1000., 0., 2.4, Uraninite.getRegistryName()));

        // Uranium Hexafluoride
        // UraniumHexafluoride // TODO: How???

        // Depleted Uranium Hexafluoride
        // DepletedUraniumHexafluoride // TODO: How???

        // Uranium Triplatinum
        // UraniumTriplatinum // TODO: How???

        // Uranium Rhodium Dinaquadide
        // UraniumRhodiumDinaquadide // TODO: How???

        // Distilled Water
        DistilledWater.setProperty(SCPropertyKey.COOLANT,
                new CoolantProperty(DistilledWater, HighPressureSteam, FluidStorageKeys.LIQUID, 1., 1000,
                        373, 2260000, 4168.)
                                .setAccumulatesHydrogen(true));
    }
}
