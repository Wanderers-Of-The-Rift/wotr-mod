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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public class ChainAbility extends Ability {

    public static final MapCodec<ChainAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("icon").forGetter(ChainAbility::getIcon),
                    ResourceLocation.CODEC.optionalFieldOf("small_icon").forGetter(ChainAbility::getSmallIcon),
                    AbilityElement.CODEC.listOf(1, Integer.MAX_VALUE)
                            .fieldOf("abilities")
                            .forGetter(ChainAbility::getAbilities)
            ).apply(instance, ChainAbility::new));

    private final List<AbilityElement> abilities;

    public ChainAbility(ResourceLocation icon, Optional<ResourceLocation> smallIcon, List<AbilityElement> abilities) {
        super(icon, smallIcon);
        this.abilities = abilities;
    }

    @Override
    public MapCodec<? extends Ability> getCodec() {
        return CODEC;
    }

    @Override
    public boolean canActivate(AbilityContext context) {
        int index = currentIndex(context.caster(), context.source());
        ChainAbilitySource childSource = new ChainAbilitySource(context.source(), index);
        Holder<Ability> childAbility = abilities.get(index).ability();
        return childAbility.value().canActivate(context.forSubAbility(childAbility, childSource));
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
                return incrementChain(context, index, 0);
            }
        }
        return false;
    }

    @Override
    public boolean tick(AbilityContext context, long age) {
        int index = currentIndex(context.caster(), context.source());
        ChainAbilityStates chainStates = context.caster().getData(WotrAttachments.CHAIN_ABILITY_STATES);
        if (chainStates.hasBeenActivated(context.source())) {
            ChainAbilitySource childSource = new ChainAbilitySource(context.source(), index);
            if (!context.caster().getData(WotrAttachments.ABILITY_STATES).isActive(childSource)) {
                return incrementChain(context, index, age);
            }
        } else if (age >= chainStates.resetTime(context.source())) {
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

    private boolean incrementChain(AbilityContext context, int index, long age) {
        AbilityStates states = context.caster().getData(WotrAttachments.ABILITY_STATES);
        int newIndex = (index + 1) % abilities.size();
        states.setState(context.source(), newIndex);
        if (newIndex != 0) {
            context.caster()
                    .getData(WotrAttachments.CHAIN_ABILITY_STATES)
                    .setResetAge(context.source(), age + abilities.get(newIndex).ticksToReset);
        }
        return newIndex == 0;
    }

    public ResourceLocation getIcon(LivingEntity entity, AbilitySource source) {
        int index = entity.getData(WotrAttachments.ABILITY_STATES).getState(source);
        return abilities.get(index).ability.value().getIcon(entity, new ChainAbilitySource(source, index));
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

    // TODO: Current display names

    @Override
    public ResourceLocation getIcon() {
        return abilities.getFirst().ability.value().getIcon();
    }

    @Override
    public Optional<ResourceLocation> getSmallIcon() {
        return abilities.getFirst().ability.value().getSmallIcon();
    }

    public int currentIndex(LivingEntity owner, AbilitySource source) {
        return owner.getData(WotrAttachments.ABILITY_STATES).getState(source);
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

    public record AbilityElement(Holder<Ability> ability, int ticksToReset, boolean autoActivate) {
        public static final Codec<AbilityElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ability.CODEC.fieldOf("ability").forGetter(AbilityElement::ability),
                Codec.INT.optionalFieldOf("ticks_to_reset", 100).forGetter(AbilityElement::ticksToReset),
                Codec.BOOL.optionalFieldOf("auto_activate", false).forGetter(AbilityElement::autoActivate)
        ).apply(instance, AbilityElement::new));
    }
}
