package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public record EquipmentSpawnFunction(Map<EquipmentSlot, ItemStack> equipment) implements SpawnFunction {
    public static final MapCodec<EquipmentSpawnFunction> MAP_CODEC = Codec
            .unboundedMap(EquipmentSlot.CODEC, ItemStack.CODEC)
            .fieldOf("equipment")
            .xmap(EquipmentSpawnFunction::new, EquipmentSpawnFunction::equipment);

    @Override
    public MapCodec<? extends SpawnFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public void applyToMob(Entity entity, BlockEntity spawner, RandomSource random) {
        if (entity instanceof LivingEntity living) {
            equipment.forEach(living::setItemSlot);
        }
    }
}
