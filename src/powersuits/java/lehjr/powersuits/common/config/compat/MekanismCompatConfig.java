package lehjr.powersuits.common.config.compat;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Settings for the optional Mekanism integration. Nothing here has any effect unless Mekanism is installed.
 */
public class MekanismCompatConfig {
    private static final ModConfigSpec.Builder builder = new ModConfigSpec.Builder().comment("Mekanism integration (ignored when Mekanism is not installed)").push("Mekanism");

    private static final ModConfigSpec.BooleanValue RADIATION_SHIELDING_ENABLED = builder
        .comment("Power armor protects the wearer from Mekanism radiation. Each piece contributes its share of a full hazmat suit, scaled by the armor tier below.")
        .define("radiationShieldingEnabled", true);

    private static final ModConfigSpec.DoubleValue TIER_1_SHIELDING = builder
        .comment("Fraction of a hazmat suit's protection provided by a complete tier 1 power armor set (0 = none, 1 = same as hazmat).")
        .defineInRange("tier1ShieldingFraction", 0.25, 0.0, 1.0);
    private static final ModConfigSpec.DoubleValue TIER_2_SHIELDING = builder
        .comment("Fraction of a hazmat suit's protection provided by a complete tier 2 power armor set.")
        .defineInRange("tier2ShieldingFraction", 0.5, 0.0, 1.0);
    private static final ModConfigSpec.DoubleValue TIER_3_SHIELDING = builder
        .comment("Fraction of a hazmat suit's protection provided by a complete tier 3 power armor set.")
        .defineInRange("tier3ShieldingFraction", 0.75, 0.0, 1.0);
    private static final ModConfigSpec.DoubleValue TIER_4_SHIELDING = builder
        .comment("Fraction of a hazmat suit's protection provided by a complete tier 4 power armor set.")
        .defineInRange("tier4ShieldingFraction", 1.0, 0.0, 1.0);

    public static final ModConfigSpec MEKANISM_COMPAT_SPEC = builder.build();

    public static boolean radiationShieldingEnabled = true;
    private static final double[] tierShielding = {0.25, 0.5, 0.75, 1.0};

    /**
     * @param tier armor tier, 1 to 4
     * @return fraction (0 - 1) of a full hazmat suit's protection a complete set of this tier provides
     */
    public static double getTierShielding(int tier) {
        if (!radiationShieldingEnabled || tier < 1 || tier > tierShielding.length) {
            return 0;
        }
        return tierShielding[tier - 1];
    }

    public static void onLoad(final ModConfigEvent event) {
        // values are already gone when Unloading fires; reading them would throw
        if (event instanceof ModConfigEvent.Unloading) {
            return;
        }
        if (event.getConfig().getSpec() == MEKANISM_COMPAT_SPEC) {
            radiationShieldingEnabled = RADIATION_SHIELDING_ENABLED.get();
            tierShielding[0] = TIER_1_SHIELDING.get();
            tierShielding[1] = TIER_2_SHIELDING.get();
            tierShielding[2] = TIER_3_SHIELDING.get();
            tierShielding[3] = TIER_4_SHIELDING.get();
        }
    }
}
