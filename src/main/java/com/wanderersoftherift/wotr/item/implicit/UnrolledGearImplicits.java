package com.wanderersoftherift.wotr.item.implicit;

import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.wanderersoftherift.wotr.init.ModDatapackRegistries.GEAR_IMPLICITS_CONFIG;

public record UnrolledGearImplicits() implements GearImplicits {

    @Override
    public List<ModifierInstance> modifierInstances(ItemStack stack, Level level) {
        if(level.isClientSide()) {
            return List.of();
        }
        Registry<ImplicitConfig> implicitConfigs = level.registryAccess().lookupOrThrow(GEAR_IMPLICITS_CONFIG);
        ImplicitConfig config = implicitConfigs.getOptional(stack.getItemHolder().getKey().location()).orElse(ImplicitConfig.DEFAULT);
        if (config.implicitModifiers().size() == 0) {
            return List.of();
        }
        List<ModifierInstance> instances = new ArrayList<>();
        Iterator<Holder<Modifier>> iterator = config.implicitModifiers().iterator();
        RandomSource randomSource = RandomSource.create();
        randomSource.consumeCount(1);
        while(iterator.hasNext()) {
            Holder<Modifier> holder = iterator.next();
            instances.add(ModifierInstance.of(holder, randomSource));
        }
        RolledGearImplicits rolledGearImplicits = new RolledGearImplicits(instances);
        stack.set(ModDataComponentType.GEAR_IMPLICITS, rolledGearImplicits);
        return instances;
    }

    @Override
    public List<ModifierInstance> modifierInstances() {
        return List.of();
    }
}
