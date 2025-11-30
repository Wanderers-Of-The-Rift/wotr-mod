package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.core.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.QuestLog;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
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
    private static final Component ACTIVE_QUESTS = Component
            .translatable(WanderersOfTheRift.translationId("command", "quest.active"));

    public QuestCommands() {
        super("quest", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        String playerArg = "player";
        String npcArg = "npc";
        String questArg = "quest";
        String amountArg = "amount";
        builder.then(Commands.literal("log")
                .then(Commands.literal("list")
                        .executes(ctx -> printLog(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument(playerArg, EntityArgument.player())
                                .executes(ctx -> printLog(ctx, EntityArgument.getPlayer(ctx, playerArg)))))
                .then(Commands.literal("set")
                        .then(Commands
                                .argument(questArg, ResourceArgument.resource(context, WotrRegistries.Keys.QUESTS))
                                .then(Commands.argument(amountArg, IntegerArgumentType.integer(0))
                                        .executes(ctx -> setLog(ctx,
                                                ResourceArgument.getResource(ctx, questArg, WotrRegistries.Keys.QUESTS),
                                                IntegerArgumentType.getInteger(ctx, amountArg),
                                                ctx.getSource().getPlayerOrException()))
                                        .then(Commands.argument(playerArg, EntityArgument.player())
                                                .executes(ctx -> setLog(ctx,
                                                        ResourceArgument.getResource(ctx, questArg,
                                                                WotrRegistries.Keys.QUESTS),
                                                        IntegerArgumentType.getInteger(ctx, amountArg),
                                                        EntityArgument.getPlayer(ctx, playerArg)))))))
                .then(Commands.literal("reset")
                        .executes(ctx -> resetLog(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument(playerArg, EntityArgument.player())
                                .executes(ctx -> resetLog(ctx, EntityArgument.getPlayer(ctx, playerArg))))));
        builder.then(Commands.literal("available")
                .then(Commands.literal("reset")
                        .then(Commands.literal("all")
                                .executes(ctx -> resetAllAvailable(ctx, ctx.getSource().getPlayerOrException()))
                                .then(Commands.argument(playerArg, EntityArgument.player())
                                        .executes(ctx -> resetAllAvailable(ctx,
                                                EntityArgument.getPlayer(ctx, playerArg)))))
                        .then(Commands.argument(npcArg, ResourceArgument.resource(context, WotrRegistries.Keys.NPCS))
                                .executes(ctx -> resetAvailable(ctx,
                                        ResourceArgument.getResource(ctx, npcArg, WotrRegistries.Keys.NPCS),
                                        ctx.getSource().getPlayerOrException()))
                                .then(Commands.argument(playerArg, EntityArgument.player())
                                        .executes(ctx -> resetAvailable(ctx,
                                                ResourceArgument.getResource(ctx, npcArg, WotrRegistries.Keys.NPCS),
                                                EntityArgument.getPlayer(ctx, playerArg)))))));
        builder.then(Commands.literal("active")
                .then(Commands.literal("list")
                        .executes(ctx -> listActive(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument(playerArg, EntityArgument.player())
                                .executes(ctx -> listActive(ctx, EntityArgument.getPlayer(ctx, playerArg))))
                ));
    }

    private int listActive(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS);
        ctx.getSource().sendSystemMessage(ACTIVE_QUESTS);
        activeQuests.getQuestList()
                .forEach(quest -> ctx.getSource()
                        .sendSystemMessage(Component.literal(quest.getOrigin().getRegisteredName())));
        return Command.SINGLE_SUCCESS;
    }

    private int setLog(CommandContext<CommandSourceStack> ctx, Holder<Quest> quest, int amount, ServerPlayer player) {
        QuestLog questLog = player.getData(WotrAttachments.QUEST_LOG);
        questLog.setCompletionCount(quest, amount);
        ctx.getSource().sendSystemMessage(SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

    private int resetLog(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        player.removeData(WotrAttachments.QUEST_LOG);
        ctx.getSource().sendSystemMessage(SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

    private int resetAllAvailable(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        player.removeData(WotrAttachments.AVAILABLE_QUESTS);
        ctx.getSource().sendSystemMessage(SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

    private int resetAvailable(CommandContext<CommandSourceStack> ctx, Holder<NpcIdentity> npc, ServerPlayer player) {
        player.getExistingData(WotrAttachments.AVAILABLE_QUESTS).ifPresent(quests -> quests.clearQuests(npc));
        ctx.getSource().sendSystemMessage(SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

    private int printLog(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        QuestLog data = player.getData(WotrAttachments.QUEST_LOG);
        ctx.getSource().sendSystemMessage(PRINT_LOG);
        data.getQuestCounts().forEach((quest, count) -> {
            ctx.getSource()
                    .sendSystemMessage(Component.literal(" \"")
                            .append(quest.getRegisteredName())
                            .append("\": ")
                            .append(Integer.toString(count)));
        });
        return Command.SINGLE_SUCCESS;
    }

}