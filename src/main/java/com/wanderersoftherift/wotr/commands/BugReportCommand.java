package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class BugReportCommand extends BaseCommand {

    public BugReportCommand() {
        super("report-bug", Commands.LEVEL_ALL);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        builder.then(Commands.literal("reportBug")).executes(this::openBugReportLink);
    }

    public int openBugReportLink(CommandContext<CommandSourceStack> cmd) {
        Util.getPlatform().openUri("https://github.com/Dimension-Delvers/submit-feedback/issues");
        return 0;
    }
}
