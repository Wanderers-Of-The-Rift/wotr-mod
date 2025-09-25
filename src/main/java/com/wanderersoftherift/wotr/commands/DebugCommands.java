package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.entity.npc.QuestGiverInteract;
import com.wanderersoftherift.wotr.gui.menu.TradingMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;

public class DebugCommands extends BaseCommand {

    public DebugCommands() {
        super("debug", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        builder.then(Commands.literal("devWorld").executes(this::devWorld));
        builder.then(Commands.literal("listItemStackComponents").executes(this::listItemStackComponents));
        builder.then(Commands.literal("openTradingMenu").executes(this::openTradingMenu));
        builder.then(
                Commands.literal("makeQuestGiver")
                        .then(Commands.argument("mob", EntityArgument.entity())
                                .executes(ctx -> makeQuestGiver(ctx, EntityArgument.getEntity(ctx, "mob")))
                                .then(Commands
                                        .argument("selection",
                                                ResourceOrTagArgument.resourceOrTag(context,
                                                        WotrRegistries.Keys.QUESTS))
                                        .executes(ctx -> makeQuestGiver(ctx, EntityArgument.getEntity(ctx, "mob"),
                                                ResourceOrTagArgument.getResourceOrTag(ctx, "selection",
                                                        WotrRegistries.Keys.QUESTS),
                                                5))
                                        .then(
                                                Commands.argument("count", IntegerArgumentType.integer(1))
                                                        .executes(ctx -> makeQuestGiver(ctx,
                                                                EntityArgument.getEntity(ctx, "mob"),
                                                                ResourceOrTagArgument.getResourceOrTag(ctx, "selection",
                                                                        WotrRegistries.Keys.QUESTS),
                                                                IntegerArgumentType.getInteger(ctx, "count")))
                                        ))));
    }

    private int makeQuestGiver(CommandContext<CommandSourceStack> context, Entity mob) {
        mob.setData(WotrAttachments.MOB_INTERACT, new QuestGiverInteract(Optional.empty(), 5));
        return 1;
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
        return 1;
    }

    private int openTradingMenu(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getPlayer() != null) {
            context.getSource()
                    .getPlayer()
                    .openMenu(new SimpleMenuProvider(
                            ((containerId, playerInventory, player) -> new TradingMenu(containerId, playerInventory,
                                    ContainerLevelAccess.create(player.level(), player.getOnPos()),
                                    context.getSource().getPlayer().getData(WotrAttachments.WALLET))),
                            Component.translatable(WanderersOfTheRift.translationId("container", "trading"), "Guild")));
            return 1;
        }
        return 0;
    }

    private int devWorld(CommandContext<CommandSourceStack> stack) {
        MinecraftServer server = stack.getSource().getServer();
        GameRules gameRules = Objects.requireNonNull(stack.getSource().getServer().getLevel(Level.OVERWORLD))
                .getGameRules();

        gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, server);
        gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, server);
        gameRules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, server);
        gameRules.getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, server);
        gameRules.getRule(GameRules.RULE_DO_VINES_SPREAD).set(false, server);
        gameRules.getRule(GameRules.RULE_DOFIRETICK).set(false, server);

        stack.getSource()
                .sendSuccess(() -> Component.translatable("command." + WanderersOfTheRift.MODID + ".dev_world_set",
                        "Daylight Cycle", "Weather Cycle", "Mob Spawning", "Wandering Trader Spawning", "Vine Growth",
                        "Fire Tick"), true);

        return 1;
    }

    private int listItemStackComponents(CommandContext<CommandSourceStack> stack) {
        ServerPlayer player = stack.getSource().getPlayer();

        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();

            if (heldItem.isEmpty()) {
                stack.getSource()
                        .sendFailure(Component.translatable("command." + WanderersOfTheRift.MODID + ".invalid_item"));
                return 0;
            }

            stack.getSource()
                    .sendSuccess(
                            () -> Component.translatable(
                                    "command." + WanderersOfTheRift.MODID + ".get_item_stack_components.success",
                                    heldItem.getDisplayName().getString())
                                    .withStyle(ChatFormatting.YELLOW)
                                    .withStyle(ChatFormatting.UNDERLINE),
                            false);
            for (TypedDataComponent<?> c : heldItem.getComponents()) {
                player.sendSystemMessage(Component.literal("- " + c.toString()));
            }

            return 1;
        }

        stack.getSource()
                .sendFailure(Component.translatable("command." + WanderersOfTheRift.MODID + ".invalid_player"));
        return 0;
    }
}
