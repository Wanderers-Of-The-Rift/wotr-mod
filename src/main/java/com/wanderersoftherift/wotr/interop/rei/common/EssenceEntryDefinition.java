package com.wanderersoftherift.wotr.interop.rei.common;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.interop.rei.client.EssenceEntryRenderer;
import com.wanderersoftherift.wotr.item.crafting.EssencePredicate;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.common.entry.EntrySerializer;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.type.EntryDefinition;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * REI definition for an {@link EssencePredicate} as a recipe element
 */
public class EssenceEntryDefinition implements EntryDefinition<EssencePredicate>, EntrySerializer<EssencePredicate> {

    private final EntryRenderer<EssencePredicate> renderer;

    public EssenceEntryDefinition() {
        this.renderer = new EssenceEntryRenderer();
    }

    @Override
    public Codec<EssencePredicate> codec() {
        return EssencePredicate.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EssencePredicate> streamCodec() {
        return EssencePredicate.STREAM_CODEC;
    }

    @Override
    public Class<EssencePredicate> getValueType() {
        return EssencePredicate.class;
    }

    @Override
    public EntryType<EssencePredicate> getType() {
        return WotrEntryTypes.ESSENCE;
    }

    @Override
    public EntryRenderer<EssencePredicate> getRenderer() {
        return renderer;
    }

    @Override
    public @Nullable ResourceLocation getIdentifier(
            EntryStack<EssencePredicate> entryStack,
            EssencePredicate essencePredicate) {
        return null;
    }

    @Override
    public boolean isEmpty(EntryStack<EssencePredicate> entryStack, EssencePredicate value) {
        return value == null;
    }

    @Override
    public EssencePredicate copy(EntryStack<EssencePredicate> entryStack, EssencePredicate value) {
        return value;
    }

    @Override
    public EssencePredicate normalize(EntryStack<EssencePredicate> entryStack, EssencePredicate value) {
        return value;
    }

    @Override
    public EssencePredicate wildcard(EntryStack<EssencePredicate> entryStack, EssencePredicate value) {
        return value;
    }

    @Override
    public @Nullable EntrySerializer<EssencePredicate> getSerializer() {
        return this;
    }

    @Override
    public Component asFormattedText(EntryStack<EssencePredicate> entryStack, EssencePredicate essencePredicate) {
        return Component.literal(essencePredicate.essenceType().toString());
    }

    @Override
    public Stream<? extends TagKey<?>> getTagsFor(
            EntryStack<EssencePredicate> entryStack,
            EssencePredicate essencePredicate) {
        return Stream.empty();
    }

    @Override
    public boolean equals(EssencePredicate v1, EssencePredicate v2, ComparisonContext comparisonContext) {
        return v1.equals(v2);
    }

    @Override
    public long hash(
            EntryStack<EssencePredicate> entryStack,
            EssencePredicate essencePredicate,
            ComparisonContext comparisonContext) {
        return essencePredicate.hashCode();
    }
}
