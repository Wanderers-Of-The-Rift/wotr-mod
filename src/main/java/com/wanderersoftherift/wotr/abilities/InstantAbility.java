package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.effects.AbilityEffect;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InstantAbility extends Ability {

    public static final MapCodec<InstantAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("icon").forGetter(InstantAbility::getIcon),
                    ResourceLocation.CODEC.optionalFieldOf("small_icon").forGetter(InstantAbility::getSmallIcon),
                    AbilityRequirement.CODEC.listOf()
                            .optionalFieldOf("requirements", List.of())
                            .forGetter(Ability::getActivationRequirements),
                    Codec.list(AbilityEffect.DIRECT_CODEC)
                            .optionalFieldOf("effects", Collections.emptyList())
                            .forGetter(InstantAbility::getEffects)
            ).apply(instance, InstantAbility::new));

    private final List<AbilityEffect> effects;

    public InstantAbility(ResourceLocation icon, Optional<ResourceLocation> smallIcon,
            List<AbilityRequirement> activationRequirements, List<AbilityEffect> effects) {
        super(icon, smallIcon, activationRequirements);
        this.effects = new ArrayList<>(effects);
    }

    @Override
    public MapCodec<? extends Ability> getCodec() {
        return CODEC;
    }

    @Override
    public boolean canActivate(AbilityContext context) {
        if (context.caster().getData(WotrAttachments.ABILITY_COOLDOWNS).isOnCooldown(context.source())) {
            return false;
        }
        return getActivationRequirements().stream().allMatch(x -> x.check(context));
    }

    @Override
    public boolean activate(AbilityContext context) {
        LivingEntity caster = context.caster();
        getActivationRequirements().forEach(x -> x.pay(context));
        this.getEffects().forEach(effect -> effect.apply(caster, List.of(), context));
        return true;
    }

    private List<AbilityEffect> getEffects() {
        return effects;
    }

    public boolean isRelevantModifier(AbstractModifierEffect modifierEffect) {
        return effects.stream().anyMatch(x -> x.isRelevant(modifierEffect)) || super.isRelevantModifier(modifierEffect);
    }
}
