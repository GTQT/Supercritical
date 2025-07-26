package supercritical.integration.opencomputers;

import gregtech.api.modules.GregTechModule;
import gregtech.api.util.Mods;
import gregtech.integration.IntegrationSubmodule;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import supercritical.SCValues;
import supercritical.integration.opencomputers.drivers.specific.DriverFissionReactor;
import supercritical.modules.SCModules;

import static gregtech.integration.opencomputers.OpenComputersModule.registerDriver;

@GregTechModule(moduleID = SCModules.MODULE_OC,
        containerID = SCValues.MODID,
        modDependencies = Mods.Names.OPEN_COMPUTERS,
        name = "Supercritical OpenComputers Integration",
        description = "Supercritical OpenComputers Integration Module")
public class OpenComputersModule extends IntegrationSubmodule {

    @Override
    public void init(FMLInitializationEvent event) {
        getLogger().info("Registering OpenComputers Drivers...");
        registerDriver(new DriverFissionReactor());
    }
}
