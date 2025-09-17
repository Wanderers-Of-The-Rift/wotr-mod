package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.abilities.AbilityResource;
import com.wanderersoftherift.wotr.network.ability.AbilityResourceChangePayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data tracking the state of a player's mana pool
 */
public class AbilityResourceData {

    private static final Codec<Map<Holder<AbilityResource>, Float>> DATA_CODEC = Codec
            .unboundedMap(AbilityResource.HOLDER_CODEC, Codec.FLOAT);

    private static final AttachmentSerializerFromDataCodec<Map<Holder<AbilityResource>, Float>, AbilityResourceData> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            DATA_CODEC, AbilityResourceData::new, AbilityResourceData::getAmounts);

    private final IAttachmentHolder holder;
    private Map<Holder<AbilityResource>, AbilityResourceState> amounts = new LinkedHashMap<>();
    private Map<Holder<Attribute>, AbilityResourceState> degenNotifiers = new LinkedHashMap<>();
    private Map<Holder<Attribute>, AbilityResourceState> rechargeNotifiers = new LinkedHashMap<>();

    public AbilityResourceData(IAttachmentHolder holder) {
        this(holder, Collections.emptyMap());
    }

    public AbilityResourceData(IAttachmentHolder holder, Map<Holder<AbilityResource>, Float> amounts) {
        this.holder = holder;

        amounts.forEach((resource, amount) -> {
            var state = createStateIfAbsent(resource);
            state.setAmount(amount);
            if (holder instanceof LivingEntity livingEntity) {
                var rechargeAttribute = resource.value().recharge();
                if (rechargeAttribute.isPresent()) {
                    var rechargeAmount = livingEntity.getAttributeValue(rechargeAttribute.get());
                    state.setHasNonZeroRecharge(rechargeAmount > -1e-6 && rechargeAmount < 1e-6);
                }
                var degenAttribute = resource.value().degen();
                if (degenAttribute.isPresent()) {
                    var degenAmount = livingEntity.getAttributeValue(degenAttribute.get());
                    state.setHasNonZeroDegen(degenAmount > -1e-6 && degenAmount < 1e-6);
                }
            }
        });

    }

    public static IAttachmentSerializer<Tag, AbilityResourceData> getSerializer() {
        return SERIALIZER;
    }

    /**
     * @return The amount of mana available
     */
    public float getAmount(Holder<AbilityResource> resource) {
        return createStateIfAbsent(resource).value;
    }

    public Map<Holder<AbilityResource>, Float> getAmounts() {
        var result = new HashMap<Holder<AbilityResource>, Float>();
        amounts.forEach(
                (resource, state) -> result.put(resource, state.value)
        );
        return result;
    }

    /**
     * Consumes an amount of mana (will not consume past 0). This will be replicated to the player if on the server.
     * 
     * @param quantity The quantity of mana to consume
     */
    public void useAmount(Holder<AbilityResource> resource, float quantity) {
        if (quantity == 0) {
            return;
        }
        var state = createStateIfAbsent(resource);
        state.setAmount(state.value - quantity);
    }

    /**
     * Sets the amount of mana in the pool. This will be replicated to the player if on the server.
     * 
     * @param value The new value of mana
     */
    public void setAmount(Holder<AbilityResource> resource, float value) {
        var state = createStateIfAbsent(resource);
        state.setAmount(value);
    }

    private AbilityResourceState createStateIfAbsent(Holder<AbilityResource> resource) {
        var state = amounts.get(resource);
        if (state != null) {
            return state;
        }
        var newState = new AbilityResourceState(resource);
        amounts.put(resource, newState);
        resource.value().degen().ifPresent(it -> degenNotifiers.put(it, newState));
        resource.value().recharge().ifPresent(it -> rechargeNotifiers.put(it, newState));
        return newState;
    }

    public void onAttributeChanged(Holder<Attribute> attribute) {
        var value = ((LivingEntity) holder).getAttributeValue(attribute);
        var degenState = degenNotifiers.get(attribute);
        if (degenState != null) {
            degenState.setHasNonZeroDegen(value < -1e-6 || value > 1e-6);
        }
        var rechargeState = rechargeNotifiers.get(attribute);
        if (rechargeState != null) {
            rechargeState.setHasNonZeroRecharge(value < -1e-6 || value > 1e-6);
        }
    }

    private class AbilityResourceState {
        private final Holder<AbilityResource> resource;
        private float value = 0f;
        private boolean isFull = false;
        private boolean isEmpty = false;
        private boolean hasNonZeroRecharge = false;
        private boolean hasNonZeroDegen = false;
        private AbilityResource.AbilityResourceRecharge currentRechargeTriggerable = null;
        private AbilityResource.AbilityResourceRecharge currentDegenTriggerable = null;

        private AbilityResourceState(Holder<AbilityResource> resource) {
            this.resource = resource;
        }

        void setAmount(float newAmount) {
            if (newAmount > value) {
                var max = resource.value().maxForEntity(holder);
                value = Math.min(max, newAmount);
                isFull = value == max;
                isEmpty = false;
            } else {
                var min = 0f;
                value = Math.max(min, newAmount);
                isEmpty = value == min;
                isFull = false;
            }
            updateTriggers();

            if (holder instanceof ServerPlayer player && player.connection != null) {
                PacketDistributor.sendToPlayer(player, new AbilityResourceChangePayload(resource, newAmount));
            }
        }

        void setHasNonZeroRecharge(boolean newValue) {
            hasNonZeroRecharge = newValue;
            updateTriggers();
        }

        void setHasNonZeroDegen(boolean newValue) {
            hasNonZeroDegen = newValue;
            updateTriggers();
        }

        private void updateTriggers() {
            var shouldRecharge = !isFull && hasNonZeroRecharge && resource.value().rechargeAction().isPresent()
                    && resource.value().recharge().isPresent();
            var shouldDegen = !isEmpty && hasNonZeroDegen && resource.value().degenAction().isPresent()
                    && resource.value().degen().isPresent();
            var triggerTracker = TriggerTracker.forEntity((Entity) holder);
            if (!shouldRecharge && currentRechargeTriggerable != null) {
                triggerTracker.unregisterTriggerable(resource.value().rechargeAction().get().type(),
                        currentRechargeTriggerable);
                currentRechargeTriggerable = null;
            }
            if (shouldRecharge && currentRechargeTriggerable == null) {
                var predicate = resource.value().rechargeAction().get();
                currentRechargeTriggerable = new AbilityResource.AbilityResourceRecharge(resource,
                        resource.value().recharge().get(), false, predicate);
                triggerTracker.registerTriggerable(predicate.type(), currentRechargeTriggerable);
            }
            if (!shouldDegen && currentDegenTriggerable != null) {
                triggerTracker.unregisterTriggerable(resource.value().degenAction().get().type(),
                        currentDegenTriggerable);
                currentDegenTriggerable = null;
            }
            if (shouldDegen && currentDegenTriggerable == null) {
                var predicate = resource.value().degenAction().get();
                currentDegenTriggerable = new AbilityResource.AbilityResourceRecharge(resource,
                        resource.value().degen().get(), true, predicate);
                triggerTracker.registerTriggerable(predicate.type(), currentDegenTriggerable);
            }
        }
    }

}
