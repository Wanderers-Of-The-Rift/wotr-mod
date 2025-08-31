package com.wanderersoftherift.wotr.entity.npc;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

/**
 * No-op mob interaction attachment
 */
public final class NoInteract implements MobInteraction {

    public static final NoInteract INSTANCE = new NoInteract();
    public static final MapCodec<NoInteract> CODEC = MapCodec.unit(INSTANCE);

    private NoInteract() {
    }

    @Override
    public MapCodec<? extends MobInteraction> getCodec() {
        return CODEC;
    }

    @Override
    public InteractionResult interact(Mob mob, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }
}
