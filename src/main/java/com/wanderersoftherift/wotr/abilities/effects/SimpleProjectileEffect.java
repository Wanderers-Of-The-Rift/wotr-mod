package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbstractTargeting;
import com.wanderersoftherift.wotr.entity.projectile.SimpleEffectProjectile;
import com.wanderersoftherift.wotr.entity.projectile.SimpleProjectileConfig;
import com.wanderersoftherift.wotr.init.ModAttributes;
import com.wanderersoftherift.wotr.init.ModEntities;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class SimpleProjectileEffect extends AbstractEffect {
    private SimpleProjectileConfig config;

    public static final MapCodec<SimpleProjectileEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbstractEffect.commonFields(instance)
                    .and(SimpleProjectileConfig.CODEC.fieldOf("config").forGetter(SimpleProjectileEffect::getConfig))
                    .apply(instance, SimpleProjectileEffect::new));

    @Override
    public MapCodec<? extends AbstractEffect> getCodec() {
        return CODEC;
    }

    public SimpleProjectileConfig getConfig() {
        return config;
    }

    public SimpleProjectileEffect(AbstractTargeting targeting, List<AbstractEffect> effects,
            Optional<ParticleInfo> particles, SimpleProjectileConfig config) {
        super(targeting, effects, particles);
        this.config = config;
    }

    @Override
    public void apply(Entity source, List<BlockPos> blocks, AbilityContext context) {
        List<BlockPos> targets = getTargeting().getBlocks(source);
        List<Entity> target_entities = getTargeting().getTargets(source, blocks, context);

        applyParticlesToUser(source);
        if (!target_entities.isEmpty()) {

            // NOTE: Making a change here based on what I originally envisioned "target" to be used for, and pulling it
            // inline with the other effects
            // Target to me has always been more of a frame of reference for the effect not what the effect actually
            // "targets" but we can change this later if we want to make the change towards it being the actual target.
            for (Entity target : target_entities) {
                EntityType<?> type = ModEntities.SIMPLE_EFFECT_PROJECTILE.get();
                int numberOfProjectiles = getNumberOfProjectiles(context);

                float spread = getSpread(context);
                float f1;
                if (numberOfProjectiles == 1) {
                    f1 = 0.0F;
                } else {
                    f1 = 2.0F * spread / (float) (numberOfProjectiles - 1);
                }
                float f2 = (float) ((numberOfProjectiles - 1) % 2) * f1 / 2.0F;
                float f3 = 1.0F;
                for (int i = 0; i < numberOfProjectiles; i++) {
                    float angle = f2 + f3 * (float) ((i + 1) / 2) * f1;
                    f3 = -f3;
                    spawnProjectile(target, type, angle, context);
                }
            }

        }
    }

    private float getSpread(AbilityContext context) {
        return context.getAbilityAttribute(ModAttributes.PROJECTILE_SPREAD, 15);
    }

    private int getNumberOfProjectiles(AbilityContext context) {
        return (int) context.getAbilityAttribute(ModAttributes.PROJECTILE_COUNT, config.projectiles());
    }

    private void spawnProjectile(Entity user, EntityType<?> type, float angle, AbilityContext context) {
        Entity simpleProjectile = type.create((ServerLevel) context.level(), null, user.getOnPos(),
                EntitySpawnReason.MOB_SUMMONED, false, false);
        if (simpleProjectile instanceof SimpleEffectProjectile projectileEntity) {
            projectileEntity.setPos(user.getEyePosition());
            projectileEntity.setOwner(context.caster());
            projectileEntity.setEffect(this);
            projectileEntity.configure(config);

            projectileEntity.shootFromRotation(user, user.getXRot(), user.getYRot() + angle, 0,
                    context.getAbilityAttribute(ModAttributes.PROJECTILE_SPEED, config.velocity()), 0);

            context.level().addFreshEntity(simpleProjectile);
        }
    }

    public void applyDelayed(Level level, Entity target, List<BlockPos> blocks, AbilityContext context) {
        context.enableModifiers();
        try {
            applyParticlesToTarget(target);
            applyParticlesToTargetBlocks(level, blocks);
            super.apply(target, blocks, context);
        } finally {
            context.disableModifiers();
        }
    }

    @Override
    protected boolean isRelevantToThis(AbstractModifierEffect modifierEffect) {
        if (modifierEffect instanceof AttributeModifierEffect attributeModifier) {
            Holder<Attribute> attribute = attributeModifier.getAttribute();
            return ModAttributes.PROJECTILE_SPREAD.equals(attribute) || ModAttributes.PROJECTILE_COUNT.equals(attribute)
                    || ModAttributes.PROJECTILE_SPEED.equals(attribute);
        }
        return false;
    }
}
