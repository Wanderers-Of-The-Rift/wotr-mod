package com.wanderersoftherift.wotr.network.charactermenu;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.character.GuildMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OpenCharacterMenuPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenCharacterMenuPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "open_character_menu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCharacterMenuPayload> STREAM_CODEC = StreamCodec
            .unit(new OpenCharacterMenuPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (!context.player().isSpectator() && !context.player().isDeadOrDying()) {
            context.player()
                    .openMenu(new SimpleMenuProvider(
                            ((containerId, playerInventory, player) -> new GuildMenu(containerId, playerInventory,
                                    ContainerLevelAccess.create(player.level(), player.getOnPos()))),
                            Component.translatable(WanderersOfTheRift.translationId("container", "guilds"), "Guild")));
        }

    }
}
