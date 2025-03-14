package com.wanderersoftherift.wotr.item.runegem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModDatapackRegistries;
import com.wanderersoftherift.wotr.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record RunegemData(RunegemShape shape, TagKey<Modifier> tag, RunegemTier tier) {
    public static Codec<RunegemData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RunegemShape.CODEC.fieldOf("shape").forGetter(RunegemData::shape),
            TagKey.codec(ModDatapackRegistries.MODIFIER_KEY).fieldOf("tag").forGetter(RunegemData::tag),
            RunegemTier.CODEC.fieldOf("tier").forGetter(RunegemData::tier)
    ).apply(inst, RunegemData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RunegemData> STREAM_CODEC = StreamCodec.composite(
            RunegemShape.STREAM_CODEC,
            RunegemData::shape,
            TagKey.streamCodec(ModDatapackRegistries.MODIFIER_KEY),
            RunegemData::tag,
            RunegemTier.STREAM_CODEC,
            RunegemData::tier,
            RunegemData::new
    );

    public Optional<Holder<Modifier>> getRandomModifier(Level level) {
        return level.registryAccess().lookupOrThrow(ModDatapackRegistries.MODIFIER_KEY)
                .get(tag)
                .flatMap(holders -> holders.getRandomElement(level.random));
    }
}
