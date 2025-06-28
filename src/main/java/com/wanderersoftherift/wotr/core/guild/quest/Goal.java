package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.codec.LaxRegistryCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public abstract class Goal {
    public static final Codec<Goal> DIRECT_CODEC = WotrRegistries.GOAL_TYPES.byNameCodec()
            .dispatch(Goal::getCodec, Function.identity());
    public static final Codec<Holder<Goal>> CODEC = LaxRegistryCodec.create(WotrRegistries.Keys.GOALS);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Goal>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.GOALS);

    public abstract MapCodec<? extends Goal> getCodec();
}
