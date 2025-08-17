package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.player.PrimaryStatistic;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public class StatCommands extends BaseCommand {

    private static final DynamicCommandExceptionType ERROR_INVALID_STAT = new DynamicCommandExceptionType(
            templateId -> Component.translatableEscape(WanderersOfTheRift.translationId("command", "stats.invalid"),
                    templateId));

    public StatCommands() {
        super("stats", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        String statArg = "stat";
        String amountArg = "amount";
        builder.then(Commands.literal("show")
                .then(Commands.argument(statArg, ResourceKeyArgument.key(WotrRegistries.Keys.PRIMARY_STATISTICS))
                        .executes((ctx) -> show(ctx, ResourceKeyArgument.resolveKey(ctx, statArg,
                                WotrRegistries.Keys.PRIMARY_STATISTICS, ERROR_INVALID_STAT)))));
        builder.then(Commands.literal("set")
                .then(Commands.argument(statArg, ResourceKeyArgument.key(WotrRegistries.Keys.PRIMARY_STATISTICS))
                        .then(Commands.argument(amountArg, IntegerArgumentType.integer(0))
                                .executes((ctx) -> set(
                                        ctx,
                                        ResourceKeyArgument.resolveKey(ctx, statArg,
                                                WotrRegistries.Keys.PRIMARY_STATISTICS, ERROR_INVALID_STAT),
                                        IntegerArgumentType.getInteger(ctx, amountArg))))));
    }

    private int show(CommandContext<CommandSourceStack> context, Holder<PrimaryStatistic> stat) {
        int value = (int) context.getSource().getPlayer().getAttribute(stat.value().attribute()).getValue();
        context.getSource()
                .getPlayer()
                .sendSystemMessage(
                        Component.translatable(WanderersOfTheRift.translationId("command", "show_attribute"),
                                PrimaryStatistic.displayName(stat), value));
        return 1;
    }

    private int set(CommandContext<CommandSourceStack> context, Holder<PrimaryStatistic> stat, int amount) {
        context.getSource().getPlayer().getData(WotrAttachments.BASE_STATISTICS).setStatistic(stat, amount);
        context.getSource()
                .getPlayer()
                .sendSystemMessage(
                        Component.translatable(WanderersOfTheRift.translationId("command", "set_attribute"),
                                PrimaryStatistic.displayName(stat), amount));
        return 1;
    }

}
