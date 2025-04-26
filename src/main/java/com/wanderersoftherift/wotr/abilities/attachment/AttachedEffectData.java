package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.StoredAbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.AttachEffect;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectMarker;
import com.wanderersoftherift.wotr.network.SetEffectMarkerPayload;
import com.wanderersoftherift.wotr.network.UpdateEffectMarkersPayload;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AttachedEffectData is an attachment that allows effects to be attached to an entity. They persist until their caster
 * is no longer available or their ContinueEffectPredicate returns false. They will trigger whenever their
 * TriggerPredicate is true. *
 */
public class AttachedEffectData {
    public static final Codec<AttachedEffectData> CODEC = AttachedEffect.CODEC.listOf()
            .xmap(AttachedEffectData::new, x -> x.effects);

    private final List<AttachedEffect> effects;

    public AttachedEffectData() {
        this(new ArrayList<>());
    }

    private AttachedEffectData(List<AttachedEffect> effects) {
        this.effects = new ArrayList<>(effects);
    }

    /**
     * Ticks all effects, and removes expired effect markers.
     * 
     * @param attachedTo The entity this data is attached to
     * @param level      The level the entity is within
     */
    public void tick(LivingEntity attachedTo, ServerLevel level) {
        List<AttachedEffect> removedEffects = tickEffects(attachedTo, level);
        updateMarkers(attachedTo, removedEffects);
    }

    private @NotNull List<AttachedEffect> tickEffects(LivingEntity attachedTo, ServerLevel level) {
        List<AttachedEffect> removedEffects = new ArrayList<>();
        for (AttachedEffect effect : ImmutableList.copyOf(effects)) {
            if (effect.tick(attachedTo, level)) {
                removedEffects.add(effect);
            }
        }
        effects.removeAll(removedEffects);
        return removedEffects;
    }

    private void updateMarkers(LivingEntity attachedTo, List<AttachedEffect> removedEffects) {
        if (!(attachedTo instanceof ServerPlayer player)) {
            return;
        }
        Map<Holder<EffectMarker>, Integer> updates = new LinkedHashMap<>();
        List<Holder<EffectMarker>> remove = new ArrayList<>();
        for (AttachedEffect effect : removedEffects) {
            effect.attachEffect.getDisplay().ifPresent(display -> {
                if (!updates.containsKey(display) && !remove.contains(display)) {
                    int remainingDuration = getRemainingDuration(display);
                    if (remainingDuration > 0) {
                        updates.put(display, remainingDuration);
                    } else {
                        remove.add(display);
                    }
                }
            });
        }
        PacketDistributor.sendToPlayer(player, new UpdateEffectMarkersPayload(updates, remove));
    }

    private int getRemainingDuration(Holder<EffectMarker> display) {
        return effects.stream()
                .filter(x -> display.equals(x.attachEffect.getDisplay().orElse(null)))
                .mapToInt(AttachedEffect::getRemainingDuration)
                .max()
                .orElse(0);
    }

    /**
     * Adds an AttachEffect.
     * 
     * @param attachedTo   The owner of this attachment
     * @param attachEffect The effect to attach
     * @param context      The context of the effect being attached
     */
    public void attach(LivingEntity attachedTo, AttachEffect attachEffect, AbilityContext context) {
        AttachedEffect newEffect = new AttachedEffect(attachEffect, context);
        if (attachedTo instanceof ServerPlayer player) {
            attachEffect.getDisplay().ifPresent(display -> {
                int existingDuration = getRemainingDuration(display);
                int newDuration = newEffect.getRemainingDuration();
                if (existingDuration < newDuration) {
                    PacketDistributor.sendToPlayer(player, new SetEffectMarkerPayload(display, newDuration));
                }
            });
        }
        effects.add(newEffect);
    }

    /**
     * @return A mapping of EffectMarker to duration for all the current attached effects
     */
    public Map<Holder<EffectMarker>, Integer> getDisplayData() {
        Map<Holder<EffectMarker>, Integer> result = new LinkedHashMap<>();
        for (AttachedEffect effect : effects) {
            effect.attachEffect.getDisplay().ifPresent(display -> {
                if (!result.containsKey(display) || result.get(display) < effect.getRemainingDuration()) {
                    result.put(display, effect.getRemainingDuration());
                }
            });
        }
        return result;
    }

    /**
     * @return if there are no attached effects
     */
    public boolean isEmpty() {
        return effects.isEmpty();
    }

    private static class AttachedEffect {
        private static final Codec<AttachedEffect> CODEC = RecordCodecBuilder
                .create(instance -> instance
                        .group(AttachEffect.CODEC.fieldOf("attachEffect").forGetter(x -> x.attachEffect),
                                StoredAbilityContext.CODEC.fieldOf("context").forGetter(x -> x.context),
                                Codec.INT.fieldOf("triggeredTimes").forGetter(x -> x.triggeredTimes),
                                Codec.INT.fieldOf("ticks").forGetter(x -> x.ticks))
                        .apply(instance, AttachedEffect::new));

        private final AttachEffect attachEffect;
        private final StoredAbilityContext context;
        private int triggeredTimes;
        private int ticks;

        private LivingEntity cachedCaster;

        public AttachedEffect(AttachEffect effect, StoredAbilityContext context, int triggeredTimes, int ticks) {
            this.attachEffect = effect;
            this.context = context;
            this.triggeredTimes = triggeredTimes;
            this.ticks = ticks;
        }

        public AttachedEffect(AttachEffect effect, AbilityContext context) {
            this(effect, new StoredAbilityContext(context), 0, 0);
            this.cachedCaster = context.caster();
        }

        /**
         * Ticks this attached effect
         * 
         * @param attachedTo The entity it is attached to
         * @param level      The level it is within
         * @return Whether this effect has expired and should be removed
         */
        public boolean tick(Entity attachedTo, ServerLevel level) {
            LivingEntity caster = getCaster(level);
            if (caster == null) {
                return true;
            }
            if (attachEffect.getTriggerPredicate().matches(attachedTo, ticks, caster)) {
                AbilityContext triggerContext = context.toContext(getCaster(level));
                triggerContext.enableModifiers();
                try {
                    attachEffect.getEffects()
                            .forEach(child -> child.apply(attachedTo, Collections.emptyList(), triggerContext));
                } finally {
                    triggerContext.disableModifiers();
                }
                triggeredTimes++;
            }
            ticks++;
            return !attachEffect.getContinuePredicate().matches(attachedTo, ticks, triggeredTimes, caster);
        }

        public LivingEntity getCaster(ServerLevel level) {
            if (cachedCaster != null) {
                if (cachedCaster.isRemoved()) {
                    return null;
                } else {
                    return cachedCaster;
                }
            }
            if (level.getEntity(context.getCasterId()) instanceof LivingEntity casterEntity) {
                cachedCaster = casterEntity;
            }
            return cachedCaster;
        }

        public int getRemainingDuration() {
            if (attachEffect.getContinuePredicate().duration() == 0) {
                return Integer.MAX_VALUE;
            }
            return attachEffect.getContinuePredicate().duration() - ticks;
        }

    }
}
