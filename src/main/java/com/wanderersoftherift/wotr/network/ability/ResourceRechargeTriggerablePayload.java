package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityResource;
import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ResourceRechargeTriggerablePayload(Holder<AbilityResource> resource,
        AbilityResource.ModificationEvent event, boolean isRegistration, float expectedResourceAmount)
        implements CustomPacketPayload {

    public static final Type<ResourceRechargeTriggerablePayload> TYPE = new Type<>(WanderersOfTheRift.id(
            "resource_recharge_triggerable"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceRechargeTriggerablePayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.holderRegistry(WotrRegistries.Keys.ABILITY_RESOURCES),
                    ResourceRechargeTriggerablePayload::resource,
                    ByteBufCodecs.fromCodec(AbilityResource.ModificationEvent.CODEC),
                    ResourceRechargeTriggerablePayload::event, ByteBufCodecs.BOOL,
                    ResourceRechargeTriggerablePayload::isRegistration, ByteBufCodecs.FLOAT,
                    ResourceRechargeTriggerablePayload::expectedResourceAmount, ResourceRechargeTriggerablePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        var player = context.player();
        var triggers = TriggerTracker.forEntity(player);
        player.getData(WotrAttachments.ABILITY_RESOURCE_DATA).setAmount(resource, expectedResourceAmount, false);
        if (isRegistration) {
            triggers.registerTriggerable(event.action().type(),
                    new AbilityResource.AbilityResourceRecharge(resource, event));
        } else {
            triggers.unregisterTriggerable(event.action().type(),
                    new AbilityResource.AbilityResourceRecharge(resource, event));
        }
    }

}
