package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import net.minecraft.core.Holder;

public record TieredModifier(int tier, Holder<Modifier> modifier) {
    public static final Codec<TieredModifier> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    Codec.INT.fieldOf("tier").forGetter(TieredModifier::tier),
                    Modifier.CODEC.fieldOf("modifier").forGetter(TieredModifier::modifier))
            .apply(inst, TieredModifier::new));
}
