package supercritical.common.metatileentities.multi.electric;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPatternTemplate;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.SoftTemplate;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.casing.GTStructureChannels;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import supercritical.api.recipes.SCRecipeMaps;
import supercritical.client.renderer.textures.SCTextures;
import supercritical.common.blocks.BlockGasCentrifugeCasing;
import supercritical.common.blocks.BlockNuclearCasing;
import supercritical.common.blocks.SCMetaBlocks;

import java.util.List;

import static gregtech.api.util.RelativeDirection.*;

public class MetaTileEntityGasCentrifuge extends RecipeMapMultiblockController {

    @NotNull
    private static final SoftTemplate TEMPLATE = TemplatePool.getInstance().register("supercritical:gas_centrifuge", () ->
            DeclarativePatternBuilder.start(FRONT, UP, RIGHT)
                    .aisle("SI", "HH", "CC", "CC", "CC", "CC", "CC")
                    .aisleRepeatable(1, 14, "EE", "HH", "CC", "CC", "CC", "CC", "CC")
                    .withAisleChannel(GTStructureChannels.STRUCTURE_LENGTH.getName())
                    .aisle("OO", "HH", "CC", "CC", "CC", "CC", "CC")
                    .where('S', selfPredicate(MetaTileEntityGasCentrifuge.class))
                    .where('P', states(getPipeState()))
                    .where('H', states(getHeaterState()))
                    .where('C', states(getCentrifugeState()))
                    .where('I', states(getPipeState())
                            .fluidInput())
                    .where('E', states(getPipeState())
                            .maintenance()
                            .energyInput())
                    .where('O', states(getPipeState())
                            .fluidOutput())
                    .buildTemplate()
    );

    public MetaTileEntityGasCentrifuge(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SCRecipeMaps.GAS_CENTRIFUGE_RECIPES);
        this.recipeMapWorkable = new MultiblockRecipeLogic(this);
    }

    private static IBlockState getPipeState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.POLYTETRAFLUOROETHYLENE_PIPE);
    }

    private static IBlockState getHeaterState() {
        return SCMetaBlocks.NUCLEAR_CASING.getState(
                BlockNuclearCasing.NuclearCasingType.GAS_CENTRIFUGE_HEATER);
    }

    private static IBlockState getCentrifugeState() {
        return SCMetaBlocks.GAS_CENTRIFUGE_CASING
                .getState(BlockGasCentrifugeCasing.GasCentrifugeCasingType.GAS_CENTRIFUGE_COLUMN);
    }

    @Override
    protected @NotNull BlockPatternTemplate createStructureTemplate() {
        return TEMPLATE.get();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.INERT_PTFE_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityGasCentrifuge(metaTileEntityId);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.recipeMapWorkable.setParallelLimit(multiblockState.formedRepetitionCount[1]);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("supercritical.machine.gas_centrifuge.tooltip.parallel"));
    }

    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return SCTextures.GAS_CENTRIFUGE_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }
}