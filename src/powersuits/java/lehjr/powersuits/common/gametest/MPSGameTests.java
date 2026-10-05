package lehjr.powersuits.common.gametest;

import lehjr.numina.common.capabilities.inventory.modechanging.IModeChangingItem;
import lehjr.numina.common.capabilities.inventory.modularitem.IModularItem;
import lehjr.numina.common.network.PacketValidation;
import lehjr.numina.common.registration.NuminaCapabilities;
import lehjr.numina.common.registration.NuminaItems;
import lehjr.numina.common.utils.ElectricItemUtils;
import lehjr.numina.common.utils.ItemUtils;
import lehjr.powersuits.common.config.module.EnergyGenerationModuleConfig;
import lehjr.powersuits.common.constants.MPSConstants;
import lehjr.powersuits.common.registration.MPSItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import com.mojang.authlib.GameProfile;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Server-side checks for the Voltz port. Only run on a GameTest server with the "powersuits" namespace enabled:
 * gradlew runGameTestServer
 */
@GameTestHolder(MPSConstants.MOD_ID)
@PrefixGameTestTemplate(false)
@EventBusSubscriber(modid = MPSConstants.MOD_ID)
public class MPSGameTests {
    static final String TEMPLATE = "empty5x4x5";

    /** Server configs are loaded on the server (gameplay values are SERVER configs now). */
    @GameTest(template = TEMPLATE)
    public static void serverConfigLoaded(GameTestHelper helper) {
        helper.assertTrue(EnergyGenerationModuleConfig.solarGeneratorModule_1_energyGenerationDay > 0, "energy generation server config not loaded");
        helper.assertTrue(EnergyGenerationModuleConfig.combustionGenerator_energyPerTick[1] > 0, "combustion server config not loaded");
        helper.succeed();
    }

    /** Tinker requests from clients may only touch a module's declared trade-offs, with slider-range values. */
    @GameTest(template = TEMPLATE)
    public static void tweakValidation(GameTestHelper helper) {
        var kinetic = new ItemStack(MPSItems.KINETIC_GENERATOR_MODULE_1.get()).getCapability(NuminaCapabilities.Module.POWER_MODULE);
        helper.assertTrue(PacketValidation.isTradeoff(kinetic, MPSConstants.ENERGY_GENERATED), "real trade-off rejected");
        helper.assertTrue(!PacketValidation.isTradeoff(kinetic, MPSConstants.ENERGY_GENERATION), "derived property accepted as a tweak");
        helper.assertTrue(!PacketValidation.isTradeoff(kinetic, MPSConstants.MOVEMENT_SPEED), "unrelated key accepted as a tweak");
        helper.assertTrue(PacketValidation.tweakValue(1000) == 1 && PacketValidation.tweakValue(-5) == 0 && PacketValidation.tweakValue(Double.NaN) < 0, "tweak values not clamped");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void combustionGeneratorBurnsFuel(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        equip(player, EquipmentSlot.CHEST, MPSItems.POWER_ARMOR_CHESTPLATE_4.get(),
            new ItemStack(NuminaItems.BATTERY_1.get()), new ItemStack(MPSItems.COMBUSTION_GENERATOR_MODULE_1.get()));
        player.getInventory().add(new ItemStack(Items.COAL, 4));

        helper.onEachTick(() -> tickArmor(player));
        helper.succeedWhen(() -> {
            helper.assertTrue(ElectricItemUtils.getPlayerEnergy(player) > 0, "no energy generated " + describe(player));
            helper.assertTrue(player.getInventory().countItem(Items.COAL) == 3, "expected exactly one coal burned, have " + player.getInventory().countItem(Items.COAL));
        });
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void combustionGeneratorIdlesWhenFull(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        equip(player, EquipmentSlot.CHEST, MPSItems.POWER_ARMOR_CHESTPLATE_4.get(),
            new ItemStack(NuminaItems.BATTERY_1.get()), new ItemStack(MPSItems.COMBUSTION_GENERATOR_MODULE_1.get()));
        ElectricItemUtils.givePlayerEnergy(player, ElectricItemUtils.getMaxPlayerEnergy(player), false);
        player.getInventory().add(new ItemStack(Items.COAL, 4));

        helper.onEachTick(() -> tickArmor(player));
        helper.runAtTickTime(50, () -> {
            helper.assertTrue(player.getInventory().countItem(Items.COAL) == 4, "burned fuel while the suit was full");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 40)
    public static void kineticGeneratorChargesWhileWalking(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        equip(player, EquipmentSlot.CHEST, MPSItems.POWER_ARMOR_CHESTPLATE_4.get(), new ItemStack(NuminaItems.BATTERY_1.get()));
        equip(player, EquipmentSlot.LEGS, MPSItems.POWER_ARMOR_LEGGINGS_4.get(), new ItemStack(MPSItems.KINETIC_GENERATOR_MODULE_1.get()));

        helper.onEachTick(() -> {
            // pretend the player walked one block this tick
            player.setOnGround(true);
            player.walkDistO = player.walkDist;
            player.walkDist += 0.6F;
            tickArmor(player);
        });
        helper.succeedWhen(() -> helper.assertTrue(ElectricItemUtils.getPlayerEnergy(player) > 0, "no energy from walking " + describe(player)));
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void solarGeneratorChargesInDaylight(GameTestHelper helper) {
        helper.getLevel().setDayTime(6000);
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setPos(Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(2, 1, 2))));
        equip(player, EquipmentSlot.CHEST, MPSItems.POWER_ARMOR_CHESTPLATE_4.get(), new ItemStack(NuminaItems.BATTERY_1.get()));
        equip(player, EquipmentSlot.HEAD, MPSItems.POWER_ARMOR_HELMET_4.get(), new ItemStack(MPSItems.SOLAR_GENERATOR_MODULE_1.get()));

        helper.onEachTick(() -> tickArmor(player));
        helper.succeedWhen(() -> helper.assertTrue(ElectricItemUtils.getPlayerEnergy(player) > 0, "no solar energy at noon " + describe(player)));
    }

    /** Vein miner must not break blocks a protection mod refuses, but still breaks the rest of the vein. */
    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void veinMinerRespectsProtection(GameTestHelper helper) {
        BlockPos a = new BlockPos(1, 1, 2), b = new BlockPos(2, 1, 2), protectedRel = new BlockPos(3, 1, 2);
        for (BlockPos pos : new BlockPos[]{a, b, protectedRel}) {
            helper.setBlock(pos, Blocks.IRON_ORE);
        }

        // survival player (the mock player is forced creative, and creative vein mining stops after one block)
        ServerPlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "mps-gametest"));
        player.setPos(Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(2, 1, 1))));
        equip(player, EquipmentSlot.CHEST, MPSItems.POWER_ARMOR_CHESTPLATE_4.get(), new ItemStack(NuminaItems.BATTERY_4.get()));
        ElectricItemUtils.givePlayerEnergy(player, ElectricItemUtils.getMaxPlayerEnergy(player), false);
        ItemStack fist = install(new ItemStack(MPSItems.POWER_FIST_4.get()),
            new ItemStack(MPSItems.DIAMOND_PICKAXE_MODULE.get()), new ItemStack(MPSItems.VEIN_MINER_MODULE.get()));
        IModeChangingItem fistCap = NuminaCapabilities.getModeChangingModularItem(fist);
        if (fistCap == null) {
            throw new GameTestAssertException("power fist has no mode changing capability");
        }
        fistCap.setActiveMode(slotOf(fistCap, MPSItems.VEIN_MINER_MODULE.get()));
        // vein size is a tinker slider (default: 1 block); max it like a player would
        fistCap.setModuleDouble(ItemUtils.getRegistryName(MPSItems.VEIN_MINER_MODULE.get()), MPSConstants.SELECTIVE_MINER_LIMIT, 1.0);
        player.setItemSlot(EquipmentSlot.MAINHAND, fist);

        protectedPos = helper.absolutePos(protectedRel);
        try {
            BlockPos abs = helper.absolutePos(a);
            fistCap.onBlockStartBreak(fist, new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false), player, helper.getLevel());
        } finally {
            protectedPos = null;
        }

        helper.assertBlockPresent(Blocks.AIR, a);
        helper.assertBlockPresent(Blocks.AIR, b);
        helper.assertBlockPresent(Blocks.IRON_ORE, protectedRel);
        helper.succeed();
    }

    // stands in for a claim mod during veinMinerRespectsProtection
    @Nullable
    static BlockPos protectedPos;

    @SubscribeEvent
    static void onBreak(BlockEvent.BreakEvent event) {
        if (protectedPos != null && event.getPos().equals(protectedPos)) {
            event.setCanceled(true);
        }
    }

    // helpers ---------------------------------------------------------------------------------------------------------
    static void equip(Player player, EquipmentSlot slot, Item host, ItemStack... modules) {
        player.setItemSlot(slot, install(new ItemStack(host), modules));
    }

    static ItemStack install(ItemStack host, ItemStack... modules) {
        for (ItemStack module : modules) {
            IModularItem modularItem = NuminaCapabilities.getModularItemOrModeChangingCapability(host);
            if (modularItem == null) {
                throw new GameTestAssertException(host + " is not a modular item");
            }
            boolean installed = false;
            for (int i = 0; i < modularItem.getSlots() && !installed; i++) {
                // same check the install/salvage GUI uses (batteries only go in energy storage slots, etc.)
                installed = modularItem.isModuleValidForPlacement(i, module) && modularItem.insertItem(i, module.copy(), false).isEmpty();
            }
            if (!installed) {
                throw new GameTestAssertException("could not install " + module + " in " + host);
            }
            NuminaCapabilities.getModularItemOrModeChangingCapability(host).toggleModule(ItemUtils.getRegistryName(module.getItem()), true);
        }
        return host;
    }

    static int slotOf(IModularItem modularItem, Item module) {
        for (int i = 0; i < modularItem.getSlots(); i++) {
            if (modularItem.getStackInSlot(i).is(module)) {
                return i;
            }
        }
        throw new GameTestAssertException(module + " not installed");
    }

    static String describe(Player player) {
        StringBuilder sb = new StringBuilder("[energy " + ElectricItemUtils.getPlayerEnergy(player) + "/" + ElectricItemUtils.getMaxPlayerEnergy(player));
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS}) {
            IModularItem modularItem = NuminaCapabilities.getModularItem(player.getItemBySlot(slot));
            if (modularItem == null) {
                continue;
            }
            for (int i = 0; i < modularItem.getSlots(); i++) {
                ItemStack module = modularItem.getStackInSlot(i);
                if (!module.isEmpty()) {
                    var pm = modularItem.getModuleCapability(module);
                    sb.append(" ").append(slot.getName()).append(":").append(ItemUtils.getRegistryName(module.getItem()).getPath())
                        .append(pm == null ? "(no cap)" : "(allowed=" + pm.isAllowed() + ",online=" + pm.isModuleOnline() + ")");
                }
            }
        }
        return sb.append("]").toString();
    }

    /** Mock players aren't ticked by the server, so run the armor's module ticks directly. */
    static void tickArmor(Player player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = player.getItemBySlot(slot);
            IModularItem modularItem = NuminaCapabilities.getModularItem(stack);
            if (modularItem != null) {
                modularItem.tick(player, player.level(), stack);
            }
        }
    }
}
