package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Effect that produces a projectile (which can have effects attached to apply on whatever it hits)
 */
public class ProjectileEffect implements AbilityEffect {
    public static final MapCodec<ProjectileEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(AbilityEffect.DIRECT_CODEC)
                    .optionalFieldOf("effects", List.of())
                    .forGetter(ProjectileEffect::getEffects),
            ResourceLocation.CODEC.fieldOf("projectile_type").forGetter(ProjectileEffect::getEntityType),
            Vec3.CODEC.fieldOf("velocity").forGetter(ProjectileEffect::getVelocity)
    ).apply(instance, ProjectileEffect::new));

    private final List<AbilityEffect> effects;
    private final ResourceLocation entityType;
    private final Vec3 velocity;

    /*
     * For now just handle any projectile given, but we will look into handling a dynamic projectile that can handle
     * effects attached to it
     */
    public ProjectileEffect(List<AbilityEffect> effects, ResourceLocation entityType, Vec3 velocity) {
        this.effects = List.copyOf(effects);
        this.entityType = entityType;
        this.velocity = velocity;
    }

    public List<AbilityEffect> getEffects() {
        return effects;
    }

    public Vec3 getVelocity() {
        return this.velocity;
    }

    private ResourceLocation getEntityType() {
        return this.entityType;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        if (BuiltInRegistries.ENTITY_TYPE.get(this.entityType).isEmpty()) {
            return;
        }
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(this.entityType).get().value();

        targetInfo.targetEntityHitResults().map(EntityHitResult::getEntity).forEach(target -> {
            Entity summon = type.create((ServerLevel) context.level(), null, target.getOnPos(),
                    EntitySpawnReason.MOB_SUMMONED, false, false);
            if (summon != null) {
                summon.setPos(target.getEyePosition());
                if (summon instanceof Projectile projectileEntity) {
                    projectileEntity.setOwner(target);

                    // TODO tweak this calculation its not quite working right

                    projectileEntity.shootFromRotation(target, (float) (target.getXRot() + velocity.y),
                            (float) (target.getYRot() + velocity.x), 0, 1, 0);
                }

                context.level().addFreshEntity(summon);
                TargetInfo summonTargetInfo = new TargetInfo(summon);
                for (AbilityEffect effect : effects) {
                    effect.apply(context, summonTargetInfo);
                }
            }
        });
    }
}
