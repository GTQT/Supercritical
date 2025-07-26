package supercritical.common.materials;

import gregtech.api.GTValues;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.BlastProperty;
import supercritical.api.unification.material.properties.CoolantProperty;
import supercritical.api.unification.material.properties.SCPropertyKey;

import static gregtech.api.GTValues.EV;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;
import static supercritical.api.unification.material.SCMaterials.*;
import static supercritical.api.util.SCUtility.scId;

/*
 * Ranges 500-999
 */
public class FirstDegreeMaterials {

    static int startId = 500;

    public static int getID() {
        return startId++;
    }

    public static void register() {
        HighEnrichedUraniumDioxide = new Material.Builder(getID(), scId("high_enriched_uranium_dioxide"))
                .dust(3)
                .color(0x53E353).iconSet(DULL)
                .flags(DISABLE_DECOMPOSITION)
                .components(Uranium235, 1, Oxygen, 2)
                .build()
                .setFormula("UO2", true);

        DepletedUraniumDioxide = new Material.Builder(getID(), scId("depleted_uranium_dioxide"))
                .dust(3)
                .color(0x335323).iconSet(DULL)
                .flags(DISABLE_DECOMPOSITION)
                .components(Uranium, 1, Oxygen, 2)
                .build()
                .setFormula("UO2", true);

        HighPressureSteam = new Material.Builder(getID(), scId("high_pressure_steam"))
                .gas(new FluidBuilder()
                        .temperature(500)
                        .customStill())
                .color(0xC4C4C4)
                .flags(DISABLE_DECOMPOSITION)
                .components(Hydrogen, 2, Oxygen, 1)
                .build();

        FissilePlutoniumDioxide = new Material.Builder(getID(), scId("fissile_plutonium_dioxide"))
                .dust(3)
                .color(0xF03232).iconSet(DULL)
                .flags(DISABLE_DECOMPOSITION)
                .components(Plutonium, 1, Oxygen, 2)
                .build();

        Zircaloy = new Material.Builder(getID(), scId("zircaloy"))
                .ingot().ore()
                .color(0x566570).iconSet(METALLIC)
                .flags(GENERATE_RING, GENERATE_PLATE)
                .components(Zirconium, 16, Tin, 2, Chrome, 1)
                .blast(1700, BlastProperty.GasTier.LOW)
                .build();

        LowEnrichedUraniumDioxide = new Material.Builder(getID(), scId("low_enriched_uranium_dioxide"))
                .dust()
                .color(0x43A333)
                .flags(DISABLE_DECOMPOSITION)
                .components(Uranium235, 1, Oxygen, 2)
                .build()
                .setFormula("UO2", true);

        Inconel = new Material.Builder(getID(), scId("inconel"))
                .ingot().fluid()
                .color(0x7F8F75).iconSet(SHINY)
                .flags(GENERATE_DOUBLE_PLATE, GENERATE_SPRING, DISABLE_DECOMPOSITION)
                .components(Nickel, 5, Chrome, 2, Iron, 2, Niobium, 1, Molybdenum, 1)
                .blast(b -> b.temp(1610, BlastProperty.GasTier.MID).blastStats(GTValues.VA[EV], 200))
                .fluidPipeProperties(2010, 175, true, true, true, false)
                .build();

        HighEnrichedUraniumHexafluoride = new Material.Builder(getID(), scId("high_enriched_uranium_hexafluoride"))
                .gas()
                .color(0x5BF93A)
                .flags(DISABLE_DECOMPOSITION)
                .components(Uranium235, 1, Fluorine, 6)
                .build();

        BoronTrioxide = new Material.Builder(getID(), scId("boron_trioxide"))
                .dust()
                .color(0xC1E9E1)
                .components(Boron, 2, Oxygen, 3)
                .iconSet(METALLIC)
                .build();

        BoronCarbide = new Material.Builder(getID(), scId("boron_carbide"))
                .ingot()
                .flags(GENERATE_ROD, DISABLE_DECOMPOSITION)
                .blast(2620)
                .color(0xC1E9C1)
                .components(Boron, 4, Carbon, 1)
                .iconSet(METALLIC)
                .build();

        HighPressureHeavyWater = new Material.Builder(getID(), scId("high_pressure_heavy_water"))
                .gas(new FluidBuilder().temperature(500))
                .color(0xCCD9F0)
                .flags(DISABLE_DECOMPOSITION)
                .components(Deuterium, 2, Oxygen, 1)
                .build();

        HeavyWater = new Material.Builder(getID(), scId("heavy_water"))
                .fluid()
                .color(0x3673D6)
                .components(Deuterium, 2, Oxygen, 1)
                .build();

        HeavyWater.setProperty(SCPropertyKey.COOLANT,
                new CoolantProperty(HeavyWater, HighPressureHeavyWater, FluidStorageKeys.LIQUID, 4., 1000,
                        374.4, 2064000, 4228.)
                        .setAccumulatesHydrogen(true));
    }
}
