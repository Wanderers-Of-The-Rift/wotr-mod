package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record HandInQuestItemPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<HandInQuestItemPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "hand_in_quest_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HandInQuestItemPayload> STREAM_CODEC = StreamCodec
            .unit(new HandInQuestItemPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (context.player().containerMenu instanceof QuestCompletionMenu menu && menu.stillValid(context.player())) {
            menu.handIn((ServerPlayer) context.player());
        }

    }
}
