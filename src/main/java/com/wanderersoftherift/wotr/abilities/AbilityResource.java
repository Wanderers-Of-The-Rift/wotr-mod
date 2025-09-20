package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.ability.ResourceRechargeTriggerablePayload;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.Map;

public record AbilityResource(int color, Holder<Attribute> maximum, Map<String, ModificationEvent> events) {

    public static final Codec<Holder<AbilityResource>> HOLDER_CODEC = LaxRegistryCodec
            .create(WotrRegistries.Keys.ABILITY_RESOURCES);
    public static final Codec<AbilityResource> DIRECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("color").forGetter(AbilityResource::color),
                    Attribute.CODEC.fieldOf("maximum").forGetter(AbilityResource::maximum),
                    Codec.unboundedMap(Codec.STRING, ModificationEvent.CODEC)
                            .fieldOf("modificators")
                            .forGetter(AbilityResource::events)
            ).apply(instance, AbilityResource::new)
    );

    public float maxForEntity(IAttachmentHolder holder) {
        if (holder instanceof LivingEntity e) {
            return (float) e.getAttributeValue(maximum);
        }
        return 0;
    }

    public float respawnValueForEntity(Entity entity) {
        return maxForEntity(entity);
    }

    public record ModificationEvent(Holder<Attribute> amount, TrackableTrigger.TriggerPredicate<?> action,
            boolean isPositive) {

        public static final Codec<ModificationEvent> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Attribute.CODEC.fieldOf("amount").forGetter(ModificationEvent::amount),
                        TrackableTrigger.TriggerPredicate.CODEC.fieldOf("action").forGetter(ModificationEvent::action),
                        Codec.BOOL.fieldOf("is_positive").forGetter(ModificationEvent::isPositive)
                ).apply(instance, ModificationEvent::new)
        );
    }

    public record AbilityResourceRecharge(Holder<AbilityResource> resource, ModificationEvent event)
            implements TriggerTracker.Triggerable {

        @Override
        public TrackableTrigger.TriggerPredicate<?> predicate() {
            return event.action;
        }

        @Override
        public boolean trigger(LivingEntity entity, TrackableTrigger activation) {
            if (predicate().type().value() != activation.type()) {
                return false;
            }
            var abilityResources = entity.getData(WotrAttachments.ABILITY_RESOURCE_DATA);
            var amount = abilityResources.getAmount(resource);
            var delta = (float) entity.getAttributeValue(event.amount);
            if (event.isPositive) {
                amount += delta;
            } else {
                amount -= delta;
            }
            abilityResources.setAmount(resource, amount, !event.action().canBeHandledByClient());
            return delta != 0;
        }

        @Override
        public void sendUnregister(ServerPlayer player) {
            var abilityResources = player.getData(WotrAttachments.ABILITY_RESOURCE_DATA);
            var amount = abilityResources.getAmount(resource);
            player.connection.send(new ResourceRechargeTriggerablePayload(resource, event, false, amount));
        }

        @Override
        public void sendRegister(ServerPlayer player) {
            var abilityResources = player.getData(WotrAttachments.ABILITY_RESOURCE_DATA);
            var amount = abilityResources.getAmount(resource);
            player.connection.send(new ResourceRechargeTriggerablePayload(resource, event, true, amount));
        }
    }
}
