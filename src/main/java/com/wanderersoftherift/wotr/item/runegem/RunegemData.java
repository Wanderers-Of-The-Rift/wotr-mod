package com.wanderersoftherift.wotr.item.runegem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModDatapackRegistries;
import com.wanderersoftherift.wotr.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public record RunegemData(RunegemShape shape, List<ModifierGroup> modifierLists, RunegemTier tier) {

    public static Codec<RunegemData> CODEC = RecordCodecBuilder
            .create(inst -> inst.group(RunegemShape.CODEC.fieldOf("shape").forGetter(RunegemData::shape),
                    ModifierGroup.CODEC.listOf().fieldOf("modifier_options").forGetter(RunegemData::modifierLists),
                    RunegemTier.CODEC.fieldOf("tier").forGetter(RunegemData::tier)).apply(inst, RunegemData::new));

    public Optional<Holder<Modifier>> getRandomModifierForItem(ItemStack stack, Level level) {
        List<Holder<Modifier>> modifiers = modifierLists.stream()
                .filter(group -> stack.is(group.supportedItems()))
                .map(ModifierGroup::modifiers)
                .flatMap(HolderSet::stream)
                .distinct()
                .toList();
        if (modifiers.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(modifiers.get(level.random.nextInt(modifiers.size())));
    }

    public record ModifierGroup(HolderSet<Item> supportedItems, HolderSet<Modifier> modifiers) {
        public static Codec<ModifierGroup> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                RegistryCodecs.homogeneousList(Registries.ITEM)
                        .fieldOf("supported_items")
                        .forGetter(ModifierGroup::supportedItems),
                RegistryCodecs.homogeneousList(ModDatapackRegistries.MODIFIER_KEY)
                        .fieldOf("modifiers")
                        .forGetter(ModifierGroup::modifiers))
                .apply(inst, ModifierGroup::new));
    }
}
