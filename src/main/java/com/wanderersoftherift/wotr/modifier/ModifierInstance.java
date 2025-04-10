package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;


public record ModifierInstance(Holder<Modifier> modifier, float roll) {

    public static Codec<ModifierInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Modifier.CODEC.fieldOf("modifier").forGetter(ModifierInstance::modifier),
            Codec.FLOAT.fieldOf("roll").forGetter(ModifierInstance::roll)
    ).apply(inst, ModifierInstance::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifierInstance> STREAM_CODEC = StreamCodec.composite(
            Modifier.STREAM_CODEC,
            ModifierInstance::modifier,
            ByteBufCodecs.FLOAT,
            ModifierInstance::roll,
            ModifierInstance::new
    );

    public static ModifierInstance of(Holder<Modifier> modifier, RandomSource random) {
        return new ModifierInstance(modifier, random.nextFloat());
    }
}
