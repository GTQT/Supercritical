package supercritical.loaders.recipe;

import supercritical.common.SCConfigHolder;
import supercritical.loaders.recipe.handlers.FluidRecipeHandler;
import supercritical.loaders.recipe.handlers.NuclearRecipeHandler;
/**
 * Copyright (C) SymmetricDevs 2025
 * 由 KeQingSoCute520 于 2025 修改。
 * 修改内容：添加靶丸。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */
public class SCRecipeManager {

    public static void load() {
        if (SCConfigHolder.misc.disableAllRecipes) return;

        SCMiscRecipes.init();
        SCMachineRecipeLoader.init();
        SCMetaTileEnityLoader.init();
        SCMetaTileEntityMachineRecipeLoader.init();
        SCNuclearRecipes.init();
        NuclearRecipeHandler.register();
        //追加注册燃料配方
        SCDecayChamberRecipes.load();
        SCRTGFuel.load();
    }

    public static void loadLatest() {
        SCRecipeModifications.load();
        FluidRecipeHandler.runRecipeGeneration();
    }
}
