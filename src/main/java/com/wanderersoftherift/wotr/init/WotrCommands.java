package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.commands.AbilityResourceCommands;
import com.wanderersoftherift.wotr.commands.BugReportCommand;
import com.wanderersoftherift.wotr.commands.CurrencyCommands;
import com.wanderersoftherift.wotr.commands.DebugCommands;
import com.wanderersoftherift.wotr.commands.ExportCommands;
import com.wanderersoftherift.wotr.commands.HudCommands;
import com.wanderersoftherift.wotr.commands.NPCCommands;
import com.wanderersoftherift.wotr.commands.RiftCommands;
import com.wanderersoftherift.wotr.commands.RiftKeyCommands;
import com.wanderersoftherift.wotr.commands.RiftMapCommands;
import com.wanderersoftherift.wotr.commands.SpawnPieceCommand;
import com.wanderersoftherift.wotr.commands.StatCommands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class WotrCommands {

    private WotrCommands() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        new SpawnPieceCommand().registerCommand(event.getDispatcher(), event.getBuildContext());
        new DebugCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new NPCCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new RiftKeyCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new RiftCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new CurrencyCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new StatCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new AbilityResourceCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        new BugReportCommand().registerCommand(event.getDispatcher(), event.getBuildContext());
        new RiftMapCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new HudCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new ExportCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
    }
}
