package com.wanderersoftherift.wotr.item.implicit;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.ModifierProvider;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.source.GearImplicitModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
}
