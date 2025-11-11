package com.wanderersoftherift.wotr.core.npc.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Interface for attachments providing mob interactions for any mob
 */
public interface NpcInteraction {
    Codec<NpcInteraction> DIRECT_CODEC = WotrRegistries.MOB_INTERACTIONS.byNameCodec()
            .dispatch(NpcInteraction::getCodec, Function.identity());

    MapCodec<? extends NpcInteraction> getCodec();

    /**
     * Npc interaction triggered on a mob
     *
     * @param mob    The mob interacted with
     * @param player The player interacting
     * @param hand   The hand the player is interacting with
     * @return The interaction result
     */
    InteractionResult interactAsMob(Mob mob, Player player, InteractionHand hand);

    /**
     * Npc interaction triggered on a block
     *
     * @param npc    The identity of the npc
     * @param level  The level the interaction occurred in
     * @param pos    The position of the interacted with block
     * @param block  The block type
     * @param player The player interacting
     * @return Whether the interaction has been consumed
     */
    boolean interactAsBlock(
            Holder<NpcIdentity> npc,
            ServerLevel level,
            @NotNull BlockPos pos,
            @NotNull Block block,
            @NotNull Player player);
}
