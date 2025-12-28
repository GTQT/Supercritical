package supercritical.common.metatileentities.multi.nuclearReactor;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import supercritical.api.capability.INuclearExtend;
import supercritical.api.metatileentity.multiblock.SCMultiblockAbility;

import java.util.List;

public class MetaTileEntityNuclearExtend extends MetaTileEntityMultiblockPart
        implements IMultiblockAbilityPart<INuclearExtend>, INuclearExtend {

    public MetaTileEntityNuclearExtend(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 4);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityNuclearExtend(metaTileEntityId);
    }

    @Override
    public MultiblockAbility<INuclearExtend> getAbility() {
        return SCMultiblockAbility.REACTOR_EXTEND_HATCH;
    }

    @Override
    public void registerAbilities(AbilityInstances abilityInstances) {
        abilityInstances.add(this);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            Textures.PIPE_IN_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
            Textures.ITEM_HATCH_INPUT_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
        }
    }

    @Override
    public void addInformation(ItemStack stack, World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(TextFormatting.GREEN + I18n.format("-工作原理："));
        tooltip.add("通过安装燃料拓展仓来扩展核反应堆的内部容量。");
        tooltip.add("每个燃料拓展仓将反应堆内部空间的X和Y方向各增加1格。");
        tooltip.add("安装多个拓展仓可以叠加效果，最大内部空间可达9x9。");
    }
}
