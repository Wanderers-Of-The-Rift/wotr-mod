package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityStates;
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
import java.util.function.BiFunction;

public class ChainAbility implements Ability {

    public static final MapCodec<ChainAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    AbilityElement.CODEC.listOf(1, Integer.MAX_VALUE)
                            .fieldOf("abilities")
                            .forGetter(ChainAbility::getAbilities)
            ).apply(instance, ChainAbility::new));

    private final List<AbilityElement> abilities;

    public ChainAbility(List<AbilityElement> abilities) {
        this.abilities = abilities;
    }

    @Override
    public MapCodec<? extends Ability> getCodec() {
        return CODEC;
    }

    @Override
    public Component getDisplayName(ResourceLocation abilityId) {
        Holder<Ability> firstAbility = abilities.getFirst().ability;
        return firstAbility.value().getDisplayName(firstAbility.getKey().location());
    }

    @Override
    public Component getDisplayName(ResourceLocation abilityId, LivingEntity entity, AbilitySource source) {
        return queryCurrentChild(entity, source, (ability, childSource) -> ability.value()
                .getDisplayName(ability.getKey().location(), entity, childSource));
    }

    @Override
    public ResourceLocation getIcon() {
        return abilities.getFirst().ability.value().getIcon();
    }

    @Override
    public ResourceLocation getIcon(LivingEntity entity, AbilitySource source) {
        return queryCurrentChild(entity, source,
                (ability, childSource) -> ability.value().getIcon(entity, childSource));
    }

    @Override
    public ResourceLocation getEmblem() {
        return abilities.getFirst().ability.value().getEmblem();
    }

    @Override
    public boolean canActivate(AbilityContext context) {
        return queryCurrentChild(context.caster(), context.source(),
                (child, childSource) -> child.value().canActivate(context.forSubAbility(child, childSource)));
    }

    @Override
    public boolean activate(AbilityContext context) {
        context.caster().getData(WotrAttachments.CHAIN_ABILITY_STATES).setActivated(context.source());
        int index = currentIndex(context.caster(), context.source());
        ChainAbilitySource childSource = new ChainAbilitySource(context.source(), index);
        Holder<Ability> childAbility = abilities.get(index).ability();
        if (context.caster()
                .getData(WotrAttachments.ONGOING_ABILITIES)
                .activate(childSource, context.abilityItem(), childAbility)) {
            boolean childFinished = !childAbility.value().isActive(context.caster(), childSource);
            if (childFinished) {
                return incrementChain(context, index);
            }
        }
        return false;
    }

    @Override
    public boolean tick(AbilityContext context) {
        int index = currentIndex(context.caster(), context.source());
        ChainAbilityStates chainStates = context.caster().getData(WotrAttachments.CHAIN_ABILITY_STATES);
        if (chainStates.hasBeenActivated(context.source())) {
            ChainAbilitySource childSource = new ChainAbilitySource(context.source(), index);
            if (!context.caster().getData(WotrAttachments.ABILITY_STATES).isActive(childSource)) {
                return incrementChain(context, index);
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

    private boolean incrementChain(AbilityContext context, int index) {
        AbilityStates states = context.caster().getData(WotrAttachments.ABILITY_STATES);
        int newIndex = (index + 1) % abilities.size();
        states.setState(context.source(), newIndex);
        if (newIndex != 0) {
            context.caster()
                    .getData(WotrAttachments.CHAIN_ABILITY_STATES)
                    .setResetAge(context.source(), context.age() + abilities.get(newIndex).ticksToReset);
        }
        return newIndex == 0;
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

    public List<AbilityElement> getAbilities() {
        return abilities;
    }

    public int currentIndex(LivingEntity owner, AbilitySource source) {
        return owner.getData(WotrAttachments.ABILITY_STATES).getState(source);
    }

    private <T> T queryCurrentChild(
            LivingEntity entity,
            AbilitySource source,
            BiFunction<Holder<Ability>, ChainAbilitySource, T> function) {
        int index = currentIndex(entity, source);
        ChainAbilitySource childSource = new ChainAbilitySource(source, index);
        Holder<Ability> childAbility = abilities.get(index).ability();
        return function.apply(childAbility, childSource);
    }

    public record AbilityElement(Holder<Ability> ability, int ticksToReset, boolean autoActivate) {
        public static final Codec<AbilityElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ability.CODEC.fieldOf("ability").forGetter(AbilityElement::ability),
                Codec.INT.optionalFieldOf("ticks_to_reset", 100).forGetter(AbilityElement::ticksToReset),
                Codec.BOOL.optionalFieldOf("auto_activate", false).forGetter(AbilityElement::autoActivate)
        ).apply(instance, AbilityElement::new));
    }
}
