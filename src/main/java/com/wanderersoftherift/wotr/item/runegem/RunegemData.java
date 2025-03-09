package com.wanderersoftherift.wotr.item.runegem;

import com.wanderersoftherift.wotr.init.ModModifiers;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.TagKey;

public record RunegemData(RuneGemShape shape, TagKey<Modifier> tag, RuneGemTier tier) {
    public static Codec<RunegemData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RuneGemShape.CODEC.fieldOf("shape").forGetter(RunegemData::shape),
            TagKey.codec(ModModifiers.MODIFIER_KEY).fieldOf("tag").forGetter(RunegemData::tag),
            RuneGemTier.CODEC.fieldOf("tier").forGetter(RunegemData::tier)
    ).apply(inst, RunegemData::new));
}
