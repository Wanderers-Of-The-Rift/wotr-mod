package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.StoredAbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.AttachEffect;
import com.wanderersoftherift.wotr.abilities.effects.attachment.EffectMarkerInstance;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.source.AttachEffectModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import com.wanderersoftherift.wotr.network.ability.AddEffectMarkerPayload;
import com.wanderersoftherift.wotr.network.ability.RemoveEffectMarkerPayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * AttachedEffects is an attachment that allows effects to be attached to an entity. They persist until their caster is
 * no longer available or their ContinueEffectPredicate returns false. They will trigger whenever their TriggerPredicate
 * is true.
 */
public class AttachedEffects {
    private static final AttachmentSerializerFromDataCodec<List<AttachedEffect>, AttachedEffects> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            AttachedEffect.CODEC.listOf(), AttachedEffects::new, (x) -> x.effects);

    private final IAttachmentHolder holder;
    private final List<AttachedEffect> effects;

    public AttachedEffects(@NotNull IAttachmentHolder holder) {
        this(holder, new ArrayList<>());
    }

    private AttachedEffects(IAttachmentHolder holder, List<AttachedEffect> effects) {
        this.holder = holder;
        this.effects = new ArrayList<>(effects);
        if (holder instanceof Entity attachedTo) {
            for (var effect : effects) {
                effect.onAttach(attachedTo);
            }
            var registry = attachedTo.level().getData(WotrAttachments.ATTACHED_EFFECT_ENTITY_REGISTRY);
            registry.add(attachedTo);
        }
    }

    /**
     * Ticks all effects, and removes expired effect markers.
     */
    public void tick() {
        if (!(holder instanceof LivingEntity attachedTo)) {
            return;
        }
        for (AttachedEffect effect : ImmutableList.copyOf(effects)) {
            effect.tick(attachedTo);
        }
        detachEffectsIf(AttachedEffect::isExpired);
    }

    /**
     * Adds an AttachEffect.
     *
     * @param attachEffect The effect to attach
     * @param context      The context of the effect being attached
     */
    public void attach(AttachEffect attachEffect, AbilityContext context) {
        if (!(holder instanceof Entity entity)) {
            return;
        }
        AttachedEffect newEffect = new AttachedEffect(attachEffect, context);
        newEffect.onAttach(entity);
        effects.add(newEffect);
    }

    /**
     * @param instanceId The ability instance to check for attachments from
     * @return Whether there are any attach effects from the given ability
     */
    public boolean has(UUID instanceId) {
        return effects.stream().anyMatch(x -> x.context.instanceId().equals(instanceId));
    }

    /**
     *
     * @param instanceId The ability instance to check for attachments from
     * @param predicate  A predicate the AttachEffect must meet
     * @return Whether there are any attach effects from the given ability and matching the AttachEffect predicate
     */
    public boolean has(UUID instanceId, Predicate<AttachEffect> predicate) {
        return effects.stream()
                .anyMatch(x -> x.context.instanceId().equals(instanceId) && predicate.test(x.attachEffect));
    }

    /**
     * Detaches all effects linked to an ability
     * 
     * @param instanceId The id of the ability instance
     */
    public void detach(UUID instanceId) {
        detachEffectsIf(effect -> effect.context.instanceId().equals(instanceId));
    }

    /**
     * Detaches all effects linked to an ability that meet a predicate
     *
     * @param instanceId The id of the ability instance
     * @param predicate  A check for valid attach effects to remove
     */
    public void detach(UUID instanceId, Predicate<AttachEffect> predicate) {
        detachEffectsIf(
                effect -> effect.context.instanceId().equals(instanceId) && predicate.test(effect.attachEffect));
    }

    private void detachEffectsIf(Predicate<AttachedEffect> predicate) {
        if (!(holder instanceof Entity entity)) {
            return;
        }
        this.effects.removeIf(effect -> {
            if (predicate.test(effect)) {
                effect.onDetach(entity);
                return true;
            }
            return false;
        });
    }

    public List<ModifierInstance> getModifiers(UUID id) {
        return this.effects.stream()
                .filter(it -> it.id.equals(id))
                .map(it -> it.attachEffect.getModifiers())
                .findAny()
                .orElse(Collections.emptyList());
    }

    /**
     * @return if there are no attached effects
     */
    public boolean isEmpty() {
        return effects.isEmpty();
    }

    /**
     * Replicates all effects to the holder
     */
    public void replicateEffects() {
        if (!(holder instanceof ServerPlayer player)) {
            return;
        }
        for (var effect : effects) {
            effect.addEffectMarker(player);
        }
    }

    public static IAttachmentSerializer<Tag, AttachedEffects> getSerializer() {
        return SERIALIZER;
    }

    private static class AttachedEffect {
        private static final Codec<AttachedEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(x -> x.id),
                AttachEffect.CODEC.fieldOf("attachEffect").forGetter(x -> x.attachEffect),
                StoredAbilityContext.CODEC.fieldOf("context").forGetter(x -> x.context),
                Codec.INT.fieldOf("triggeredTimes").forGetter(x -> x.triggeredTimes),
                Codec.INT.fieldOf("age").forGetter(x -> x.age)).apply(instance, AttachedEffect::new));

        private final UUID id;
        private final AttachEffect attachEffect;
        private final StoredAbilityContext context;
        private int triggeredTimes;
        private int age;
        private boolean expired;

        private LivingEntity cachedCaster;

        private AttachedEffect(UUID id, AttachEffect effect, StoredAbilityContext context, int triggeredTimes,
                int age) {
            this.id = id;
            this.attachEffect = effect;
            this.context = context;
            this.triggeredTimes = triggeredTimes;
            this.age = age;
        }

        public AttachedEffect(AttachEffect effect, AbilityContext context) {
            this(UUID.randomUUID(), effect, new StoredAbilityContext(context), 0, 0);
            this.cachedCaster = context.caster();
        }

        public void onAttach(Entity attachedTo) {
            applyModifiers(attachedTo);
            // player connection can be null because the player might still be in the process of being constructed.
            if (attachedTo instanceof ServerPlayer player && player.connection != null) {
                addEffectMarker(player);
            }
        }

        public void onDetach(Entity attachedTo) {
            removeModifiers(attachedTo);
            if (attachedTo instanceof ServerPlayer player) {
                removeEffectMarker(player);
            }
        }

        /**
         * Ticks this attached effect
         * 
         * @param attachedTo The entity it is attached to
         * @return Whether this effect has expired and should be removed
         */
        public void tick(Entity attachedTo) {
            if (!(attachedTo.level() instanceof ServerLevel level)) {
                expired = true;
                return;
            }
            LivingEntity caster = getCaster(level.getServer());
            if (caster == null) {
                expired = true;
                return;
            }
            if (attachEffect.getTriggerPredicate().matches(attachedTo, age, caster)) {
                AbilityContext triggerContext = context.toContext(caster, attachedTo.level());
                try (var ignore = triggerContext.enableTemporaryUpgradeModifiers()) {
                    attachEffect.getEffects()
                            .forEach(child -> child.apply(attachedTo, Collections.emptyList(), triggerContext));
                }
                triggeredTimes++;
            }
            age++;
            expired = !attachEffect.getContinuePredicate().matches(attachedTo, age, triggeredTimes, caster);
        }

        public boolean isExpired() {
            return expired;
        }

        private LivingEntity getCaster(MinecraftServer server) {
            if (cachedCaster != null) {
                if (cachedCaster.getRemovalReason() == Entity.RemovalReason.CHANGED_DIMENSION) {
                    cachedCaster = context.getCaster(server);
                } else if (cachedCaster.isRemoved()) {
                    cachedCaster = null;
                }
            } else {
                cachedCaster = context.getCaster(server);
            }
            return cachedCaster;
        }

        private void applyModifiers(Entity attachedTo) {
            for (int i = 0; i < attachEffect.getModifiers().size(); i++) {
                ModifierInstance modifier = attachEffect.getModifiers().get(i);
                ModifierSource source = new AttachEffectModifierSource(id, i);
                modifier.modifier().value().enableModifier(modifier.roll(), attachedTo, source, modifier.tier());
            }
        }

        private void removeModifiers(Entity attachedTo) {
            for (int i = 0; i < attachEffect.getModifiers().size(); i++) {
                ModifierInstance modifier = attachEffect.getModifiers().get(i);
                ModifierSource source = new AttachEffectModifierSource(id, i);
                modifier.modifier().value().disableModifier(modifier.roll(), attachedTo, source, modifier.tier());
            }
        }

        private void addEffectMarker(ServerPlayer player) {
            if (attachEffect.getDisplay().isPresent()) {
                Long until = null;
                if (attachEffect.getContinuePredicate().duration() < Integer.MAX_VALUE) {
                    until = player.level().getGameTime() + attachEffect.getContinuePredicate().duration() - age;
                }
                PacketDistributor.sendToPlayer(player, new AddEffectMarkerPayload(
                        new EffectMarkerInstance(id, attachEffect.getDisplay(), Optional.ofNullable(until))));
            }
        }

        private void removeEffectMarker(ServerPlayer player) {
            if (attachEffect.getDisplay().isPresent()) {
                PacketDistributor.sendToPlayer(player, new RemoveEffectMarkerPayload(id));
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof AttachedEffect other) {
                return this.id.equals(other.id);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}
