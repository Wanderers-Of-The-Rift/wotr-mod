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

public record QuestRemovedPayload(Holder<Quest> quest) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<QuestRemovedPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "quest_removed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuestRemovedPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(WotrRegistries.Keys.QUESTS), QuestRemovedPayload::quest,
            QuestRemovedPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.ACTIVE_QUESTS.get()).remove(quest);
    }
}
