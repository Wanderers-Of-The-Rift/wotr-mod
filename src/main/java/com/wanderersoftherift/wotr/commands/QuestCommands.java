package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.QuestLog;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Commands relating to quests
 */
public class QuestCommands extends BaseCommand {

    private static final Component SUCCESS = Component
            .translatable(WanderersOfTheRift.translationId("command", "generic.success"));
    private static final Component PRINT_LOG = Component
            .translatable(WanderersOfTheRift.translationId("command", "quest.log.print"));

    public QuestCommands() {
        super("quest", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        String targetArg = "target";
        builder.then(Commands.literal("log")
                .then(Commands.literal("list")
                        .executes(ctx -> printLog(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument(targetArg, EntityArgument.player())
                                .executes(ctx -> printLog(ctx, EntityArgument.getPlayer(ctx, targetArg)))))
                .then(Commands.literal("reset")
                        .executes(ctx -> resetLog(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument(targetArg, EntityArgument.player())
                                .executes(ctx -> resetLog(ctx, EntityArgument.getPlayer(ctx, targetArg))))));
        builder.then(Commands.literal("available")
                .then(Commands.literal("reset")
                        .executes(ctx -> resetAvailable(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument(targetArg, EntityArgument.player())
                                .executes(ctx -> resetAvailable(ctx, EntityArgument.getPlayer(ctx, targetArg))))));
    }

    private int resetLog(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        player.removeData(WotrAttachments.QUEST_LOG);
        ctx.getSource().sendSystemMessage(SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

    private int resetAvailable(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        player.removeData(WotrAttachments.AVAILABLE_QUESTS);
        ctx.getSource().sendSystemMessage(SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

    private int printLog(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        QuestLog data = player.getData(WotrAttachments.QUEST_LOG);
        ctx.getSource().sendSystemMessage(PRINT_LOG);
        data.getQuestCounts().forEach((quest, count) -> {
            ctx.getSource()
                    .sendSystemMessage(Component.literal(" ")
                            .append(Quest.title(quest))
                            .append(": ")
                            .append(Integer.toString(count)));
        });
        return Command.SINGLE_SUCCESS;
    }

}