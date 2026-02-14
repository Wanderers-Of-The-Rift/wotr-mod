package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.math.Constants;
import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.abilities.AbilityResource;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.ability.AbilityResourceChangePayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data tracking the state of a player's ability resources
 */
public class AbilityResourceData {

    private static final Codec<Map<ResourceKey<AbilityResource>, Float>> DATA_CODEC = Codec
            .unboundedMap(ResourceKey.codec(WotrRegistries.Keys.ABILITY_RESOURCES), Codec.FLOAT);

    private static final AttachmentSerializerFromDataCodec<Map<ResourceKey<AbilityResource>, Float>, AbilityResourceData> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            DATA_CODEC, AbilityResourceData::new, AbilityResourceData::getAmounts);

    private final IAttachmentHolder holder;
    private Map<Holder<AbilityResource>, AbilityResourceState> amounts = new LinkedHashMap<>();
    private Multimap<Holder<Attribute>, AbilityResourceState.ModificationEventState> watchers = Multimaps
            .newMultimap(new LinkedHashMap<>(), ArrayList::new);

    public AbilityResourceData(IAttachmentHolder holder) {
        this(holder, Collections.emptyMap());
    }

    public AbilityResourceData(IAttachmentHolder holder, Map<ResourceKey<AbilityResource>, Float> amounts) {
        this.holder = holder;

        var reg = ((Entity) holder).registryAccess().lookupOrThrow(WotrRegistries.Keys.ABILITY_RESOURCES);

        reg.asHolderIdMap()
                .forEach(
                        resource -> {
                            var amount = amounts.getOrDefault(resource.getKey(), 0f);
                            var state = createStateIfAbsent(resource);
                            state.setAmount(amount, true);
                            if (holder instanceof LivingEntity livingEntity) {
                                resource.value().events().forEach((key, event) -> {
                                    var attribute = event.amount();
                                    var deltaAmount = livingEntity.getAttributeValue(attribute);
                                    state.setHasNonZeroDelta(key, isNotZero(deltaAmount));
                                });
                            }
                        }
                );

    }

    public static IAttachmentSerializer<Tag, AbilityResourceData> getSerializer() {
        return SERIALIZER;
    }

    /**
     * @return The amount of ability resource available
     */
    public float getAmount(Holder<AbilityResource> resource) {
        return createStateIfAbsent(resource).value;
    }

    public Map<ResourceKey<AbilityResource>, Float> getAmounts() {
        var result = new HashMap<ResourceKey<AbilityResource>, Float>();
        amounts.forEach(
                (resource, state) -> result.put(resource.getKey(), state.value)
        );
        return result;
    }

    /**
     * Consumes an amount of ability resource (will not consume past 0). This will be replicated to the player if on the
     * server.
     *
     * @param resource The ability resource to consume
     * @param quantity The quantity of ability resource to consume
     */
    public void useAmount(Holder<AbilityResource> resource, float quantity) {
        if (quantity == 0) {
            return;
        }
        var state = createStateIfAbsent(resource);
        state.setAmount(state.value - quantity, true);
    }

    /**
     * Sets the amount of ability resource in the pool. This will be replicated to the player if on the server.
     *
     * @param resource         The ability resource to change
     * @param value            The new value of ability resource
     * @param sendClientUpdate whether to notify the client about change in ability resource, check whether holder is
     *                         ServerPlayer is done automatically
     */
    public void setAmount(Holder<AbilityResource> resource, float value, boolean sendClientUpdate) {
        var state = createStateIfAbsent(resource);
        state.setAmount(value, sendClientUpdate);
    }

    private AbilityResourceState createStateIfAbsent(Holder<AbilityResource> resource) {
        var state = amounts.get(resource);
        if (state != null) {
            return state;
        }
        var newState = new AbilityResourceState(resource);
        amounts.put(resource, newState);
        resource.value()
                .events()
                .forEach(
                        (key, event) -> {
                            watchers.put(event.amount(), newState.eventStateMap.get(key));
                        }
                );
        return newState;
    }

    /**
     * Disables/enables triggers for all resources affected by this attribute
     *
     * @param attribute
     */
    public void onAttributeChanged(Holder<Attribute> attribute) {
        var value = ((LivingEntity) holder).getAttributeValue(attribute);
        var triggerTracker = TriggerTracker.forEntity(((LivingEntity) holder));
        var eventStates = watchers.get(attribute);
        var isNonZero = isNotZero(value);
        eventStates.forEach(eventState -> eventState.setHasNonZeroDelta(triggerTracker, isNonZero));

        for (var entry : amounts.entrySet()) {
            if (entry.getKey().value().maximum().equals(attribute)) {
                entry.getValue().onMaxCharged();
            }
        }
    }

    private static boolean isNotZero(double value) {
        return value < -Constants.EPSILON || value > Constants.EPSILON;
    }

    private class AbilityResourceState {
        private final Holder<AbilityResource> resource;
        private float value = 0f;
        private boolean isNotFull = false;
        private boolean isNotEmpty = false;

        private final Map<String, ModificationEventState> eventStateMap;

        private AbilityResourceState(Holder<AbilityResource> resource) {
            this.resource = resource;
            var eventStateMapBuilder = ImmutableMap.<String, ModificationEventState>builder();
            for (var eventEntry : resource.value().events().entrySet()) {
                eventStateMapBuilder.put(eventEntry.getKey(), new ModificationEventState(eventEntry.getValue()));
            }
            eventStateMap = eventStateMapBuilder.build();
        }

        void setAmount(float newAmount, boolean sendClientUpdate) {
            var max = resource.value().maxForEntity(holder);
            var min = 0f;
            value = Math.clamp(newAmount, min, max);
            isNotFull = value != max;
            isNotEmpty = value != min;
            updateTriggers();

            if (sendClientUpdate && holder instanceof ServerPlayer player && player.connection != null) {
                PacketDistributor.sendToPlayer(player, new AbilityResourceChangePayload(resource, value));
            }
        }

        void setHasNonZeroDelta(String eventKey, boolean newValue) {
            var event = eventStateMap.get(eventKey);
            event.hasNonZeroDelta = newValue;
            event.updateTriggers(TriggerTracker.forEntity((Entity) holder));
        }

        void onMaxCharged() {
            var max = resource.value().maxForEntity(holder);
            var oldNotFull = isNotFull;

            isNotFull = value != max;

            if (oldNotFull != isNotFull) {
                updateTriggers();
            }
        }

        private void updateTriggers() {
            var triggerTracker = TriggerTracker.forEntity((Entity) holder);
            eventStateMap.forEach(
                    (key, state) -> state.updateTriggers(triggerTracker)
            );
        }

        public class ModificationEventState {

            private final AbilityResource.ModificationEvent event;
            private boolean hasNonZeroDelta = false;
            private AbilityResource.AbilityResourceRecharge currentTriggerable = null;

            public ModificationEventState(AbilityResource.ModificationEvent event) {
                this.event = event;
            }

            void setHasNonZeroDelta(TriggerTracker triggerTracker, boolean newValue) {
                hasNonZeroDelta = newValue;
                updateTriggers(triggerTracker);
            }

            private void updateTriggers(TriggerTracker triggerTracker) {
                var isAllowedByCurrentAmount = event.isPositive() ? isNotFull : isNotEmpty;

                var shouldRecharge = isAllowedByCurrentAmount && hasNonZeroDelta;
                var isRecharging = currentTriggerable != null;
                if (shouldRecharge == isRecharging) {
                    return;
                }

                if (shouldRecharge) {
                    var predicate = event.action();
                    currentTriggerable = new AbilityResource.AbilityResourceRecharge(resource, event);
                    triggerTracker.registerTriggerable(predicate.type(), currentTriggerable);
                } else {
                    triggerTracker.unregisterTriggerable(event.action().type(), currentTriggerable);
                    currentTriggerable = null;
                }
            }
        }
    }

}
