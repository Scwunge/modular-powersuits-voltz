package lehjr.powersuits.common.compat.mekanism;

import lehjr.powersuits.common.constants.MPSConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Entry point for the optional Mekanism integration. Mekanism is a soft dependency: nothing in this package may be
 * loaded unless {@link #isLoaded()} is true. This class itself must stay free of Mekanism types for that reason.
 */
@EventBusSubscriber(modid = MPSConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class MekanismCompat {
    public static final String MOD_ID = "mekanism";

    private MekanismCompat() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (isLoaded()) {
            MekanismRadiationShielding.register(event);
        }
    }
}
