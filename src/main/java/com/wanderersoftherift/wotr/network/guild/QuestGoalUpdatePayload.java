package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.Quest;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record QuestGoalUpdatePayload(Holder<Quest> quest, int goalIndex, int progress) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<QuestGoalUpdatePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "quest_goal_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuestGoalUpdatePayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.holderRegistry(WotrRegistries.Keys.QUESTS), QuestGoalUpdatePayload::quest,
                    ByteBufCodecs.INT, QuestGoalUpdatePayload::goalIndex, ByteBufCodecs.INT,
                    QuestGoalUpdatePayload::progress, QuestGoalUpdatePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.ACTIVE_QUESTS.get()).getQuestState(quest).ifPresent(state -> {
            if (goalIndex >= 0 && goalIndex < state.goalCount()) {
                state.setGoalProgress(goalIndex, progress);
            }
        });
    }
}
