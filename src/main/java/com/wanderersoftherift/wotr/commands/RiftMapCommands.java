package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.screen.RiftMapScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class RiftMapCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
                Commands.literal(WanderersOfTheRift.MODID + ":riftmap")
                        .then(Commands.argument("rd", IntegerArgumentType.integer())
                        .executes(
                        (ctx) -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new RiftMapScreen(Component.literal("test"), IntegerArgumentType.getInteger(ctx, "rd") )));
                            return 1;
                        }
                ))
        );
    }
}
