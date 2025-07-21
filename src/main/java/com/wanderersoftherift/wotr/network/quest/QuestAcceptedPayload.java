package com.wanderersoftherift.wotr.network.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Server to client payload to accept a quest in the Quest Giver menu
 * 
 * @param quest
 */
public record QuestAcceptedPayload(QuestState quest) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<QuestAcceptedPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "quest_accepted"));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuestAcceptedPayload> STREAM_CODEC = StreamCodec.composite(
            QuestState.STREAM_CODEC, QuestAcceptedPayload::quest, QuestAcceptedPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.ACTIVE_QUESTS.get()).add(quest);
    }
}
