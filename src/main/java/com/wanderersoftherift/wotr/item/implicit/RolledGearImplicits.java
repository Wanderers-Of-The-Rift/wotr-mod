package com.wanderersoftherift.wotr.item.implicit;

import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public record RolledGearImplicits(
        List<ModifierInstance> modifierInstances
) implements GearImplicits{

    @Override
    public List<ModifierInstance> modifierInstances(ItemStack stack, Level level) {
        return List.copyOf(modifierInstances);
    }
}
