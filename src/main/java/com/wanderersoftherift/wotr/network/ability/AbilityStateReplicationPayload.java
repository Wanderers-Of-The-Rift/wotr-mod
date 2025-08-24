package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilitySource;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityStates;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Replicates the complete ability state of all equipment slots
 * 
 * @param activeSources The list of active slots
 */
public record AbilityStateReplicationPayload(List<AbilitySource> activeSources) implements CustomPacketPayload {
    public static final Type<AbilityStateReplicationPayload> TYPE = new Type<>(
            WanderersOfTheRift.id("ability_state_replication"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityStateReplicationPayload> STREAM_CODEC = StreamCodec
            .composite(AbilitySource.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    AbilityStateReplicationPayload::activeSources, AbilityStateReplicationPayload::new);

    public AbilityStateReplicationPayload(AbilityStates abilityState) {
        this(List.copyOf(abilityState.getActiveSources()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        context.player().getData(WotrAttachments.ABILITY_STATES).clearAndSetActive(activeSources);
    }

}