package com.wanderersoftherift.wotr.core.npc.interaction;

import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.gui.menu.ValidatingLevelAccess;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * NpcInteraction that opens a menu.
 */
public interface MenuInteraction extends NpcInteraction {

    /**
     * Generic interaction across block/mob, for opening a menu
     * 
     * @param npc    The npc interacted with
     * @param access The level access to open the menu with, tracking the thing being interacted with
     * @param level  The level the menu is opening in
     * @param player The player to open the menu for
     */
    void interact(Holder<NpcIdentity> npc, ValidatingLevelAccess access, ServerLevel level, Player player);

    default InteractionResult interactAsMob(Mob mob, Player player, InteractionHand hand) {
        if (player.isCrouching()) {
            return InteractionResult.PASS;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        Holder<NpcIdentity> npc = mob.getData(WotrAttachments.NPC_IDENTITY)
                .identity()
                .orElse(level.registryAccess().get(NpcIdentity.DEFAULT).orElseThrow());
        interact(npc, ValidatingLevelAccess.create(mob), level, player);

        return InteractionResult.CONSUME;
    }

    default void interactAsBlock(
            Holder<NpcIdentity> npc,
            ServerLevel level,
            @NotNull BlockPos pos,
            @NotNull Block block,
            @NotNull Player player) {
        interact(npc, ValidatingLevelAccess.create(level, pos, block), level, player);
    }
}
