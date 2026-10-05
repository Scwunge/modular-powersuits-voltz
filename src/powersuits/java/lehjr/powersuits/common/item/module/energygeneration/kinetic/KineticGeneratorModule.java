package lehjr.powersuits.common.item.module.energygeneration.kinetic;

import lehjr.numina.common.capabilities.module.powermodule.ModuleCategory;
import lehjr.numina.common.capabilities.module.powermodule.ModuleTarget;
import lehjr.numina.common.capabilities.module.tickable.PlayerTickModule;
import lehjr.numina.common.constants.NuminaConstants;
import lehjr.numina.common.utils.ElectricItemUtils;
import lehjr.powersuits.common.config.module.EnergyGenerationModuleConfig;
import lehjr.powersuits.common.constants.MPSConstants;
import lehjr.powersuits.common.item.module.AbstractPowerModule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Generates energy from walking on the ground. Higher "energy generated" settings make the wearer slower.
 */
public class KineticGeneratorModule extends AbstractPowerModule {
    public static class KineticGeneratorTickingCapability extends PlayerTickModule {
        final int tier;

        public KineticGeneratorTickingCapability(ItemStack module, int tier) {
            super(module, ModuleCategory.ENERGY_GENERATION, ModuleTarget.LEGSONLY);
            this.tier = tier;
            addBaseProperty(MPSConstants.ENERGY_GENERATION, EnergyGenerationModuleConfig.kineticGenerator_energyGenerationBase[tier], "FE");
            addTradeoffProperty(MPSConstants.ENERGY_GENERATED, MPSConstants.ENERGY_GENERATION, EnergyGenerationModuleConfig.kineticGenerator_energyGenerationMultiplier[tier], "FE");
            addBaseProperty(NuminaConstants.MOVEMENT_RESISTANCE, EnergyGenerationModuleConfig.kineticGenerator_movementResistanceBase[tier]);
            addTradeoffProperty(MPSConstants.ENERGY_GENERATED, NuminaConstants.MOVEMENT_RESISTANCE, EnergyGenerationModuleConfig.kineticGenerator_movementResistanceMultiplier[tier], "%");
        }

        @Override
        public boolean isAllowed() {
            return EnergyGenerationModuleConfig.kineticGenerator_isAllowed[tier];
        }

        @Override
        public boolean onPlayerTickActive(Player player, Level level, @NotNull ItemStack item, int moduleIndex) {
            // server side, walking on the ground only (not flying, gliding or riding)
            if (level.isClientSide || player.getAbilities().flying || player.isPassenger() || player.isFallFlying() || !player.onGround()) {
                return false;
            }
            // walkDist advances by 0.6 per block walked
            double blocks = (player.walkDist - player.walkDistO) / 0.6D;
            if (blocks > 0 && ElectricItemUtils.getPlayerEnergy(player) < ElectricItemUtils.getMaxPlayerEnergy(player)) {
                ElectricItemUtils.givePlayerEnergy(player, (int) (blocks * applyPropertyModifiers(MPSConstants.ENERGY_GENERATION)), false);
            }
            return false;
        }
    }
}
