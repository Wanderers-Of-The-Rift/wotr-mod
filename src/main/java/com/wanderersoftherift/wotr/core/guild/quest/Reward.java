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

public abstract class Reward {
    public static final Codec<Reward> DIRECT_CODEC = WotrRegistries.REWARD_TYPES.byNameCodec()
            .dispatch(Reward::getCodec, Function.identity());
    public static final Codec<Holder<Reward>> CODEC = LaxRegistryCodec.create(WotrRegistries.Keys.REWARDS);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Reward>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.REWARDS);

    public abstract MapCodec<? extends Reward> getCodec();
}
