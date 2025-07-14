package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Debug commands for testing the inventory snapshot system
 */
public class InventorySnapshotCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("wotr:createInventorySnapshot")
                .executes((ctx) -> createInventorySnapshot(ctx.getSource())));

    }

    private static int createInventorySnapshot(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            // InventorySnapshotSystem.captureSnapshot(player, );
            source.sendFailure(Component.literal("Unsupported command, No snapshot created"));
            return 0;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

}
