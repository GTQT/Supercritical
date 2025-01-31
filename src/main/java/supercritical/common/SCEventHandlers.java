package supercritical.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.jetbrains.annotations.ApiStatus;

import gregtech.api.unification.material.event.MaterialEvent;
import gregtech.api.unification.material.event.PostMaterialEvent;
import supercritical.SCValues;
import supercritical.api.unification.material.SCMaterialFlagAddition;
import supercritical.api.unification.material.SCMaterialPropertyAddition;
import supercritical.api.unification.material.SCMaterials;
import supercritical.api.unification.ore.SCOrePrefix;
import supercritical.common.materials.*;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = SCValues.MODID)
public final class SCEventHandlers {

    private SCEventHandlers() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerMaterials(MaterialEvent event) {
        SCMaterials.register();
        SCOrePrefix.init();
        SCMaterialFlagAddition.init();
        SCMaterialPropertyAddition.init();
    }

    @SubscribeEvent
    public static void registerMaterialsPost(PostMaterialEvent event) {
        if (SCConfigHolder.misc.enableMaterialModifications) {
            MaterialModifications.init();
        }
    }
}
