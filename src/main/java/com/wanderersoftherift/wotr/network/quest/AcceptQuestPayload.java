package com.wanderersoftherift.wotr.network.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Client to server payload for accepting a quest in the quest giver menu
 * 
 * @param index
 */
public record AcceptQuestPayload(int index) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AcceptQuestPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "accept_quest"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AcceptQuestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AcceptQuestPayload::index, AcceptQuestPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (context.player().containerMenu instanceof QuestGiverMenu menu && menu.stillValid(context.player())) {
            menu.acceptQuest((ServerPlayer) context.player(), index);
        }

    }
}
