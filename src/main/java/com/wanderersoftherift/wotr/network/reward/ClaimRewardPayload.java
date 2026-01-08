package com.wanderersoftherift.wotr.network.reward;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.reward.RewardProvidingMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Payload for claiming non-item rewards from the Rewards menu
 * 
 * @param rewardId
 */
public record ClaimRewardPayload(int rewardId) implements CustomPacketPayload {
    public static final Type<ClaimRewardPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "claim_reward"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClaimRewardPayload> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.INT, ClaimRewardPayload::rewardId, ClaimRewardPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (context.player().containerMenu instanceof RewardProvidingMenu menu
                && context.player().containerMenu.stillValid(context.player())) {
            menu.claimReward(context.player(), rewardId);
        }
    }
}
