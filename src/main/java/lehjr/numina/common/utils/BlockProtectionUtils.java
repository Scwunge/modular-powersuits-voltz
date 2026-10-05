package lehjr.numina.common.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

/**
 * Area-breaking modules (vein miner, tunnel bore, selective miner) break blocks the player never clicked.
 * Vanilla only fires BlockEvent.BreakEvent for the clicked block, so claim/protection mods never see the rest.
 * Fire it for each extra block and skip the ones a protection mod cancels.
 */
public class BlockProtectionUtils {
    /**
     * @return true if the player may break the block at pos. Always true on the client.
     */
    public static boolean canPlayerBreak(Level level, Player player, BlockPos pos) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return true;
        }
        return !CommonHooks.fireBlockBreak(level, serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer, pos, level.getBlockState(pos)).isCanceled();
    }
}
