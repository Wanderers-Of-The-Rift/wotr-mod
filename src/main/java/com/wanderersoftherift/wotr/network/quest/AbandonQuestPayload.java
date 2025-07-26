package com.wanderersoftherift.wotr.network.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrAttachments;
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
 * Client to server payload for abandoning a quest
 * 
 * @param questId
 */
public record AbandonQuestPayload(UUID questId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AbandonQuestPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "abandon_quest"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbandonQuestPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, AbandonQuestPayload::questId, AbandonQuestPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            player.getData(WotrAttachments.ACTIVE_QUESTS).remove(questId);
        }
    }
}
