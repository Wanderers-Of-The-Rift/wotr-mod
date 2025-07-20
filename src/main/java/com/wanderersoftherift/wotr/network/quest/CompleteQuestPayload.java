package com.wanderersoftherift.wotr.network.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Client to server payload to complete a quest in the quest completion menu
 *
 * @param questId
 */
public record CompleteQuestPayload(UUID questId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CompleteQuestPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "complete_quest"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompleteQuestPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, CompleteQuestPayload::questId, CompleteQuestPayload::new);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (context.player().containerMenu instanceof QuestCompletionMenu menu && menu.stillValid(context.player())) {
            menu.completeQuest((ServerPlayer) context.player(), questId);
        }
    }
}
