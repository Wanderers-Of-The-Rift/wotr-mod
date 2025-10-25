package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.abilities.triggers.TrackableTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.TriggerPredicate;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AbilityTriggerablePayload(AbilitySource source, Holder<Ability> ability, TriggerPredicate<?> predicate,
        boolean isRegistration) implements CustomPacketPayload {

    public static final Type<AbilityTriggerablePayload> TYPE = new Type<>(WanderersOfTheRift.id(
            "ability_triggerable"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityTriggerablePayload> STREAM_CODEC = StreamCodec
            .composite(
                    AbilitySource.STREAM_CODEC, AbilityTriggerablePayload::source,
                    ByteBufCodecs.holderRegistry(WotrRegistries.Keys.ABILITIES), AbilityTriggerablePayload::ability,
                    ByteBufCodecs.fromCodecWithRegistries(TriggerPredicate.CODEC), AbilityTriggerablePayload::predicate,
                    ByteBufCodecs.BOOL, AbilityTriggerablePayload::isRegistration, AbilityTriggerablePayload::new
            );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        var player = context.player();
        var triggers = TriggerTracker.forEntity(player);
        if (isRegistration) {
            triggers.registerTriggerable(predicate.type(), new ClientTriggerable(predicate, ability, source));
        } else {
            triggers.unregisterTriggerable(predicate.type(), new ClientTriggerable(predicate, ability, source));
        }
    }

    private record ClientTriggerable(TriggerPredicate<?> predicate, Holder<Ability> ability, AbilitySource source)
            implements TriggerTracker.Triggerable {

        @Override
        public boolean trigger(LivingEntity holder, TrackableTrigger activation) {
            var flag = holder.getData(WotrAttachments.ONGOING_ABILITIES)
                    .activate(source, ability, activation::addComponents);
            var type = activation.type();
            if (flag && type.clientTriggerInstance() != null) {
                PacketDistributor.sendToServer(new ClientTriggerTriggerPayload(type));
            }
            return flag;
        }

        @Override
        public void sendUnregister(ServerPlayer player) {

        }

        @Override
        public void sendRegister(ServerPlayer player) {

        }
    }
}
