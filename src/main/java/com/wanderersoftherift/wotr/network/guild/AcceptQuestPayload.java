package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.Quest;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AcceptQuestPayload(Holder<Quest> quest) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AcceptQuestPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "accept_quest"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AcceptQuestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(WotrRegistries.Keys.QUESTS), AcceptQuestPayload::quest,
            AcceptQuestPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (context.player().containerMenu instanceof QuestGiverMenu menu && menu.stillValid(context.player())) {
            menu.acceptQuest((ServerPlayer) context.player(), quest);
        }

    }
}
