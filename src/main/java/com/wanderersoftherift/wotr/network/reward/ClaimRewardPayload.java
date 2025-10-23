package com.wanderersoftherift.wotr.network.reward;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.gui.menu.reward.RewardMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Payload for claiming non-item rewards from the Rewards menu
 * 
 * @param reward
 */
public record ClaimRewardPayload(Reward reward) implements CustomPacketPayload {
    public static final Type<ClaimRewardPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "claim_reward"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClaimRewardPayload> STREAM_CODEC = StreamCodec
            .composite(Reward.STREAM_CODEC, ClaimRewardPayload::reward, ClaimRewardPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (context.player().containerMenu instanceof RewardMenu menu && menu.stillValid(context.player())) {
            menu.claimReward(context.player(), reward);
        }
    }
}
