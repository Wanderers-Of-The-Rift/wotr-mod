package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.QuestLog;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Commands relating to quests
 */
public class QuestCommands extends BaseCommand {

    public QuestCommands() {
        super("quest", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        builder.then(Commands.literal("log").executes(ctx -> printLog(ctx))
        );
    }

    private int printLog(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        QuestLog data = player.getData(WotrAttachments.QUEST_LOG);
        data.getQuestCounts().forEach((quest, count) -> {
            player.sendSystemMessage(
                    Component.empty().append(Quest.description(quest)).append(": ").append(Integer.toString(count)));
        });
        return Command.SINGLE_SUCCESS;
    }

}