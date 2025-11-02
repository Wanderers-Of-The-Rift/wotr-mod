package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.ChainAbilityState;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.abilities.sources.ChainAbilitySource;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import com.wanderersoftherift.wotr.util.LongRange;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record ChainAbility(boolean inCreativeMenu, List<Entry> abilities) implements Ability {

    public static final MapCodec<ChainAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("in_creative_menu", true).forGetter(ChainAbility::isInCreativeMenu),
                    Entry.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("abilities").forGetter(ChainAbility::abilities)
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
    public boolean isInCreativeMenu() {
        return inCreativeMenu;
    }

    @Override
    public ResourceLocation getIcon(LivingEntity entity, AbilitySource source) {
        int index = currentIndex(entity, source);
        ChainAbilitySource subSource = new ChainAbilitySource(source, index);
        Holder<Ability> subAbility = abilities.get(index).ability();
        return subAbility.value().getIcon(entity, subSource);
    }

    @Override
    public ResourceLocation getEmblemIcon() {
        return abilities.getFirst().ability.value().getEmblemIcon();
    }

    @Override
    public boolean canActivate(AbilityContext context) {
        int index = currentIndex(context.caster(), context.source());
        ChainAbilitySource subSource = new ChainAbilitySource(context.source(), index);
        var subAbilityEntry = abilities.get(index);
        if (!subAbilityEntry.requirements().stream().allMatch(x -> x.check(context))) {
            return false;
        }
        Holder<Ability> subAbility = subAbilityEntry.ability();
        return subAbility.value().canActivate(context.forSubAbility(subAbility, subSource));
    }

    @Override
    public boolean activate(AbilityContext context) {
        return progressChain(context, currentIndex(context.caster(), context.source()));
    }

    private boolean progressChain(AbilityContext context, int index) {
        boolean childComplete = activateChild(context, index);
        while (childComplete && (index = nextIndex(index)) < abilities.size() && abilities.get(index).autoActivate) {
            childComplete = activateChild(context, index);
        }
        if (childComplete && index >= abilities.size()) {
            deactivate(context);
            return true;
        }
        updateState(context, index, !childComplete);
        return false;
    }

    private int nextIndex(int index) {
        return this.abilities.get(index).next.map(it -> it.sample(RandomSource.create())).orElse(index + 1);
    }

    private void updateState(AbilityContext context, int index, boolean active) {
        context.caster().getData(WotrAttachments.ABILITY_STATES).setState(context.source(), index);
        if (active) {
            context.set(WotrDataComponentType.AbilityContextData.CHAIN_ABILITY_STATE, new ChainAbilityState(0, true));
        } else {
            context.set(WotrDataComponentType.AbilityContextData.CHAIN_ABILITY_STATE,
                    new ChainAbilityState(context.age() + abilities.get(index).ticksToReset, false));
        }
    }

    /**
     * @param context
     * @param index
     * @return whether the child ability has completed
     */
    private boolean activateChild(AbilityContext context, int index) {
        ChainAbilitySource childSource = new ChainAbilitySource(context.source(), index);
        var subAbilityEntry = abilities.get(index);
        Holder<Ability> childAbility = subAbilityEntry.ability();
        subAbilityEntry.requirements().forEach(it -> it.pay(context));
        if (context.caster()
                .getData(WotrAttachments.ONGOING_ABILITIES)
                .activate(childSource, childAbility, (childContext) -> childContext
                        .set(WotrDataComponentType.AbilityContextData.PARENT_ABILITY, context.instanceId()))) {
            return !childAbility.value().isActive(context.caster(), childSource);
        }
        return true;
    }

    @Override
    public boolean tick(AbilityContext context) {
        int index = currentIndex(context.caster(), context.source());
        ChainAbilityState chainState = context
                .getOrDefault(WotrDataComponentType.AbilityContextData.CHAIN_ABILITY_STATE, ChainAbilityState.DEFAULT);
        if (chainState.activated()) {
            return updateSubabilityState(context, index);
        }
        return checkForChainReset(context, chainState);
    }

    private boolean checkForChainReset(AbilityContext context, ChainAbilityState chainState) {
        if (context.age() >= chainState.resetAge()) {
            deactivate(context);
            return true;
        }
        return false;
    }

    private boolean updateSubabilityState(AbilityContext context, int index) {
        ChainAbilitySource currentSubabilitySource = new ChainAbilitySource(context.source(), index);
        if (context.caster().getData(WotrAttachments.ABILITY_STATES).isActive(currentSubabilitySource)) {
            return false;
        }
        index = nextIndex(index);
        if (index >= abilities.size()) {
            deactivate(context);
            return true;
        }
        if (abilities.get(index).autoActivate) {
            return progressChain(context, index);
        }
        updateState(context, index, false);
        return false;
    }

    @Override
    public void deactivate(AbilityContext context) {
        context.caster().getData(WotrAttachments.ABILITY_STATES).setState(context.source(), 0);
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
                .map(Entry::ability)
                .map(Holder::value)
                .anyMatch(x -> x.isRelevantModifier(modifierEffect));
    }

    public int currentIndex(LivingEntity owner, AbilitySource source) {
        return owner.getData(WotrAttachments.ABILITY_STATES).getState(source);
    }

    public record Entry(Holder<Ability> ability, int ticksToReset, boolean autoActivate,
            List<AbilityRequirement> requirements, Optional<IntProvider> next) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ability.CODEC.fieldOf("ability").forGetter(Entry::ability),
                Codec.INT.optionalFieldOf("ticks_to_reset", 100).forGetter(Entry::ticksToReset),
                Codec.BOOL.optionalFieldOf("auto_activate", false).forGetter(Entry::autoActivate),
                AbilityRequirement.CODEC.listOf()
                        .optionalFieldOf("requirements", Collections.emptyList())
                        .forGetter(Entry::requirements),
                IntProvider.CODEC.optionalFieldOf("next").forGetter(Entry::next)
        ).apply(instance, Entry::new));
    }
}
