package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.status.BaseStatusMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SelectStatusMenuPayload(int submenu) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SelectStatusMenuPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "select_status_submenu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectStatusMenuPayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.INT, SelectStatusMenuPayload::submenu, SelectStatusMenuPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (context.player().containerMenu instanceof BaseStatusMenu menu && menu.stillValid(context.player())
                && submenu >= 0 && submenu < BaseStatusMenu.ITEMS.size()) {
            var statusMenuItem = BaseStatusMenu.ITEMS.get(submenu);
            context.player().openMenu(new MenuProvider() {
                @Override
                public @Nullable AbstractContainerMenu createMenu(
                        int containerId,
                        Inventory playerInventory,
                        Player player) {
                    return statusMenuItem.menuSupplier().createMenu(containerId, playerInventory, player);
                }

                @Override
                public Component getDisplayName() {
                    return statusMenuItem.name();
                }

                @Override
                public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                    return false;
                }
            });
        }
    }
}
