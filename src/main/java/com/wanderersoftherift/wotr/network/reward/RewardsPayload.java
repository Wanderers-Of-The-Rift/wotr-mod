package com.wanderersoftherift.wotr.network.reward;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.gui.menu.reward.RewardMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Server to client payload to provide the list of (non-item) rewards for the {@link RewardMenu}
 * 
 * @param rewards
 */
public record RewardsPayload(List<Reward> rewards) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RewardsPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "rewards"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RewardsPayload> STREAM_CODEC = StreamCodec.composite(
            Reward.STREAM_CODEC.apply(ByteBufCodecs.list()), RewardsPayload::rewards, RewardsPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        if (context.player().containerMenu instanceof RewardMenu menu) {
            menu.addRewards(context.player(), rewards);
        }
    }
}
