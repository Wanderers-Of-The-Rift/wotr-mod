package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.guild.quest.QuestState;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ActiveQuestsReplicationPayload(List<QuestState> quests) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ActiveQuestsReplicationPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "active_quest_replication"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ActiveQuestsReplicationPayload> STREAM_CODEC = StreamCodec
            .composite(
                    QuestState.STREAM_CODEC.apply(ByteBufCodecs.list()), ActiveQuestsReplicationPayload::quests,
                    ActiveQuestsReplicationPayload::new);

    public ActiveQuestsReplicationPayload(ActiveQuests quests) {
        this(new ArrayList<>(quests.getQuestList()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.ACTIVE_QUESTS.get()).resyncFromServer(quests);
    }
}
