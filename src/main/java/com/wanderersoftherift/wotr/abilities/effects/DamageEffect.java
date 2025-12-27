package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * Effect that applies damage to target entities
 */
public record DamageEffect(float damageAmount, Holder<Attribute> damageAttribute, Holder<DamageType> damageTypeKey)
        implements AbilityEffect {

    public static final MapCodec<DamageEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.FLOAT.fieldOf("amount").forGetter(DamageEffect::damageAmount),
                    Attribute.CODEC.optionalFieldOf("damage_attribute", WotrAttributes.ABILITY_DAMAGE)
                            .forGetter(DamageEffect::damageAttribute),
                    DamageType.CODEC.fieldOf("damage_type").forGetter(DamageEffect::damageTypeKey))
            .apply(instance, DamageEffect::new));

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        DamageSource damageSource = new DamageSource(
                context.level()
                        .registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(this.damageTypeKey.getKey()),
                null, context.caster(), targetInfo.source().getLocation());

        // for now its ABILITY_DAMAGE but needs to be considered how multiple types are going to be implemented ie AP or
        // AD
        float finalDamage = context.getAbilityAttribute(damageAttribute, damageAmount);

        targetInfo.targetEntities()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .forEach(target -> {
                    target.hurtServer((ServerLevel) target.level(), damageSource, finalDamage);
                });
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        return modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && WotrAttributes.ABILITY_DAMAGE.equals(attributeModifierEffect.attribute());
    }
}
