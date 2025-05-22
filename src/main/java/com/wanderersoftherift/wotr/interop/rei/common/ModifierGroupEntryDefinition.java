package com.wanderersoftherift.wotr.interop.rei.common;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.interop.rei.client.ModifierGroupEntryRenderer;
import com.wanderersoftherift.wotr.item.crafting.EssencePredicate;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
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
public class ModifierGroupEntryDefinition
        implements EntryDefinition<RunegemData.ModifierGroup>, EntrySerializer<RunegemData.ModifierGroup> {

    private final EntryRenderer<RunegemData.ModifierGroup> renderer;

    public ModifierGroupEntryDefinition() {
        this.renderer = new ModifierGroupEntryRenderer();
    }

    @Override
    public Codec<RunegemData.ModifierGroup> codec() {
        return RunegemData.ModifierGroup.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RunegemData.ModifierGroup> streamCodec() {
        return RunegemData.ModifierGroup.STREAM_CODEC;
    }

    @Override
    public Class<RunegemData.ModifierGroup> getValueType() {
        return RunegemData.ModifierGroup.class;
    }

    @Override
    public EntryType<RunegemData.ModifierGroup> getType() {
        return WotrEntryTypes.MODIFIER_GROUP;
    }

    @Override
    public EntryRenderer<RunegemData.ModifierGroup> getRenderer() {
        return renderer;
    }

    @Override
    public @Nullable ResourceLocation getIdentifier(
            EntryStack<RunegemData.ModifierGroup> entryStack,
            RunegemData.ModifierGroup modifierGroup) {
        return null;
    }

    @Override
    public boolean isEmpty(EntryStack<RunegemData.ModifierGroup> entryStack, RunegemData.ModifierGroup value) {
        return value == null;
    }

    @Override
    public RunegemData.ModifierGroup copy(
            EntryStack<RunegemData.ModifierGroup> entryStack,
            RunegemData.ModifierGroup value) {
        return value;
    }

    @Override
    public RunegemData.ModifierGroup normalize(
            EntryStack<RunegemData.ModifierGroup> entryStack,
            RunegemData.ModifierGroup value) {
        return value;
    }

    @Override
    public RunegemData.ModifierGroup wildcard(
            EntryStack<RunegemData.ModifierGroup> entryStack,
            RunegemData.ModifierGroup value) {
        return value;
    }

    @Override
    public @Nullable EntrySerializer<RunegemData.ModifierGroup> getSerializer() {
        return this;
    }

    @Override
    public Component asFormattedText(
            EntryStack<RunegemData.ModifierGroup> entryStack,
            RunegemData.ModifierGroup value) {
        return Component.literal(value.toString());
    }

    @Override
    public Stream<? extends TagKey<?>> getTagsFor(
            EntryStack<RunegemData.ModifierGroup> entryStack,
            RunegemData.ModifierGroup value) {
        return Stream.empty();
    }

    @Override
    public boolean equals(
            RunegemData.ModifierGroup v1,
            RunegemData.ModifierGroup v2,
            ComparisonContext comparisonContext) {
        return v1.equals(v2);
    }

    @Override
    public long hash(
            EntryStack<RunegemData.ModifierGroup> entryStack,
            RunegemData.ModifierGroup value,
            ComparisonContext comparisonContext) {
        return value.hashCode();
    }
}
