package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityCooldowns;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AbilityCooldownReplicationPayload(List<AbilityCooldownUpdatePayload> cooldowns)
        implements CustomPacketPayload {
    public static final Type<AbilityCooldownReplicationPayload> TYPE = new Type<>(
            WanderersOfTheRift.id("ability_cooldown_replication"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityCooldownReplicationPayload> STREAM_CODEC = StreamCodec
            .composite(AbilityCooldownUpdatePayload.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    AbilityCooldownReplicationPayload::cooldowns, AbilityCooldownReplicationPayload::new);

    public AbilityCooldownReplicationPayload(AbilityCooldowns data) {
        this(data.getCooldowns()
                .map(x -> new AbilityCooldownUpdatePayload(x.slot(), x.timeRange().from(), x.timeRange().until()))
                .toList());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        AbilityCooldowns playerCooldowns = context.player().getData(WotrAttachments.ABILITY_COOLDOWNS);
        playerCooldowns.clear();
        for (AbilityCooldownUpdatePayload cooldownPayload : cooldowns) {
            cooldownPayload.handleOnClient(context);
        }
    }

}