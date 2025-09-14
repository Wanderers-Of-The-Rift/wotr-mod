package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.abilities.triggers.TriggerPredicate;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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
            triggers.registerAbilityTrigger(predicate, ability, source);
        } else {
            triggers.unregisterAbilityTrigger(predicate, ability, source);
        }
    }
}
