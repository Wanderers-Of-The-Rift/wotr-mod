package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.screen.AbilityBenchScreen;
import com.wanderersoftherift.wotr.gui.screen.KeyForgeScreen;
import com.wanderersoftherift.wotr.gui.screen.RiftCompleteScreen;
import com.wanderersoftherift.wotr.gui.screen.RuneAnvilScreen;
import com.wanderersoftherift.wotr.gui.screen.TradingScreen;
import com.wanderersoftherift.wotr.gui.screen.character.GuildsScreen;
import com.wanderersoftherift.wotr.gui.screen.character.WalletScreen;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WotrScreens {

    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(WotrMenuTypes.RUNE_ANVIL_MENU.get(), RuneAnvilScreen::new);
        event.register(WotrMenuTypes.KEY_FORGE_MENU.get(), KeyForgeScreen::new);
        event.register(WotrMenuTypes.ABILITY_BENCH_MENU.get(), AbilityBenchScreen::new);
        event.register(WotrMenuTypes.RIFT_COMPLETE_MENU.get(), RiftCompleteScreen::new);
        event.register(WotrMenuTypes.TRADING_MENU.get(), TradingScreen::new);
        event.register(WotrMenuTypes.GUILDS_MENU.get(), GuildsScreen::new);
        event.register(WotrMenuTypes.WALLET_MENU.get(), WalletScreen::new);
    }
}
