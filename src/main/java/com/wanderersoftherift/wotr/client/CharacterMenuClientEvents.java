package com.wanderersoftherift.wotr.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrCharacterMenuItems;
import com.wanderersoftherift.wotr.network.charactermenu.OpenCharacterMenuPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.wanderersoftherift.wotr.init.client.WotrKeyMappings.GUILD_MENU_KEY;
import static com.wanderersoftherift.wotr.init.client.WotrKeyMappings.QUEST_MENU_KEY;
import static com.wanderersoftherift.wotr.init.client.WotrKeyMappings.WALLET_MENU_KEY;

/**
 * Event handling related to the character menu
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CharacterMenuClientEvents {

    @SubscribeEvent
    public static void processOpenMenuKeys(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.gameMode == null
                || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        if (GUILD_MENU_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new OpenCharacterMenuPayload(WotrCharacterMenuItems.GUILDS));
        }
        if (QUEST_MENU_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new OpenCharacterMenuPayload(WotrCharacterMenuItems.QUESTS));
        }
        if (WALLET_MENU_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new OpenCharacterMenuPayload(WotrCharacterMenuItems.WALLET));
        }
    }
}
