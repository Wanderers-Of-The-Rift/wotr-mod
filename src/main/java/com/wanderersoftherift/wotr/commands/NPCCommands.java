package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.entity.npc.MerchantInteract;
import com.wanderersoftherift.wotr.entity.npc.QuestGiverInteract;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceOrIdArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;

public class NPCCommands extends BaseCommand {

    public NPCCommands() {
        super("npc", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        builder.then(
                Commands.literal("make")
                        .then(Commands.argument("mob", EntityArgument.entity())
                                .then(Commands.literal("questGiver")
                                        .executes(ctx -> makeQuestGiver(ctx, EntityArgument.getEntity(ctx, "mob")))
                                        .then(Commands
                                                .argument("selection",
                                                        ResourceOrTagArgument.resourceOrTag(context,
                                                                WotrRegistries.Keys.QUESTS))
                                                .executes(
                                                        ctx -> makeQuestGiver(ctx, EntityArgument.getEntity(ctx, "mob"),
                                                                ResourceOrTagArgument.getResourceOrTag(ctx, "selection",
                                                                        WotrRegistries.Keys.QUESTS),
                                                                5))
                                                .then(
                                                        Commands.argument("count", IntegerArgumentType.integer(1))
                                                                .executes(ctx -> makeQuestGiver(ctx,
                                                                        EntityArgument.getEntity(ctx, "mob"),
                                                                        ResourceOrTagArgument.getResourceOrTag(ctx,
                                                                                "selection",
                                                                                WotrRegistries.Keys.QUESTS),
                                                                        IntegerArgumentType.getInteger(ctx, "count")))
                                                )
                                        )
                                )
                                .then(Commands.literal("merchant")
                                        .then(Commands
                                                .argument("trades",
                                                        ResourceOrIdArgument.LootTableArgument.lootTable(context))
                                                .suggests(LootCommand.SUGGEST_LOOT_TABLE)
                                                .executes(
                                                        ctx -> makeMerchant(ctx, EntityArgument.getEntity(ctx, "mob"),
                                                                ResourceOrIdArgument.LootTableArgument.getLootTable(ctx,
                                                                        "trades"))
                                                )
                                        )
                                )
                        )
        );
    }

    private int makeMerchant(CommandContext<CommandSourceStack> ctx, Entity mob, Holder<LootTable> trades) {
        mob.setData(WotrAttachments.MOB_INTERACT, new MerchantInteract(trades.getKey()));
        return Command.SINGLE_SUCCESS;
    }

    private int makeQuestGiver(CommandContext<CommandSourceStack> context, Entity mob) {
        mob.setData(WotrAttachments.MOB_INTERACT, new QuestGiverInteract(Optional.empty(), 5));
        return Command.SINGLE_SUCCESS;
    }

    private int makeQuestGiver(
            CommandContext<CommandSourceStack> context,
            Entity mob,
            ResourceOrTagArgument.Result<Quest> quests,
            int count) {
        quests.unwrap()
                .ifLeft(questReference -> mob.setData(WotrAttachments.MOB_INTERACT,
                        new QuestGiverInteract(Optional.of(HolderSet.direct(questReference)), count))
                )
                .ifRight(
                        holders -> mob.setData(WotrAttachments.MOB_INTERACT,
                                new QuestGiverInteract(Optional.of(holders), count))
                );
        return Command.SINGLE_SUCCESS;
    }
}
