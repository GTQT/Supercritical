package supercritical.api.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMapBuilder;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;
import supercritical.api.recipes.builders.NoEnergyRecipeBuilder;

/**
 * Copyright (C) SymmetricDevs 2025
 * 由 KeQingSoCute520 于 2025 修改。
 * 修改内容：添加靶丸。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */
public class SCRecipeMaps {

    public static final RecipeMap<NoEnergyRecipeBuilder> HEAT_EXCHANGER_RECIPES;
    public static final RecipeMap<SimpleRecipeBuilder> SPENT_FUEL_POOL_RECIPES;
    public static final RecipeMap<SimpleRecipeBuilder> GAS_CENTRIFUGE_RECIPES;
    public static final RecipeMap<SimpleRecipeBuilder> DECAY_CHAMBER_RECIPES;
    public static final RecipeMap<FuelRecipeBuilder> RTG_RECIPES;

    static {
        HEAT_EXCHANGER_RECIPES = new RecipeMapBuilder<>("heat_exchanger", new NoEnergyRecipeBuilder())
                .itemInputs(1)
                .itemOutputs(0)
                .fluidInputs(2)
                .fluidOutputs(2)
                .progressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressWidget.MoveType.HORIZONTAL)
                .sound(GTSoundEvents.COOLING)
                .build();

        SPENT_FUEL_POOL_RECIPES = new RecipeMapBuilder<>("spent_fuel_pool", new SimpleRecipeBuilder())
                .itemInputs(1)
                .itemOutputs(1)
                .fluidInputs(1)
                .fluidOutputs(1)
                .progressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressWidget.MoveType.HORIZONTAL)
                .build();

        GAS_CENTRIFUGE_RECIPES = new RecipeMapBuilder<>("gas_centrifuge", new SimpleRecipeBuilder())
                .itemInputs(0)
                .itemOutputs(0)
                .fluidInputs(1)
                .fluidOutputs(2)
                .progressBar(GuiTextures.PROGRESS_BAR_MIXER, ProgressWidget.MoveType.CIRCULAR)
                .sound(GTSoundEvents.CENTRIFUGE)
                .build();

        DECAY_CHAMBER_RECIPES = new RecipeMapBuilder<>("decay_chamber_recipes", new SimpleRecipeBuilder())
                .itemInputs(1)
                .itemOutputs(1)
                .fluidInputs(1)
                .fluidOutputs(1)
                .progressBar(GuiTextures.PROGRESS_BAR_HAMMER, ProgressWidget.MoveType.VERTICAL_DOWNWARDS)
                .sound(GTSoundEvents.SCIENCE)
                .build();

        RTG_RECIPES = new RecipeMapBuilder<>("rtg_recipes", new FuelRecipeBuilder())
                .itemInputs(1)
                .itemOutputs(1)
                .fluidInputs(1)
                .fluidOutputs(0)
                .progressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressWidget.MoveType.HORIZONTAL)
                .sound(GTSoundEvents.ARC)
                .build();
    }

    private SCRecipeMaps() {
    }
}
