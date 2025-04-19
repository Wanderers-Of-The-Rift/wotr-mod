package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbstractTargeting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Optional;

public class SummonEffect extends AbstractEffect {
    ResourceLocation entityType;
    int summonAmount = 1;

    // TODO look into handling different types of teleports and better handle relative motion
    // TODO also look into teleporting "towards" a location to find the nearest safe spot that isnt the exact location

    public static final MapCodec<SummonEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> AbstractEffect
            .commonFields(instance)
            .and(instance.group(ResourceLocation.CODEC.fieldOf("entity_type").forGetter(SummonEffect::getEntityType),
                    Codec.INT.fieldOf("amount").forGetter(SummonEffect::getAmount)))
            .apply(instance, SummonEffect::new));

    private Integer getAmount() {
        return this.summonAmount;
    }

    private ResourceLocation getEntityType() {
        return this.entityType;
    }

    @Override
    public MapCodec<? extends AbstractEffect> getCodec() {
        return CODEC;
    }

    public SummonEffect(AbstractTargeting targeting, List<AbstractEffect> effects, Optional<ParticleInfo> particles,
            ResourceLocation entityType, int amount) {
        super(targeting, effects, particles);
        this.entityType = entityType;
        this.summonAmount = amount;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);
        applyParticlesToUser(user);

        // No entity was selected as the summon position
        if (targets.isEmpty()) {
            List<BlockPos> blockInArea = getTargeting().getBlocksInArea(user, blocks, context);
            // TODO look into more systematically placing summons
            for (int i = 0; i < summonAmount; i++) {
                if (blockInArea.isEmpty()) {
                    WanderersOfTheRift.LOGGER.info("No Suitable Spawn Location!");
                    super.apply(null, getTargeting().getBlocks(user), context);
                }
                BlockPos random = blockInArea
                        .get(context.caster().getRandom().nextIntBetweenInclusive(0, blockInArea.size() - 1));
                if (BuiltInRegistries.ENTITY_TYPE.get(this.entityType).isPresent()) {
                    EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(this.entityType).get().value();
                    Entity summon = type.create((ServerLevel) context.level(), null, random,
                            EntitySpawnReason.MOB_SUMMONED, true, true);
                    if (summon != null) {
                        context.level().addFreshEntity(summon);
                        applyParticlesToTarget(summon);
                        super.apply(summon, getTargeting().getBlocks(user), context);
                    }
                }
            }
        } else {
            for (int i = 0; i < summonAmount; i++) {
                Entity random = targets
                        .get(context.caster().getRandom().nextIntBetweenInclusive(0, targets.size() - 1));
                if (BuiltInRegistries.ENTITY_TYPE.get(this.entityType).isPresent()) {
                    EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(this.entityType).get().value();
                    Entity summon = type.create((ServerLevel) context.level(), null, random.getOnPos(),
                            EntitySpawnReason.MOB_SUMMONED, false, false);
                    if (summon != null) {
                        context.level().addFreshEntity(summon);
                        applyParticlesToTarget(summon);
                        super.apply(summon, getTargeting().getBlocks(user), context);
                    }
                }
            }
        }
    }

}
