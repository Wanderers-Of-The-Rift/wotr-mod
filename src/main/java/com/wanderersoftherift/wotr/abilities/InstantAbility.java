package com.wanderersoftherift.wotr.abilities;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.effects.AbilityEffect;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InstantAbility implements Ability {

    public static final MapCodec<InstantAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("icon").forGetter(InstantAbility::getIcon),
                    ResourceLocation.CODEC.optionalFieldOf("small_icon").forGetter(x -> x.smallIcon),
                    Codec.BOOL.optionalFieldOf("in_creative_menu", true).forGetter(InstantAbility::isInCreativeMenu),
                    AbilityRequirement.CODEC.listOf()
                            .optionalFieldOf("requirements", List.of())
                            .forGetter(InstantAbility::getActivationRequirements),
                    Codec.list(AbilityEffect.DIRECT_CODEC)
                            .optionalFieldOf("effects", Collections.emptyList())
                            .forGetter(InstantAbility::getEffects)
            ).apply(instance, InstantAbility::new));

    private final ResourceLocation icon;
    private final Optional<ResourceLocation> smallIcon;
    private final List<AbilityRequirement> activationRequirements;
    private final List<AbilityEffect> effects;
    private final boolean inCreativeMenu;

    public InstantAbility(ResourceLocation icon, Optional<ResourceLocation> smallIcon, boolean inCreativeMenu,
            List<AbilityRequirement> activationRequirements, List<AbilityEffect> effects) {
        this.icon = icon;
        this.smallIcon = smallIcon;
        this.effects = ImmutableList.copyOf(effects);
        this.activationRequirements = ImmutableList.copyOf(activationRequirements);
        this.inCreativeMenu = inCreativeMenu;
    }

    @Override
    public MapCodec<? extends Ability> getCodec() {
        return CODEC;
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getEmblemIcon() {
        return smallIcon.orElse(icon);
    }

    @Override
    public boolean isInCreativeMenu() {
        return inCreativeMenu;
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

    public List<AbilityRequirement> getActivationRequirements() {
        return activationRequirements;
    }

    public boolean isRelevantModifier(ModifierEffect modifierEffect) {
        return effects.stream().anyMatch(x -> x.isRelevant(modifierEffect))
                || activationRequirements.stream().anyMatch(x -> x.isRelevant(modifierEffect));
    }
}
