package com.wanderersoftherift.wotr.item.runegem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.TieredModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public record RunegemData(Component name, RunegemShape shape, List<ModifierGroup> modifierLists, RunegemTier tier) {

    public static final Codec<RunegemData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(RunegemData::name),
            RunegemShape.CODEC.fieldOf("shape").forGetter(RunegemData::shape),
            ModifierGroup.CODEC.listOf().fieldOf("modifier_options").forGetter(RunegemData::modifierLists),
            RunegemTier.CODEC.fieldOf("tier").forGetter(RunegemData::tier)
    ).apply(inst, RunegemData::new));

    public Optional<TieredModifier> getRandomTieredModifierForItem(
            ItemStack stack,
            Level level,
            Set<Holder<Modifier>> exclusionList) {
        List<TieredModifier> modifiers = modifierLists.stream()
                .filter(group -> stack.is(group.supportedItems()))
                .flatMap(group -> group.modifiers.stream())
                .filter(modifier -> !exclusionList.contains(modifier.modifier()))
                .distinct()
                .toList();
        if (modifiers.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(modifiers.get(level.random.nextInt(modifiers.size())));
    }

    public record ModifierGroup(HolderSet<Item> supportedItems, List<TieredModifier> modifiers) {
        public static final Codec<ModifierGroup> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                RegistryCodecs.homogeneousList(Registries.ITEM)
                        .fieldOf("supported_items")
                        .forGetter(ModifierGroup::supportedItems),
                TieredModifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifierGroup::modifiers))
                .apply(inst, ModifierGroup::new));

    }

}
