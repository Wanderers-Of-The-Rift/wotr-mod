package com.wanderersoftherift.wotr.item.implicit;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.ModifierProvider;
import com.wanderersoftherift.wotr.modifier.source.GearImplicitModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;

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
    default void forEachModifier(ItemStack stack, WotrEquipmentSlot slot, LivingEntity entity, Action action) {
        List<ModifierInstance> modifierInstances = modifierInstances(stack, entity.level());
        for (int i = 0; i < modifierInstances.size(); i++) {
            ModifierInstance modifier = modifierInstances.get(i);
            ModifierSource source = new GearImplicitModifierSource(slot, i);
            action.accept(modifier.modifier(), modifier.tier(), modifier.roll(), source);
        }
    }

    default Collection<Either<FormattedText, TooltipComponent>> tooltips(ItemStack stack) {
        var result = ImmutableList.<Either<FormattedText, TooltipComponent>>builder();

        result.add(Either.left(Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".implicit")
                .withStyle(ChatFormatting.GRAY)));

        for (ModifierInstance modifierInstance : modifierInstances(stack, Minecraft.getInstance().level)) {
            List<TooltipComponent> tooltipComponents = modifierInstance.getTooltipComponent(stack);
            result.addAll(tooltipComponents.stream().map(Either::<FormattedText, TooltipComponent>right).toList());
        }
        return result.build();
    }
}
