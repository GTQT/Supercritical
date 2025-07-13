package supercritical.common.metatileentities.multi;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType;
import gregtech.common.blocks.BlockMetalCasing.MetalCasingType;
import gregtech.common.blocks.MetaBlocks;
import supercritical.api.capability.impl.NoEnergyRecipeLogic;
import supercritical.api.recipes.SCRecipeMaps;
import supercritical.client.renderer.textures.SCTextures;

public class MetaTileEntityHeatExchanger extends RecipeMapMultiblockController {

    public MetaTileEntityHeatExchanger(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SCRecipeMaps.HEAT_EXCHANGER_RECIPES);
        this.recipeMapWorkable = new NoEnergyRecipeLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityHeatExchanger(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCC", "BCB", "ACA")
                .aisle("CCC", "CDC", "ACA").setRepeatable(7)
                .aisle("CCC", "BSB", "AEA")
                .where('S', selfPredicate())
                .where('A', frames(Materials.Steel))
                .where('B', states(MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID)).setMinGlobalLimited(2)
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS)).setMinGlobalLimited(2)
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS)).setMinGlobalLimited(2)
                )
                .where('C', states(MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH)).setExactLimit(1)
                )
                .where('D', states(MetaBlocks.BOILER_CASING.getState(BoilerCasingType.STEEL_PIPE)))
                .where('E', states(MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS)).setMinGlobalLimited(2)
                )
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SCTextures.HEAT_EXCHANGER_OVERLAY;
    }
}
