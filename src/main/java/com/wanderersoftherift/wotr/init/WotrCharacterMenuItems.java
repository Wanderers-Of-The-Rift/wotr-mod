package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.character.CharacterMenuItem;
import com.wanderersoftherift.wotr.gui.menu.character.GuildMenu;
import com.wanderersoftherift.wotr.gui.menu.character.OrderHint;
import com.wanderersoftherift.wotr.gui.menu.character.QuestMenu;
import com.wanderersoftherift.wotr.gui.menu.character.WalletMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrCharacterMenuItems {

    public static final DeferredRegister<CharacterMenuItem> MENU_ITEMS = DeferredRegister
            .create(WotrRegistries.Keys.CHARACTER_MENU_ITEMS, WanderersOfTheRift.MODID);

    public static final Supplier<CharacterMenuItem> GUILDS = MENU_ITEMS.register("guilds", () -> new CharacterMenuItem(
            Component.translatable(WanderersOfTheRift.translationId("container", "guilds")),
            WotrMenuTypes.GUILDS_MENU.get(),
            (id, inventory, player) -> new GuildMenu(id, inventory,
                    ContainerLevelAccess.create(player.level(), player.getOnPos())),
            OrderHint.NONE, null
    ));

    public static final Supplier<CharacterMenuItem> WALLET = MENU_ITEMS.register("wallet", () -> new CharacterMenuItem(
            Component.translatable(WanderersOfTheRift.translationId("container", "wallet")),
            WotrMenuTypes.WALLET_MENU.get(),
            (id, inventory, player) -> new WalletMenu(id, inventory,
                    ContainerLevelAccess.create(player.level(), player.getOnPos())),
            OrderHint.AFTER, WotrMenuTypes.GUILDS_MENU.get()
    ));

    public static final Supplier<CharacterMenuItem> QUESTS = MENU_ITEMS.register("quests", () -> new CharacterMenuItem(
            Component.translatable(WanderersOfTheRift.translationId("container", "quests")),
            WotrMenuTypes.QUEST_MENU.get(),
            (id, inventory, player) -> new QuestMenu(id, inventory,
                    ContainerLevelAccess.create(player.level(), player.getOnPos())),
            OrderHint.AFTER, WotrMenuTypes.WALLET_MENU.get()
    ));

}