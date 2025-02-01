package supercritical.common;

import java.util.Objects;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import org.jetbrains.annotations.NotNull;

import gregtech.api.GregTechAPI;
import gregtech.api.block.VariantItemBlock;
import gregtech.api.modules.ModuleContainerRegistryEvent;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.event.MaterialRegistryEvent;
import gregtech.api.unification.material.event.PostMaterialEvent;
import gregtech.common.items.MetaItems;
import gregtech.modules.ModuleManager;
import supercritical.SCValues;
import supercritical.api.nuclear.fission.CoolantRegistry;
import supercritical.api.nuclear.fission.FissionFuelRegistry;
import supercritical.api.unification.material.SCMaterialFlagAddition;
import supercritical.api.unification.material.SCMaterialPropertyAddition;
import supercritical.api.unification.material.properties.CoolantProperty;
import supercritical.api.unification.material.properties.FissionFuelProperty;
import supercritical.api.unification.material.properties.SCPropertyKey;
import supercritical.api.unification.ore.SCOrePrefix;
import supercritical.api.util.SCLog;
import supercritical.common.blocks.SCMetaBlocks;
import supercritical.common.item.SCMetaItems;
import supercritical.loaders.recipe.SCRecipeManager;
import supercritical.modules.SCModules;
/**
 * Copyright (C) SymmetricDevs 2025
 * 由 KeQingSoCute520 于 2025 修改。
 * 修改内容：添加靶丸。
 * 此文件遵循 GPL-3.0 许可证，详情请见项目根目录的 LICENSE 文件。
 */
@Mod.EventBusSubscriber(modid = SCValues.MODID)
public class CommonProxy {

    @SubscribeEvent
    public static void syncConfigValues(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(SCValues.MODID)) {
            ConfigManager.sync(SCValues.MODID, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        SCLog.logger.info("Registering blocks...");
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(SCMetaBlocks.FISSION_CASING);
        registry.register(SCMetaBlocks.NUCLEAR_CASING);
        registry.register(SCMetaBlocks.GAS_CENTRIFUGE_CASING);
        registry.register(SCMetaBlocks.PANELLING);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        SCLog.logger.info("Registering Items...");

        SCMetaItems.initSubitems();
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(createItemBlock(SCMetaBlocks.FISSION_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(SCMetaBlocks.NUCLEAR_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(SCMetaBlocks.GAS_CENTRIFUGE_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(SCMetaBlocks.PANELLING, VariantItemBlock::new));
    }

    private static <T extends Block> ItemBlock createItemBlock(T block, Function<T, ItemBlock> producer) {
        ItemBlock itemBlock = producer.apply(block);
        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        return itemBlock;
    }

    @SubscribeEvent
    public static void postRegisterMaterials(@NotNull PostMaterialEvent event) {
        MetaItems.addOrePrefix(
                SCOrePrefix.fuelRod,
                SCOrePrefix.fuelRodDepleted,
                SCOrePrefix.fuelRodHotDepleted,
                SCOrePrefix.fuelPellet,
                SCOrePrefix.fuelPelletDepleted,
                SCOrePrefix.dustSpentFuel,
                SCOrePrefix.dustBredFuel,
                SCOrePrefix.dustFissionByproduct,
                //Additions Nuclear stuff, introduced by KeQingSoCute520
                SCOrePrefix.fuelPebble,
                SCOrePrefix.fuelPebbleDepleted
        );
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        SCLog.logger.info("Registering recipes...");

        SCRecipeManager.load();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerRecipesLowest(RegistryEvent.Register<IRecipe> event) {
        SCLog.logger.info("Running late material handlers...");

        SCRecipeManager.loadLatest();
    }

    public void preLoad() {}

    public void onPostLoad() {
        for (Material material : GregTechAPI.materialManager.getRegisteredMaterials()) {
            if (material.hasProperty(SCPropertyKey.FISSION_FUEL)) {
                FissionFuelProperty prop = material.getProperty(SCPropertyKey.FISSION_FUEL);
                FissionFuelRegistry.registerFuel(OreDictUnifier.get(SCOrePrefix.fuelRod, material), prop,
                        OreDictUnifier.get(SCOrePrefix.fuelRodHotDepleted, material));
            }
            if (material.hasProperty(SCPropertyKey.COOLANT)) {
                CoolantProperty prop = material.getProperty(SCPropertyKey.COOLANT);
                CoolantRegistry.registerCoolant(material.getFluid(prop.getCoolantKey()), prop);
            }
        }
    }

    @SubscribeEvent
    public static void createMaterialRegistry(MaterialRegistryEvent event) {
        GregTechAPI.materialManager.createRegistry(SCValues.MODID);
    }

    @SubscribeEvent
    public static void registerModuleContainer(ModuleContainerRegistryEvent event) {
        ModuleManager.getInstance().registerContainer(new SCModules());
    }
}
