package lehjr.powersuits.common.compat.mekanism;

import lehjr.powersuits.common.config.compat.MekanismCompatConfig;
import lehjr.powersuits.common.registration.MPSItems;
import mekanism.api.radiation.capability.IRadiationShielding;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Exposes Mekanism's radiation shielding capability on the power armor.
 * <p>
 * This class references Mekanism API types directly, so it must only be touched after confirming Mekanism is loaded
 * (see {@link MekanismCompat}).
 * <p>
 * Mekanism sums the shielding of every armor slot (see {@code RadiationUtil.getRadiationResistance}), so each piece
 * only contributes its share of a full hazmat suit's protection; the same split Mekanism's own hazmat suit uses.
 */
final class MekanismRadiationShielding {
    private static final double HELMET_SHARE = 0.25;
    private static final double CHESTPLATE_SHARE = 0.4;
    private static final double LEGGINGS_SHARE = 0.2;
    private static final double BOOTS_SHARE = 0.15;

    /**
     * Same name and type as Mekanism's own {@code Capabilities.RADIATION_SHIELDING}; NeoForge hands back the
     * existing instance for a matching name, so Mekanism sees what is registered here.
     */
    private static final ItemCapability<IRadiationShielding, Void> RADIATION_SHIELDING =
        ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(MekanismCompat.MOD_ID, "radiation_shielding"), IRadiationShielding.class);

    private MekanismRadiationShielding() {
    }

    static void register(RegisterCapabilitiesEvent event) {
        registerTier(event, 1, MPSItems.POWER_ARMOR_HELMET_1.get(), MPSItems.POWER_ARMOR_CHESTPLATE_1.get(), MPSItems.POWER_ARMOR_LEGGINGS_1.get(), MPSItems.POWER_ARMOR_BOOTS_1.get());
        registerTier(event, 2, MPSItems.POWER_ARMOR_HELMET_2.get(), MPSItems.POWER_ARMOR_CHESTPLATE_2.get(), MPSItems.POWER_ARMOR_LEGGINGS_2.get(), MPSItems.POWER_ARMOR_BOOTS_2.get());
        registerTier(event, 3, MPSItems.POWER_ARMOR_HELMET_3.get(), MPSItems.POWER_ARMOR_CHESTPLATE_3.get(), MPSItems.POWER_ARMOR_LEGGINGS_3.get(), MPSItems.POWER_ARMOR_BOOTS_3.get());
        registerTier(event, 4, MPSItems.POWER_ARMOR_HELMET_4.get(), MPSItems.POWER_ARMOR_CHESTPLATE_4.get(), MPSItems.POWER_ARMOR_LEGGINGS_4.get(), MPSItems.POWER_ARMOR_BOOTS_4.get());
    }

    private static void registerTier(RegisterCapabilitiesEvent event, int tier, Item helmet, Item chestplate, Item leggings, Item boots) {
        // The config is read when queried rather than here, since capabilities register before the common config loads.
        event.registerItem(RADIATION_SHIELDING, (stack, ctx) -> () -> HELMET_SHARE * MekanismCompatConfig.getTierShielding(tier), helmet);
        event.registerItem(RADIATION_SHIELDING, (stack, ctx) -> () -> CHESTPLATE_SHARE * MekanismCompatConfig.getTierShielding(tier), chestplate);
        event.registerItem(RADIATION_SHIELDING, (stack, ctx) -> () -> LEGGINGS_SHARE * MekanismCompatConfig.getTierShielding(tier), leggings);
        event.registerItem(RADIATION_SHIELDING, (stack, ctx) -> () -> BOOTS_SHARE * MekanismCompatConfig.getTierShielding(tier), boots);
    }
}
