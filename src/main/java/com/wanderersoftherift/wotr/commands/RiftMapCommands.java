package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.wanderersoftherift.wotr.gui.screen.RiftMapScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class RiftMapCommands extends BaseCommand {

    public RiftMapCommands() {
        super("riftMap", Commands.LEVEL_ALL);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        builder.then(
                Commands.literal("open").then(Commands.argument("rd", IntegerArgumentType.integer()).executes((ctx) -> {
                    Minecraft.getInstance()
                            .execute(() -> Minecraft.getInstance()
                                    .setScreen(new RiftMapScreen(Component.literal("test"),
                                            IntegerArgumentType.getInteger(ctx, "rd"))));
                    return 1;
                })));
    }

}
