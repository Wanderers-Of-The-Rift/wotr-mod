package com.dimensiondelvers.dimensiondelvers.item.runegem;

import com.dimensiondelvers.dimensiondelvers.init.ModModifiers;
import com.dimensiondelvers.dimensiondelvers.modifier.Modifier;
import com.dimensiondelvers.dimensiondelvers.modifier.ModifierInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record RunegemData(RunegemShape shape, TagKey<Modifier> tag, RunegemTier tier) {
    public static Codec<RunegemData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RunegemShape.CODEC.fieldOf("shape").forGetter(RunegemData::shape),
            TagKey.codec(ModModifiers.MODIFIER_KEY).fieldOf("tag").forGetter(RunegemData::tag),
            RunegemTier.CODEC.fieldOf("tier").forGetter(RunegemData::tier)
    ).apply(inst, RunegemData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RunegemData> STREAM_CODEC = StreamCodec.composite(
            RunegemShape.STREAM_CODEC,
            RunegemData::shape,
            TagKey.streamCodec(ModModifiers.MODIFIER_KEY),
            RunegemData::tag,
            RunegemTier.STREAM_CODEC,
            RunegemData::tier,
            RunegemData::new
    );

    public Optional<Holder<Modifier>> getRandomModifier(Level level) {
        return level.registryAccess().lookupOrThrow(ModModifiers.MODIFIER_KEY)
                .get(tag)
                .flatMap(holders -> holders.getRandomElement(level.random));
    }
}
