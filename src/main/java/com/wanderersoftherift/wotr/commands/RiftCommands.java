package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Commands relating to the rift level for testing
 */
public class RiftCommands extends BaseCommand {

    public RiftCommands() {
        super("rift", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        builder.then(Commands.literal("exit").executes(this::exitRift));
    }

    private int exitRift(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        ServerLevel level = ctx.getSource().getLevel();

        if (player != null && RiftData.isRift(level)) {
            RiftLevelManager.returnPlayerFromRift(player);
            return 1;
        }

        return 0;
    }
}
