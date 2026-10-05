package lehjr.numina.common.network;

import lehjr.numina.common.capabilities.module.powermodule.IPowerModule;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Server-side checks for values that arrive from clients. A modified client can send anything, and on a
 * multiplayer server these values end up in shared items and player stats.
 */
public class PacketValidation {
    /** Furthest block (in blocks) a player may target through a packet: a little over survival/creative reach. */
    public static final double MAX_TARGET_DISTANCE = 10;
    /** Upper bound for client-supplied cosmetic NBT and colour arrays. */
    public static final int MAX_COSMETIC_TAG_BYTES = 64 * 1024;
    public static final int MAX_COLORS = 256;

    /**
     * Tinker sliders are fractions; only names the module declares as trade-offs may be set.
     */
    public static boolean isTradeoff(@Nullable IPowerModule module, String tweakName) {
        if (module == null) {
            return false;
        }
        for (List<IPowerModule.IPropertyModifier> modifiers : module.getPropertyModifiers().values()) {
            for (IPowerModule.IPropertyModifier modifier : modifiers) {
                if (modifier instanceof IPowerModule.PropertyModifierLinearAdditive linear && linear.getTradeoffName().equals(tweakName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** NaN/infinity rejected (returns -1), everything else clamped into [0, 1]. */
    public static double tweakValue(double value) {
        if (!Double.isFinite(value)) {
            return -1;
        }
        return Math.max(0, Math.min(1, value));
    }

    public static boolean isReachable(Player player, BlockPos pos) {
        return player.level().isLoaded(pos) && player.distanceToSqr(Vec3.atCenterOf(pos)) <= MAX_TARGET_DISTANCE * MAX_TARGET_DISTANCE;
    }
}
