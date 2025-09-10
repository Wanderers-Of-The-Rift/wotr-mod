package com.wanderersoftherift.wotr.entity.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

/**
 * Interface for attachments providing mob interactions for any mob
 */
public interface MobInteraction {
    Codec<MobInteraction> DIRECT_CODEC = WotrRegistries.MOB_INTERACTIONS.byNameCodec()
            .dispatch(MobInteraction::getCodec, Function.identity());

    MapCodec<? extends MobInteraction> getCodec();

    InteractionResult interact(Mob mob, Player player, InteractionHand hand);
}
