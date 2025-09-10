package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.ChainAbilityStates;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.abilities.sources.ChainAbilitySource;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import com.wanderersoftherift.wotr.util.LongRange;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public record ChainAbility(List<AbilityElement> abilities) implements Ability {

    public static final MapCodec<ChainAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    AbilityElement.CODEC.listOf(1, Integer.MAX_VALUE)
                            .fieldOf("abilities")
                            .forGetter(ChainAbility::abilities)
            ).apply(instance, ChainAbility::new));

    @Override
    public MapCodec<? extends Ability> getCodec() {
        return CODEC;
    }

    @Override
    public Component getDisplayName(ResourceLocation abilityId, LivingEntity entity, AbilitySource source) {
        int index = currentIndex(entity, source);
        ChainAbilitySource subSource = new ChainAbilitySource(source, index);
        Holder<Ability> subAbility = abilities.get(index).ability();
        return subAbility.value().getDisplayName(subAbility.getKey().location(), entity, subSource);
    }

    @Override
    public ResourceLocation getIcon() {
        return abilities.getFirst().ability.value().getIcon();
    }

    @Override
    public ResourceLocation getIcon(LivingEntity entity, AbilitySource source) {
        int index = currentIndex(entity, source);
        ChainAbilitySource subSource = new ChainAbilitySource(source, index);
        Holder<Ability> subAbility = abilities.get(index).ability();
        return subAbility.value().getIcon(entity, subSource);
    }

    @Override
    public ResourceLocation getEmblem() {
        return abilities.getFirst().ability.value().getEmblem();
    }

    @Override
    public boolean canActivate(AbilityContext context) {
        int index = currentIndex(context.caster(), context.source());
        ChainAbilitySource subSource = new ChainAbilitySource(context.source(), index);
        Holder<Ability> subAbility = abilities.get(index).ability();
        return subAbility.value().canActivate(context.forSubAbility(subAbility, subSource));
    }

    @Override
    public boolean activate(AbilityContext context) {
        return progressChain(context, currentIndex(context.caster(), context.source()));
    }

    private boolean progressChain(AbilityContext context, int index) {
        boolean childComplete = activateChild(context, index);
        while (childComplete && ++index < abilities.size() && abilities.get(index).autoActivate) {
            childComplete = activateChild(context, index);
        }
        if (childComplete && index >= abilities.size()) {
            deactivate(context);
            return true;
        }
        updateState(context, index, !childComplete);
        return false;
    }

    private void updateState(AbilityContext context, int index, boolean active) {
        context.caster().getData(WotrAttachments.ABILITY_STATES).setState(context.source(), index);
        if (active) {
            context.caster().getData(WotrAttachments.CHAIN_ABILITY_STATES).setActivated(context.source());
        } else {
            context.caster()
                    .getData(WotrAttachments.CHAIN_ABILITY_STATES)
                    .setResetAge(context.source(), context.age() + abilities.get(index).ticksToReset);
        }
    }

    /**
     * @param context
     * @param index
     * @return whether the child ability has completed
     */
    private boolean activateChild(AbilityContext context, int index) {
        ChainAbilitySource childSource = new ChainAbilitySource(context.source(), index);
        Holder<Ability> childAbility = abilities.get(index).ability();
        if (context.caster()
                .getData(WotrAttachments.ONGOING_ABILITIES)
                .activate(childSource, context.abilityItem(), childAbility)) {
            return !childAbility.value().isActive(context.caster(), childSource);
        }
        return true;
    }

    @Override
    public boolean tick(AbilityContext context) {
        int index = currentIndex(context.caster(), context.source());
        ChainAbilityStates chainStates = context.caster().getData(WotrAttachments.CHAIN_ABILITY_STATES);
        if (chainStates.hasBeenActivated(context.source())) {
            ChainAbilitySource childSource = new ChainAbilitySource(context.source(), index);
            if (!context.caster().getData(WotrAttachments.ABILITY_STATES).isActive(childSource)) {
                index++;
                if (index >= abilities.size()) {
                    deactivate(context);
                    return true;
                }
                if (abilities.get(index).autoActivate) {
                    return progressChain(context, index);
                } else {
                    updateState(context, index, false);
                }
            }
        } else if (context.age() >= chainStates.resetTime(context.source())) {
            deactivate(context);
            return true;
        }
        return false;
    }

    @Override
    public void deactivate(AbilityContext context) {
        context.caster().getData(WotrAttachments.ABILITY_STATES).setState(context.source(), 0);
        context.caster().getData(WotrAttachments.CHAIN_ABILITY_STATES).clear(context.source());
    }

    public boolean isActive(LivingEntity entity, AbilitySource source) {
        int index = entity.getData(WotrAttachments.ABILITY_STATES).getState(source);
        return abilities.get(index).ability.value().isActive(entity, new ChainAbilitySource(source, index));
    }

    @Override
    public LongRange getCooldown(LivingEntity entity, AbilitySource source) {
        int index = entity.getData(WotrAttachments.ABILITY_STATES).getState(source);
        return abilities.get(index).ability.value().getCooldown(entity, new ChainAbilitySource(source, index));
    }

    @Override
    public boolean isRelevantModifier(ModifierEffect modifierEffect) {
        return abilities.stream()
                .map(AbilityElement::ability)
                .map(Holder::value)
                .anyMatch(x -> x.isRelevantModifier(modifierEffect));
    }

    public int currentIndex(LivingEntity owner, AbilitySource source) {
        return owner.getData(WotrAttachments.ABILITY_STATES).getState(source);
    }

    public record AbilityElement(Holder<Ability> ability, int ticksToReset, boolean autoActivate) {
        public static final Codec<AbilityElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ability.CODEC.fieldOf("ability").forGetter(AbilityElement::ability),
                Codec.INT.optionalFieldOf("ticks_to_reset", 100).forGetter(AbilityElement::ticksToReset),
                Codec.BOOL.optionalFieldOf("auto_activate", false).forGetter(AbilityElement::autoActivate)
        ).apply(instance, AbilityElement::new));
    }
}
