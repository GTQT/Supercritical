package supercritical.integration.top;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;
import supercritical.integration.top.provider.NuclearReactorInfoProvider;

public class TheOneProbeModule {
    public static void init() {
        ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new NuclearReactorInfoProvider());
    }
}
