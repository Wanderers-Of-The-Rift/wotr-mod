package com.wanderersoftherift.wotr.gui.menu.status;

import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseStatusMenu extends AbstractContainerMenu {
    public static final List<StatusMenuItem> ITEMS = List.of(
            new StatusMenuItem(Component.literal("Guilds"), WotrMenuTypes.GUILDS_MENU.get(),
                    (id, inventory, player) -> new GuildMenu(id, inventory,
                            ContainerLevelAccess.create(player.level(), player.getOnPos()))),
            new StatusMenuItem(Component.literal("Wallet"), WotrMenuTypes.WALLET_MENU.get(),
                    (id, inventory, player) -> new WalletMenu(id, inventory,
                            ContainerLevelAccess.create(player.level(), player.getOnPos())))
    );

    protected BaseStatusMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }
}
