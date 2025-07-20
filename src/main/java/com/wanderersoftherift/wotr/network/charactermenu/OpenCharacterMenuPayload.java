package com.wanderersoftherift.wotr.network.charactermenu;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.character.BaseCharacterMenu;
import com.wanderersoftherift.wotr.gui.menu.character.CharacterMenuItem;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
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

/**
 * Client to server payload for opening a character menu
 * 
 * @param menuItem
 */
public record OpenCharacterMenuPayload(Holder<CharacterMenuItem> menuItem) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenCharacterMenuPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "open_character_menu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCharacterMenuPayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.holderRegistry(WotrRegistries.Keys.CHARACTER_MENU_ITEMS),
                    OpenCharacterMenuPayload::menuItem, OpenCharacterMenuPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (!context.player().isSpectator() && !context.player().isDeadOrDying()) {
            context.player().openMenu(new MenuProvider() {

                @Override
                public @NotNull Component getDisplayName() {
                    return menuItem.value().name();
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(
                        int containerId,
                        @NotNull Inventory playerInventory,
                        @NotNull Player player) {
                    return menuItem.value().menuSupplier().createMenu(containerId, playerInventory, player);
                }

                @Override
                public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                    return !(context.player().containerMenu instanceof BaseCharacterMenu);
                }
            });
        }
    }
}
