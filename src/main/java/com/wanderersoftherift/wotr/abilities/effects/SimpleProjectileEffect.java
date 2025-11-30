package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import com.wanderersoftherift.wotr.entity.projectile.SimpleEffectProjectile;
import com.wanderersoftherift.wotr.entity.projectile.SimpleProjectileConfig;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.init.WotrEntities;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

/**
 * Effect that produces a projectile, with effects that apply to what it hits
 */
public record SimpleProjectileEffect(List<AbilityEffect> effects, SimpleProjectileConfig config)
        implements AbilityEffect {
    public static final MapCodec<SimpleProjectileEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbilityEffect.DIRECT_CODEC.listOf()
                    .optionalFieldOf("effects", List.of())
                    .forGetter(SimpleProjectileEffect::effects),
            SimpleProjectileConfig.CODEC.fieldOf("config").forGetter(SimpleProjectileEffect::config)
    ).apply(instance, SimpleProjectileEffect::new));

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {

        // NOTE: Making a change here based on what I originally envisioned "target" to be used for, and pulling it
        // inline with the other effects
        // Target to me has always been more of a frame of reference for the effect not what the effect actually
        // "targets" but we can change this later if we want to make the change towards it being the actual target.
        targetInfo.targetEntities().forEach(target -> {
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
        });
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

    public void applyDelayed(Entity projectile, HitResult hit, AbilityContext context) {
        try (var ignore = context.activate()) {
            TargetInfo targetInfo = new TargetInfo(new EntityHitResult(projectile, projectile.position()),
                    List.of(hit));
            for (AbilityEffect effect : effects) {
                effect.apply(context, targetInfo);
            }
        }
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        if (modifierEffect instanceof AttributeModifierEffect attributeModifier) {
            Holder<Attribute> attribute = attributeModifier.attribute();
            if (WotrAttributes.PROJECTILE_SPREAD.equals(attribute) || WotrAttributes.PROJECTILE_COUNT.equals(attribute)
                    || WotrAttributes.PROJECTILE_SPEED.equals(attribute)) {
                return true;
            }
        }
        for (AbilityEffect effect : effects) {
            if (effect.isRelevant(modifierEffect)) {
                return true;
            }
        }
        return false;
    }
}
