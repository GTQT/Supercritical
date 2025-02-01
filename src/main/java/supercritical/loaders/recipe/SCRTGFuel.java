package supercritical.loaders.recipe;

import gregtech.api.unification.material.Material;

import static gregtech.api.GTValues.VA;
import static gregtech.api.recipes.RecipeMaps.AUTOCLAVE_RECIPES;
import static gregtech.api.recipes.RecipeMaps.CENTRIFUGE_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static supercritical.api.recipes.SCRecipeMaps.RTG_RECIPES;
import static supercritical.api.unification.material.SCMaterials.*;
import static supercritical.api.unification.ore.SCOrePrefix.fuelPebble;
import static supercritical.api.unification.ore.SCOrePrefix.fuelPebbleDepleted;

public class SCRTGFuel {
    public static void load() {
        RTG(Thorium, 1, Uranium233, Uranium234, Thorium230, Thorium229, Strontium, Caesium);
        RTG(Uranium233, 2, Uranium234, Thorium230, Thorium229, Protactinium, Strontium, Caesium);
        RTG(Uranium234, 2, Uranium235, Thorium230, Thorium229, Protactinium, Strontium, Caesium);
        RTG(Uranium235, 2, Uranium236, Thorium230, Thorium229, Protactinium, Strontium, Caesium);
        RTG(Uranium236, 3, Uranium238, Thorium230, Thorium229, Protactinium, Strontium, Caesium);
        RTG(Uranium238, 3, Uranium239, Thorium230, Thorium229, Protactinium, Strontium, Caesium);
        RTG(Uranium239, 3, Plutonium239, Thorium230, Thorium229, Protactinium, Strontium, Caesium);
        RTG(Plutonium238, 3, Plutonium239, Uranium235, Uranium234, Americium241, Molybdenum, Caesium);
        RTG(Plutonium239, 3, Plutonium240, Uranium235, Uranium234, Americium241, Molybdenum, Caesium);
        RTG(Plutonium240, 3, Plutonium241, Uranium235, Uranium234, Americium241, Molybdenum, Caesium);
        RTG(Plutonium241, 4, Plutonium242, Uranium235, Uranium234, Americium241, Molybdenum, Caesium);
        RTG(Plutonium242, 4, Plutonium244, Uranium235, Uranium234, Americium241, Molybdenum, Curium243);
        RTG(Plutonium244, 4, Neptunium236, Uranium235, Uranium234, Americium241, Molybdenum, Curium243);
        RTG(Neptunium236, 5, Neptunium237, Plutonium242, Plutonium241, Americium241, Molybdenum, Curium243);
        RTG(Neptunium235, 5, Neptunium236, Plutonium242, Plutonium241, Americium241, Molybdenum, Curium243);
        RTG(Neptunium237, 5, Neptunium, Plutonium242, Plutonium241, Americium241, Molybdenum, Curium243);
        RTG(Neptunium, 5, Neptunium237, Plutonium242, Plutonium241, Americium241, Molybdenum, Curium243);
        RTG(Neptunium239, 5, Plutonium240, Plutonium241, Americium241, Curium243, Molybdenum, Curium243);
        RTG(Americium, 5, Americium241, Curium243, Curium, Curium245, Molybdenum, Curium243);
        RTG(Americium241, 5, Americium242, Curium, Curium245, Curium246, Molybdenum, Curium243);
        RTG(Americium242, 5, Americium243, Curium245, Curium246, Curium247, Molybdenum, Curium243);
        RTG(Americium243, 6, Curium, Curium245, Curium246, Curium247, Molybdenum, Curium243);

    }

    private static void RTG(Material material, int fuel, Material material1, Material material2, Material material3, Material material4, Material material5, Material material6) {
        RTG(material, fuel);

        CENTRIFUGE_RECIPES.recipeBuilder()
                .input(fuelPebbleDepleted, material, 9)
                .output(dust, material1)
                .output(dust, material2, 5)
                .output(dust, material3)
                .output(dust, material4)
                .chancedOutput(dust, material5, 1, 2500, 0)
                .chancedOutput(dust, material6, 1, 2500, 0)
                .duration(400 + fuel * 20)
                .EUt(19)
                .buildAndRegister();
    }

    private static void RTG(Material material, int fuel) {
        AUTOCLAVE_RECIPES.recipeBuilder()
                .output(fuelPebble, material)
                .input(dust, material, 16)
                .fluidInputs(Nitrogen.getFluid(1000))
                .duration(400 + fuel * 20)
                .EUt(VA[fuel])
                .buildAndRegister();

        RTG_RECIPES.recipeBuilder()
                .input(fuelPebble, material)
                .output(fuelPebbleDepleted, material)
                .fluidInputs(Argon.getFluid(1000))
                .duration(2400 + fuel * 240)
                .EUt(2048)
                .buildAndRegister();

        RTG_RECIPES.recipeBuilder()
                .input(fuelPebble, material)
                .output(fuelPebbleDepleted, material)
                .fluidInputs(Helium.getFluid(1000))
                .duration(1600 + fuel * 180)
                .EUt(2048)
                .buildAndRegister();

        RTG_RECIPES.recipeBuilder()
                .input(fuelPebble, material)
                .output(fuelPebbleDepleted, material)
                .fluidInputs(Nitrogen.getFluid(1000))
                .duration(1200 + fuel * 120)
                .EUt(2048)
                .buildAndRegister();

    }
}
