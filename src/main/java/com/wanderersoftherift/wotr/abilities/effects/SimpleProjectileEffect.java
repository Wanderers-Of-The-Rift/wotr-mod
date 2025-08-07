package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import com.wanderersoftherift.wotr.entity.projectile.SimpleEffectProjectile;
import com.wanderersoftherift.wotr.entity.projectile.SimpleProjectileConfig;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.init.WotrEntities;
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

public class SimpleProjectileEffect extends AbilityEffect {
    public static final MapCodec<SimpleProjectileEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbilityEffect.commonFields(instance)
                    .and(SimpleProjectileConfig.CODEC.fieldOf("config").forGetter(SimpleProjectileEffect::getConfig))
                    .apply(instance, SimpleProjectileEffect::new));

    private SimpleProjectileConfig config;

    public SimpleProjectileEffect(AbilityTargeting targeting, List<AbilityEffect> effects,
            Optional<ParticleInfo> particles, SimpleProjectileConfig config) {
        super(targeting, effects, particles);
        this.config = config;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    public SimpleProjectileConfig getConfig() {
        return config;
    }

    @Override
    public void apply(Entity source, List<BlockPos> blocks, AbilityContext context) {
        List<BlockPos> targets = getTargeting().getBlocks(source);
        List<Entity> targetEntities = getTargeting().getTargets(source, blocks, context);

        applyParticlesToUser(source);
        if (!targetEntities.isEmpty()) {

            // NOTE: Making a change here based on what I originally envisioned "target" to be used for, and pulling it
            // inline with the other effects
            // Target to me has always been more of a frame of reference for the effect not what the effect actually
            // "targets" but we can change this later if we want to make the change towards it being the actual target.
            for (Entity target : targetEntities) {
                EntityType<?> type = WotrEntities.SIMPLE_EFFECT_PROJECTILE.get();
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
        return context.getAbilityAttribute(WotrAttributes.PROJECTILE_SPREAD, 15);
    }

    private int getNumberOfProjectiles(AbilityContext context) {
        return (int) context.getAbilityAttribute(WotrAttributes.PROJECTILE_COUNT, config.projectiles());
    }

    private void spawnProjectile(Entity user, EntityType<?> type, float angle, AbilityContext context) {
        Entity simpleProjectile = type.create((ServerLevel) context.level(), null, user.getOnPos(),
                EntitySpawnReason.MOB_SUMMONED, false, false);
        if (simpleProjectile instanceof SimpleEffectProjectile projectileEntity) {
            projectileEntity.setPos(user.getEyePosition());
            projectileEntity.setOwner(context.caster());
            projectileEntity.setEffect(this);
            projectileEntity.configure(config, context);

            projectileEntity.shootFromRotation(user, user.getXRot(), user.getYRot() + angle, 0,
                    context.getAbilityAttribute(WotrAttributes.PROJECTILE_SPEED, config.velocity()), 0);

            context.level().addFreshEntity(simpleProjectile);
        }
    }

    public void applyDelayed(Level level, Entity target, List<BlockPos> blocks, AbilityContext context) {
        context.enableUpgradeModifiers();
        try {
            applyParticlesToTarget(target);
            applyParticlesToTargetBlocks(level, blocks);
            super.apply(target, blocks, context);
        } finally {
            context.disableUpgradeModifiers();
        }
    }

    @Override
    protected boolean isRelevantToThis(AbstractModifierEffect modifierEffect) {
        if (modifierEffect instanceof AttributeModifierEffect attributeModifier) {
            Holder<Attribute> attribute = attributeModifier.getAttribute();
            return WotrAttributes.PROJECTILE_SPREAD.equals(attribute)
                    || WotrAttributes.PROJECTILE_COUNT.equals(attribute)
                    || WotrAttributes.PROJECTILE_SPEED.equals(attribute);
        }
        return false;
    }
}
