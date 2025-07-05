package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuests;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AbandonQuestPayload(int questSlot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AbandonQuestPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "abandon_quest"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbandonQuestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AbandonQuestPayload::questSlot, AbandonQuestPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            ActiveQuests quests = player.getData(WotrAttachments.ACTIVE_QUESTS);
            if (questSlot >= 0 && questSlot < quests.count()) {
                quests.remove(questSlot);
                PacketDistributor.sendToPlayer(player, new ActiveQuestReplicationPayload(quests));
            }
        }
    }
}
