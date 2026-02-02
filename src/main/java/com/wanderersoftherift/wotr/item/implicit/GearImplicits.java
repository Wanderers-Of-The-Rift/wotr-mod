package com.wanderersoftherift.wotr.item.implicit;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.client.tooltip.GearImplicitRenderer;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.ModifierProvider;
import com.wanderersoftherift.wotr.modifier.source.GearImplicitModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface GearImplicits extends ModifierProvider {
    Codec<GearImplicits> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ModifierInstance.CODEC.listOf()
                    .optionalFieldOf("instances", List.of())
                    .forGetter(GearImplicits::modifierInstances)
    ).apply(inst, GearImplicits::of));

    static GearImplicits of(List<ModifierInstance> modifierInstances) {
        if (modifierInstances.isEmpty()) {
            return new UnrolledGearImplicits();
        } else {
            return new RolledGearImplicits(modifierInstances);
        }
    }

    List<ModifierInstance> modifierInstances(ItemStack stack, Level level);

    List<ModifierInstance> modifierInstances();

    @Override
    default Stream<ModifierEntry> modifiers(ItemStack stack, WotrEquipmentSlot slot, LivingEntity entity) {
        List<ModifierInstance> modifierInstances = modifierInstances(stack, entity == null ? null : entity.level());
        return IntStream.range(0, modifierInstances.size()).mapToObj(idx -> {

            ModifierInstance modifier = modifierInstances.get(idx);
            ModifierSource source = new GearImplicitModifierSource(slot, idx);
            return new ModifierEntry(modifier, source);
        });
    }

    default List<Either<FormattedText, TooltipComponent>> tooltips(int maxWidth) {
        if (modifierInstances().isEmpty()) {
            return Collections.emptyList();
        }
        return List.of(Either.right(new GearImplicitRenderer.GearImplicitsComponent(this)));
    }
}
