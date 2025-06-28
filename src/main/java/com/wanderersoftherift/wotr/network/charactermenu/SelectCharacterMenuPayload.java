package com.wanderersoftherift.wotr.network.charactermenu;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.character.BaseCharacterMenu;
import com.wanderersoftherift.wotr.gui.menu.character.CharacterMenuItem;
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

import java.util.List;

public record SelectCharacterMenuPayload(int submenu) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SelectCharacterMenuPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "select_character_submenu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectCharacterMenuPayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.INT, SelectCharacterMenuPayload::submenu, SelectCharacterMenuPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        List<CharacterMenuItem> items = BaseCharacterMenu.getSortedMenuItems(context.player().level().registryAccess());
        if (context.player().containerMenu instanceof BaseCharacterMenu menu && menu.stillValid(context.player())
                && submenu >= 0 && submenu < items.size()) {
            var menuItem = items.get(submenu);
            context.player().openMenu(new MenuProvider() {
                @Override
                public @Nullable AbstractContainerMenu createMenu(
                        int containerId,
                        Inventory playerInventory,
                        Player player) {
                    return menuItem.menuSupplier().createMenu(containerId, playerInventory, player);
                }

                @Override
                public Component getDisplayName() {
                    return menuItem.name();
                }

                @Override
                public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                    return false;
                }
            });
        }
    }
}
