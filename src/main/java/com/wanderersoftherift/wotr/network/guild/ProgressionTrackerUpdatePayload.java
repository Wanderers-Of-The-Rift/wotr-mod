package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.toast.ProgressionTrackRankToast;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTrack;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTracker;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Payload for updating the status of a single progression track for a client
 * 
 * @param track
 * @param data
 */
public record ProgressionTrackerUpdatePayload(Holder<ProgressionTrack> track, ProgressionTracker.ProgressionData data)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ProgressionTrackerUpdatePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "progression_track_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProgressionTrackerUpdatePayload> STREAM_CODEC = StreamCodec
            .composite(
                    ProgressionTrack.STREAM_CODEC, ProgressionTrackerUpdatePayload::track,
                    ProgressionTracker.ProgressionData.STREAM_CODEC, ProgressionTrackerUpdatePayload::data,
                    ProgressionTrackerUpdatePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        handleClientOnly(context);
    }

    /*
     * Client-side only: references other Client-only classes and would cause crash if it existed on the server. The
     * main handler still has to exist on client and server since it's directly referenced in the packet registration.
     */
    @OnlyIn(Dist.CLIENT)
    public void handleClientOnly(final IPayloadContext context) {
        ProgressionTracker tracker = context.player().getData(WotrAttachments.PROGRESSION_TRACKER.get());
        int previousRank = tracker.getRank(track);
        tracker.setData(track, data);
        if (track.value().hasRankUpToast() && previousRank < data.getRank()) {
            Minecraft.getInstance().getToastManager().addToast(new ProgressionTrackRankToast(track, data.getRank()));
        }
    }

}
