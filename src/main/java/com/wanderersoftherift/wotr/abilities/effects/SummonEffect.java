package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.HitResult;

import java.util.List;
import java.util.Optional;

/**
 * Effect that summons an entity, and applies effects to it
 */
public record SummonEffect(List<AbilityEffect> effects, ResourceLocation entityType, Optional<CompoundTag> nbt)
        implements AbilityEffect {

    public static final MapCodec<SummonEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbilityEffect.DIRECT_CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(SummonEffect::effects),
            ResourceLocation.CODEC.fieldOf("entity_type").forGetter(SummonEffect::entityType),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(SummonEffect::nbt)
    ).apply(instance, SummonEffect::new));

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        if (!BuiltInRegistries.ENTITY_TYPE.get(this.entityType).isPresent()) {
            return;
        }
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(this.entityType).get().value();

        for (HitResult target : targetInfo.targets()) {
            Entity summon = type.create((ServerLevel) context.level(), null, BlockPos.containing(target.getLocation()),
                    EntitySpawnReason.MOB_SUMMONED, target.getType() == HitResult.Type.BLOCK, false);
            if (summon != null) {
                nbt.ifPresent(tag -> {
                    CompoundTag existingTag = summon.saveWithoutId(new CompoundTag());
                    existingTag.merge(tag);
                    summon.load(existingTag);
                });

                context.level().addFreshEntity(summon);
                TargetInfo summonTarget = new TargetInfo(summon);
                for (AbilityEffect effect : effects) {
                    effect.apply(context, summonTarget);
                }
            }
        }
    }

}
