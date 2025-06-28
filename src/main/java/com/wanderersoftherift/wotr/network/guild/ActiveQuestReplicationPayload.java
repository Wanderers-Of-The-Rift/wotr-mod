package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuest;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ActiveQuestReplicationPayload(List<ActiveQuest> quests) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ActiveQuestReplicationPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "active_quest_replication"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ActiveQuestReplicationPayload> STREAM_CODEC = StreamCodec
            .composite(
                    ActiveQuest.STREAM_CODEC.apply(ByteBufCodecs.list()), ActiveQuestReplicationPayload::quests,
                    ActiveQuestReplicationPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.ACTIVE_QUESTS.get()).updateFromServer(quests);
    }
}
