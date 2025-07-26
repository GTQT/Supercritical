package supercritical.mixins.gregtech;

import gregtech.api.unification.ore.OrePrefix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import supercritical.api.unification.ore.OrePrefixExtension;

import java.util.function.Function;

@Mixin(value = OrePrefix.class, remap = false)
public abstract class MixinOrePrefix implements OrePrefixExtension {

    @Unique
    public Function<Double, Double> sc$radiationDamageFunction = null;

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public Function<Double, Double> getRadiationDamageFunction() {
        return sc$radiationDamageFunction;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setRadiationDamageFunction(Function<Double, Double> function) {
        this.sc$radiationDamageFunction = function;
    }
}
