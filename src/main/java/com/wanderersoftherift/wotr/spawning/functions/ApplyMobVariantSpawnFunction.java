package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.entity.mob.RiftMobVariantData;
import com.wanderersoftherift.wotr.entity.mob.VariedRiftMob;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;

public record ApplyMobVariantSpawnFunction(Holder<RiftMobVariantData> variant) implements SpawnFunction {
    public static final MapCodec<ApplyMobVariantSpawnFunction> MAP_CODEC = RiftMobVariantData.VARIANT_HOLDER_CODEC
            .fieldOf("variant")
            .xmap(ApplyMobVariantSpawnFunction::new, ApplyMobVariantSpawnFunction::variant);

    @Override
    public MapCodec<? extends SpawnFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public void applyToMob(Mob mob, BlockEntity spawner, RandomSource random) {
        if (mob instanceof VariedRiftMob zombie) {
            zombie.setVariant(variant);
        }
    }
}
