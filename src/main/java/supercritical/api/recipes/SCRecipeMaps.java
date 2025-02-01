package supercritical.api.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;
import supercritical.api.recipes.builders.NoEnergyRecipeBuilder;
import supercritical.common.SCConfigHolder;

/**
 * Copyright (C) SymmetricDevs 2025
 * 由 KeQingSoCute520 于 2025 修改。
 * 修改内容：添加靶丸。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */
public class SCRecipeMaps {

    public static final RecipeMap<NoEnergyRecipeBuilder> HEAT_EXCHANGER_RECIPES = new RecipeMap<>("heat_exchanger", 1,
            0, 2, 2, new NoEnergyRecipeBuilder(), SCConfigHolder.misc.enableHX)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressWidget.MoveType.HORIZONTAL)
            .setSound(GTSoundEvents.COOLING);

    public static final RecipeMap<SimpleRecipeBuilder> SPENT_FUEL_POOL_RECIPES = new RecipeMap<>("spent_fuel_pool", 1,
            1, 1, 1, new SimpleRecipeBuilder(), false)
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressWidget.MoveType.HORIZONTAL);

    public static final RecipeMap<SimpleRecipeBuilder> GAS_CENTRIFUGE_RECIPES = new RecipeMap<>("gas_centrifuge", 0, 0,
            1, 2, new SimpleRecipeBuilder(), false)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MIXER, ProgressWidget.MoveType.CIRCULAR)
            .setSound(GTSoundEvents.CENTRIFUGE);

    public static final RecipeMap<SimpleRecipeBuilder> DECAY_CHAMBER_RECIPES = new RecipeMap<>("decay_chamber_recipes", 1, 1, 1, 1, new SimpleRecipeBuilder(), false)
            .setProgressBar(GuiTextures.PROGRESS_BAR_HAMMER, ProgressWidget.MoveType.VERTICAL_DOWNWARDS)
            .setSound(GTSoundEvents.SCIENCE);

    public static final RecipeMap<FuelRecipeBuilder> RTG_RECIPES= new RecipeMap<>("rtg_recipes", 1,  1,  1, 0, new FuelRecipeBuilder(), false)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressWidget.MoveType.HORIZONTAL)
            .setSound(GTSoundEvents.ARC);
    public static void init() {
    }
}
