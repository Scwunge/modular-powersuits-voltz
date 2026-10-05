package lehjr.powersuits.common.item.module.energygeneration.combustion;

import lehjr.numina.common.capabilities.module.powermodule.ModuleCategory;
import lehjr.numina.common.capabilities.module.powermodule.ModuleTarget;
import lehjr.numina.common.capabilities.module.tickable.PlayerTickModule;
import lehjr.numina.common.utils.ElectricItemUtils;
import lehjr.numina.common.utils.HeatUtils;
import lehjr.numina.common.utils.TagUtils;
import lehjr.powersuits.common.config.module.EnergyGenerationModuleConfig;
import lehjr.powersuits.common.constants.MPSConstants;
import lehjr.powersuits.common.item.module.AbstractPowerModule;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Burns furnace fuel from the player's inventory to charge the suit. Fuel is only taken while the suit isn't full.
 * Work is done once a second so the module stack is only rewritten once a second.
 */
public class CombustionGeneratorModule extends AbstractPowerModule {
    public static class CombustionGeneratorTickingCapability extends PlayerTickModule {
        static final int TICKS_PER_ACTION = 20;
        final int tier;

        public CombustionGeneratorTickingCapability(ItemStack module, int tier) {
            super(module, ModuleCategory.ENERGY_GENERATION, ModuleTarget.TORSOONLY);
            this.tier = tier;
            addBaseProperty(MPSConstants.ENERGY_PER_TICK, EnergyGenerationModuleConfig.combustionGenerator_energyPerTick[tier], "FE/t");
            addBaseProperty(MPSConstants.HEAT_GENERATION, EnergyGenerationModuleConfig.combustionGenerator_heatPerTick[tier]);
            addBaseProperty(MPSConstants.FUEL_BURN_RATE, EnergyGenerationModuleConfig.combustionGenerator_fuelTicksPerTick[tier], "x");
        }

        @Override
        public boolean isAllowed() {
            return EnergyGenerationModuleConfig.combustionGenerator_isAllowed[tier];
        }

        @Override
        public boolean onPlayerTickActive(Player player, Level level, @NotNull ItemStack item, int moduleIndex) {
            if (level.isClientSide || level.getGameTime() % TICKS_PER_ACTION != 0) {
                return false;
            }

            double energyPerTick = applyPropertyModifiers(MPSConstants.ENERGY_PER_TICK);
            int burnRate = Math.max(1, (int) applyPropertyModifiers(MPSConstants.FUEL_BURN_RATE));
            double room = ElectricItemUtils.getMaxPlayerEnergy(player) - ElectricItemUtils.getPlayerEnergy(player);
            if (room <= 0 || energyPerTick <= 0) {
                return false;
            }

            ItemStack module = getModule();
            int fuel = TagUtils.getModuleInt(module, MPSConstants.FUEL_TICKS_REMAINING);
            int wanted = TICKS_PER_ACTION * burnRate;
            if (fuel < wanted) {
                fuel += takeFuel(player);
            }
            if (fuel <= 0) {
                return false;
            }

            int burned = Math.min(fuel, wanted);
            // fraction of a full second of burning that this fuel covers
            double fraction = (double) burned / wanted;
            ElectricItemUtils.givePlayerEnergy(player, Math.min(room, TICKS_PER_ACTION * energyPerTick * fraction), false);
            HeatUtils.heatPlayer(player, TICKS_PER_ACTION * applyPropertyModifiers(MPSConstants.HEAT_GENERATION) * fraction);
            TagUtils.setModuleInt(module, MPSConstants.FUEL_TICKS_REMAINING, fuel - burned);
            return true;
        }

        /**
         * Consumes one fuel item from the main inventory (never armor or offhand) and returns its burn time.
         * Items with a crafting remainder (lava bucket) leave it behind.
         */
        static int takeFuel(Player player) {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.items.size(); i++) {
                ItemStack stack = inventory.items.get(i);
                if (stack.isEmpty()) {
                    continue;
                }
                int burnTime = stack.getBurnTime(RecipeType.SMELTING);
                if (burnTime <= 0) {
                    continue;
                }
                ItemStack remainder = stack.getCraftingRemainingItem();
                stack.shrink(1);
                if (!remainder.isEmpty()) {
                    if (stack.isEmpty()) {
                        inventory.items.set(i, remainder);
                    } else if (!inventory.add(remainder)) {
                        player.drop(remainder, false);
                    }
                }
                return burnTime;
            }
            return 0;
        }
    }
}
