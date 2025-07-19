package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.commands.AbilityCommands;
import com.wanderersoftherift.wotr.commands.BugReportCommand;
import com.wanderersoftherift.wotr.commands.DebugCommands;
import com.wanderersoftherift.wotr.commands.EssenceCommands;
import com.wanderersoftherift.wotr.commands.HudCommands;
import com.wanderersoftherift.wotr.commands.RiftCommands;
import com.wanderersoftherift.wotr.commands.RiftKeyCommands;
import com.wanderersoftherift.wotr.commands.RiftMapCommands;
import com.wanderersoftherift.wotr.commands.SpawnPieceCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class WotrCommands {

    private WotrCommands() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        SpawnPieceCommand.register(event.getDispatcher(), event.getBuildContext());
        new DebugCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        AbilityCommands.register(event.getDispatcher(), event.getBuildContext());
        new RiftKeyCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new EssenceCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new BugReportCommand().registerCommand(event.getDispatcher(), event.getBuildContext());
        new RiftCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new HudCommands().registerCommand(event.getDispatcher(), event.getBuildContext());

        if (FMLEnvironment.dist.isClient()) {
            RiftMapCommands.register(event.getDispatcher(), event.getBuildContext());
        }
    }
}
