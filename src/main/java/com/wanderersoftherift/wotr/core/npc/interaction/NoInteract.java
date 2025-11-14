package com.wanderersoftherift.wotr.core.npc.interaction;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
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
 * No-op npc interaction attachment
 */
public final class NoInteract implements NpcInteraction {

    public static final NoInteract INSTANCE = new NoInteract();
    public static final MapCodec<NoInteract> CODEC = MapCodec.unit(INSTANCE);

    private NoInteract() {
    }

    @Override
    public MapCodec<? extends NpcInteraction> getCodec() {
        return CODEC;
    }

    @Override
    public InteractionResult interactAsMob(Mob mob, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interactAsBlock(
            Holder<NpcIdentity> npc,
            ServerLevel level,
            @NotNull BlockPos pos,
            @NotNull Block block,
            @NotNull Player player) {
        return InteractionResult.PASS;
    }
}
