package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTrack;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Payload for claiming rank up rewards from the progression tracks character screen
 * 
 * @param track
 */
public record ClaimTrackRewardPayload(Holder<ProgressionTrack> track) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClaimTrackRewardPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "claim_track_reward"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClaimTrackRewardPayload> STREAM_CODEC = StreamCodec
            .composite(ProgressionTrack.STREAM_CODEC, ClaimTrackRewardPayload::track, ClaimTrackRewardPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        context.player()
                .getExistingData(WotrAttachments.PROGRESSION_TRACKER)
                .ifPresent(tracker -> tracker.claimRewards(track));
    }
}
