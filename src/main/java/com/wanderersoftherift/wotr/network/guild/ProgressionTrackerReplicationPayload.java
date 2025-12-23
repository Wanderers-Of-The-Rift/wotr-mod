package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTrack;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTracker;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Payload for replicating ProgressionTrack status to a client player
 * 
 * @param progressionStatus status of each progression track
 */
public record ProgressionTrackerReplicationPayload(
        Map<Holder<ProgressionTrack>, ProgressionTracker.ProgressionData> progressionStatus)
        implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ProgressionTrackerReplicationPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "progression_tracker_replication"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProgressionTrackerReplicationPayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.map(LinkedHashMap::new, ProgressionTrack.STREAM_CODEC,
                            ProgressionTracker.ProgressionData.STREAM_CODEC),
                    ProgressionTrackerReplicationPayload::progressionStatus, ProgressionTrackerReplicationPayload::new);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.PROGRESSION_TRACKER.get()).setAll(progressionStatus);
    }
}
