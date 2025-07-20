package com.wanderersoftherift.wotr.network.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.toast.GoalToast;
import com.wanderersoftherift.wotr.client.toast.QuestToast;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Server to client payload to update the progress of a single goal of an active quest
 * 
 * @param quest
 * @param goalIndex
 * @param progress
 */
public record QuestGoalUpdatePayload(UUID quest, int goalIndex, int progress) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<QuestGoalUpdatePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "quest_goal_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuestGoalUpdatePayload> STREAM_CODEC = StreamCodec
            .composite(
                    UUIDUtil.STREAM_CODEC, QuestGoalUpdatePayload::quest, ByteBufCodecs.INT,
                    QuestGoalUpdatePayload::goalIndex, ByteBufCodecs.INT, QuestGoalUpdatePayload::progress,
                    QuestGoalUpdatePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.ACTIVE_QUESTS.get()).getQuestState(quest).ifPresent(state -> {
            if (goalIndex >= 0 && goalIndex < state.goalCount()) {
                state.setGoalProgress(goalIndex, progress);
                if (state.isComplete()) {
                    Minecraft.getInstance().getToastManager().addToast(new QuestToast(state.getOrigin()));
                } else if (state.isGoalComplete(goalIndex)) {
                    Minecraft.getInstance().getToastManager().addToast(new GoalToast(state, goalIndex));
                }
            }
        });
    }
}
