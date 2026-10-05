package lehjr.powersuits.common.registration;

import lehjr.numina.common.config.ConfigHelper;
import lehjr.powersuits.client.config.MPSClientConfig;
import lehjr.powersuits.common.config.ArmorConfig;
import lehjr.powersuits.common.config.MPSCommonConfig;
import lehjr.powersuits.common.config.PowerFistConfig;
import lehjr.powersuits.common.config.module.ArmorModuleConfig;
import lehjr.powersuits.common.config.module.AxeModuleConfig;
import lehjr.powersuits.common.config.module.CosmeticModuleConfig;
import lehjr.powersuits.common.config.module.EnergyGenerationModuleConfig;
import lehjr.powersuits.common.config.module.EnvironmentalModuleConfig;
import lehjr.powersuits.common.config.module.FluidStorageConfig;
import lehjr.powersuits.common.config.module.HoeModuleConfig;
import lehjr.powersuits.common.config.module.MiningEnchantmentModuleConfig;
import lehjr.powersuits.common.config.module.MiningEnhancementModuleConfig;
import lehjr.powersuits.common.config.module.MovementModuleConfig;
import lehjr.powersuits.common.config.module.PickaxeModuleConfig;
import lehjr.powersuits.common.config.module.ShovelModuleConfig;
import lehjr.powersuits.common.config.module.ToolModuleConfig;
import lehjr.powersuits.common.config.module.VisionModuleConfig;
import lehjr.powersuits.common.config.module.WeaponModuleConfig;
import lehjr.powersuits.common.constants.MPSConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = MPSConstants.MOD_ID)
public class MPSConfigurations {
    public static void setup(ModContainer modContainer) {
        // Client
        registerClient(modContainer, MPSClientConfig.CLIENT_SPEC, "powersuits-client-only.toml");

        // General ----------------------------------------------------------------------------------------------------
        registerServer(modContainer, MPSCommonConfig.MPS_GENERAL_SPEC, "general.toml");

        // Armor ------------------------------------------------------------------------------------------------------
        registerServer(modContainer, ArmorConfig.ARMOR_CONFIG_SPEC, "items/armor.toml");

        // PowerFist --------------------------------------------------------------------------------------------------
        registerServer(modContainer, PowerFistConfig.POWER_FIST_CONFIG_SPEC, "items/powerfist.toml");

        // Modules ----------------------------------------------------------------------------------------------------
        // Armor
        registerServer(modContainer, ArmorModuleConfig.ARMOR_MODULE_CONFIG_SPEC, "items/modules/armor.toml");

        // Cosmetic
        registerServer(modContainer, CosmeticModuleConfig.COSMETIC_MODULE_CONFIG_SPEC, "items/modules/cosmetic.toml");
        // Tool - Axe
        registerServer(modContainer, AxeModuleConfig.MPS_AXE_MODULE_SPEC, "items/modules/tool_axe.toml");

        // Energy Generation
        registerServer(modContainer, EnergyGenerationModuleConfig.MPS_GENERATOR_MODULE_SPEC, "items/modules/energy_generation.toml");

        // Environmental
        registerServer(modContainer, EnvironmentalModuleConfig.ENVIRONMENTAL_MODULE_SPEC, "items/modules/environmental.toml");

        // FLuid Storage
        registerServer(modContainer, FluidStorageConfig.FLUID_STORAGE_MODULE_SPEC, "items/modules/fluid_storage.toml");

        // Tool - Hoe
        registerServer(modContainer, HoeModuleConfig.MPS_HOE_MODULE_SPEC, "items/modules/tool_rototiller.toml");

        // Mining Enchantment
        registerServer(modContainer, MiningEnchantmentModuleConfig.MINING_ENCHANTMENT_MODULE_CONFIG_SPEC, "items/modules/mining_enchantment.toml");

        // Mining Enhancement
        registerServer(modContainer, MiningEnhancementModuleConfig.MINING_ENHANCEMENT_MODULE_SPEC, "items/modules/mining_enhancement.toml");

        // Movement
        registerServer(modContainer, MovementModuleConfig.MPS_MOVEMENGT_MODULE_SPEC, "items/modules/movement.toml");
        // Tool - Pickaxe
        registerServer(modContainer, PickaxeModuleConfig.MPS_PICKAXE_MODULE_SPEC, "items/modules/tool_pickaxe.toml");
        // Tool - Shovel
        registerServer(modContainer, ShovelModuleConfig.MPS_SHOVEL_MODULE_SPEC, "items/modules/tool_shovels.toml");
        // Tool -Misc
        registerServer(modContainer, ToolModuleConfig.MPS_TOOL_MODULE_SPEC, "items/modules/tool.toml");
        // Vision
        registerServer(modContainer, VisionModuleConfig.MPS_VISION_MODULE_SPEC, "items/modules/vision.toml");
        // Weapon
        registerServer(modContainer, WeaponModuleConfig.MPS_WEAPON_MODULE_SPEC, "items/modules/weapon.toml");
    }

    static void registerClient(ModContainer modContainer, ModConfigSpec spec, String path) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, spec, ConfigHelper.setupConfigFile(path ,MPSConstants.MOD_ID).getAbsolutePath());
    }

    static void registerCommon(ModContainer modContainer, ModConfigSpec spec, String path) {
        modContainer.registerConfig(ModConfig.Type.COMMON, spec, ConfigHelper.setupConfigFile(path ,MPSConstants.MOD_ID).getAbsolutePath());
    }

    /**
     * Gameplay values are SERVER configs so a server's settings are synced to every client (movement and module
     * numbers are also used client side). They live per world in serverconfig/powersuits/; a pack can ship
     * defaults in defaultconfigs/powersuits/.
     */
    static void registerServer(ModContainer modContainer, ModConfigSpec spec, String path) {
        modContainer.registerConfig(ModConfig.Type.SERVER, spec, MPSConstants.MOD_ID + "/" + path);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        // values are already gone when Unloading fires (server stop, leaving a server); reading them would throw
        if (event instanceof ModConfigEvent.Unloading) {
            return;
        }
        MPSCommonConfig.onLoad(event);
        // Modular Items
        ArmorConfig.onLoad(event);
        PowerFistConfig.onLoad(event);

        // Modules
        ArmorModuleConfig.onLoad(event);

        CosmeticModuleConfig.onLoad(event);

        EnergyGenerationModuleConfig.onLoad(event);

        EnvironmentalModuleConfig.onLoad(event);

        FluidStorageConfig.onLoad(event);

        MiningEnchantmentModuleConfig.onLoad(event);

        MiningEnhancementModuleConfig.onLoad(event);

        MovementModuleConfig.onLoad(event);

        AxeModuleConfig.onLoad(event);
        HoeModuleConfig.onLoad(event);
        PickaxeModuleConfig.onLoad(event);
        ShovelModuleConfig.onLoad(event);
        ToolModuleConfig.onLoad(event);

        VisionModuleConfig.onLoad(event);
        WeaponModuleConfig.onLoad(event);
    }
}
