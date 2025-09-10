package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;

public class SummonEffect implements AbilityEffect {
    public static final MapCodec<SummonEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbilityEffect.DIRECT_CODEC.listOf()
                    .optionalFieldOf("effects", List.of())
                    .forGetter(SummonEffect::getEffects),
            ResourceLocation.CODEC.fieldOf("entity_type").forGetter(SummonEffect::getEntityType),
            Codec.INT.optionalFieldOf("amount", 1).forGetter(SummonEffect::getAmount)
    ).apply(instance, SummonEffect::new));

    private final List<AbilityEffect> effects;
    private final ResourceLocation entityType;
    private final int summonAmount;

    // TODO look into handling different types of teleports and better handle relative motion
    // TODO also look into teleporting "towards" a location to find the nearest safe spot that isnt the exact location

    public SummonEffect(List<AbilityEffect> effects, ResourceLocation entityType, int amount) {
        this.effects = List.copyOf(effects);
        this.entityType = entityType;
        this.summonAmount = amount;
    }

    private Integer getAmount() {
        return this.summonAmount;
    }

    private ResourceLocation getEntityType() {
        return this.entityType;
    }

    private List<AbilityEffect> getEffects() {
        return effects;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {

        List<Entity> targetEntities = targetInfo.targetEntities().map(EntityHitResult::getEntity).toList();

        // No entity was selected as the summon position
        if (!targetEntities.isEmpty()) {
            for (int i = 0; i < summonAmount; i++) {
                Entity random = targetEntities
                        .get(context.caster().getRandom().nextIntBetweenInclusive(0, targetEntities.size() - 1));
                if (BuiltInRegistries.ENTITY_TYPE.get(this.entityType).isPresent()) {
                    EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(this.entityType).get().value();
                    Entity summon = type.create((ServerLevel) context.level(), null, random.getOnPos(),
                            EntitySpawnReason.MOB_SUMMONED, false, false);
                    if (summon != null) {
                        context.level().addFreshEntity(summon);
                        TargetInfo summonTarget = new TargetInfo(summon);
                        for (AbilityEffect effect : effects) {
                            effect.apply(context, summonTarget);
                        }
                    }
                }
            }
            return;
        }

        List<BlockPos> blockInArea = targetInfo.targetBlocks().map(BlockHitResult::getBlockPos).toList();
        if (!blockInArea.isEmpty()) {
            // TODO look into more systematically placing summons
            for (int i = 0; i < summonAmount; i++) {
                BlockPos random = blockInArea
                        .get(context.caster().getRandom().nextIntBetweenInclusive(0, blockInArea.size() - 1));
                if (BuiltInRegistries.ENTITY_TYPE.get(this.entityType).isPresent()) {
                    EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(this.entityType).get().value();
                    Entity summon = type.create((ServerLevel) context.level(), null, random,
                            EntitySpawnReason.MOB_SUMMONED, true, true);
                    if (summon != null) {
                        context.level().addFreshEntity(summon);
                        TargetInfo summonTarget = new TargetInfo(summon);
                        for (AbilityEffect effect : effects) {
                            effect.apply(context, summonTarget);
                        }
                    }
                }
            }
        }
    }

}
