package com.wanderersoftherift.wotr.network.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Server to client payload to provide a list of available quests for the quest giver menu
 * 
 * @param quests
 */
public record AvailableQuestsPayload(List<QuestState> quests) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AvailableQuestsPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "available_quests"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AvailableQuestsPayload> STREAM_CODEC = StreamCodec
            .composite(
                    QuestState.STREAM_CODEC.apply(ByteBufCodecs.list()), AvailableQuestsPayload::quests,
                    AvailableQuestsPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        if (context.player().containerMenu instanceof QuestGiverMenu menu) {
            menu.setAvailableQuests(quests);
        }
    }
}
