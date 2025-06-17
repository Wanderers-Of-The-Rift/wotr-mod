package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TieredModifier(int tier, Holder<Modifier> modifier) {
    public static final Codec<TieredModifier> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("tier").forGetter(TieredModifier::tier),
            Modifier.CODEC.fieldOf("modifier").forGetter(TieredModifier::modifier)
    ).apply(inst, TieredModifier::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TieredModifier> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TieredModifier::tier, ByteBufCodecs.holderRegistry(WotrRegistries.Keys.MODIFIERS),
            TieredModifier::modifier, TieredModifier::new
    );

    public Component getName() {
        if (modifier().getKey() == null) {
            return Component.empty();
        }
        MutableComponent modifier = Component
                .translatable(WanderersOfTheRift.translationId("modifier", modifier().getKey().location()));
        modifier.append(" (T");
        modifier.append(String.valueOf(tier));
        modifier.append(")");
        return modifier;
    }
}
