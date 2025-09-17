package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.Optional;

public record AbilityResource(int color, Holder<Attribute> maximum, Optional<Holder<Attribute>> recharge,
        Optional<TrackableTrigger.TriggerPredicate<?>> rechargeAction, Optional<Holder<Attribute>> degen,
        Optional<TrackableTrigger.TriggerPredicate<?>> degenAction) {

    public static final Codec<Holder<AbilityResource>> HOLDER_CODEC = LaxRegistryCodec
            .create(WotrRegistries.Keys.ABILITY_RESOURCES);
    public static final Codec<AbilityResource> DIRECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("color").forGetter(AbilityResource::color),
                    Attribute.CODEC.fieldOf("maximum").forGetter(AbilityResource::maximum),
                    Attribute.CODEC.optionalFieldOf("recharge_amount").forGetter(AbilityResource::recharge),
                    TrackableTrigger.TriggerPredicate.CODEC.optionalFieldOf("recharge_action")
                            .forGetter(AbilityResource::rechargeAction),
                    Attribute.CODEC.optionalFieldOf("degen_amount").forGetter(AbilityResource::degen),
                    TrackableTrigger.TriggerPredicate.CODEC.optionalFieldOf("degen_action")
                            .forGetter(AbilityResource::degenAction)
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

    public record AbilityResourceRecharge(Holder<AbilityResource> resource, Holder<Attribute> recharge,
            boolean isNegative, TrackableTrigger.TriggerPredicate<?> predicate) implements TriggerTracker.Triggerable {

        @Override
        public boolean trigger(LivingEntity entity, TrackableTrigger activation) {
            if (predicate().type().value() != activation.type()
                    || !((TrackableTrigger.TriggerPredicate<TrackableTrigger>) predicate()).test(activation)) {
                return false;
            }
            var abilityResources = entity.getData(WotrAttachments.ABILITY_RESOURCE_DATA);
            var amount = abilityResources.getAmount(resource);
            var delta = (float) entity.getAttributeValue(recharge);
            if (isNegative) {
                amount -= delta;
            } else {
                amount += delta;
            }
            abilityResources.setAmount(resource, amount);
            return delta != 0;
        }
    }
}
