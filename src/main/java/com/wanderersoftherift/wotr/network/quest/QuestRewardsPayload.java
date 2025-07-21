package com.wanderersoftherift.wotr.network.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.Reward;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestRewardMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Server to client payload to provide the list of (non-item) rewards for the
 * {@link com.wanderersoftherift.wotr.gui.menu.quest.QuestRewardMenu}
 * 
 * @param rewards
 */
public record QuestRewardsPayload(List<Reward> rewards) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<QuestRewardsPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "quest_rewards"));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuestRewardsPayload> STREAM_CODEC = StreamCodec.composite(
            Reward.STREAM_CODEC.apply(ByteBufCodecs.list()), QuestRewardsPayload::rewards, QuestRewardsPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        if (context.player().containerMenu instanceof QuestRewardMenu menu) {
            menu.setRewards(context.player(), rewards);
        }
    }
}
