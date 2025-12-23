package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTrack;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTracker;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

/**
 * Commands related to guilds and guild status
 */
public class ProgressionTrackCommands extends BaseCommand {

    private static final DynamicCommandExceptionType ERROR_INVALID_TRACK = new DynamicCommandExceptionType(
            templateId -> Component.translatableEscape(WanderersOfTheRift.translationId("command", "track.invalid"),
                    templateId));

    public ProgressionTrackCommands() {
        super("progression", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        String trackArg = "track";
        String amountArg = "amount";
        String targetArg = "target";
        builder.then(Commands.literal("setPoints")
                .then(Commands.argument(targetArg, EntityArgument.entity())
                        .then(Commands
                                .argument(trackArg, ResourceKeyArgument.key(WotrRegistries.Keys.PROGRESSION_TRACKS))
                                .then(Commands.argument(amountArg, IntegerArgumentType.integer(0))
                                        .executes(
                                                ctx -> setPoints(
                                                        ctx, EntityArgument.getEntity(ctx, targetArg),
                                                        ResourceKeyArgument.resolveKey(ctx, trackArg,
                                                                WotrRegistries.Keys.PROGRESSION_TRACKS,
                                                                ERROR_INVALID_TRACK),
                                                        IntegerArgumentType.getInteger(ctx, amountArg))
                                        )))));
        builder.then(Commands.literal("setRank")
                .then(Commands.argument(targetArg, EntityArgument.entity())
                        .then(Commands
                                .argument(trackArg, ResourceKeyArgument.key(WotrRegistries.Keys.PROGRESSION_TRACKS))
                                .then(Commands.argument(amountArg, IntegerArgumentType.integer(0))
                                        .executes(
                                                ctx -> setRank(
                                                        ctx, EntityArgument.getEntity(ctx, targetArg),
                                                        ResourceKeyArgument.resolveKey(ctx, trackArg,
                                                                WotrRegistries.Keys.PROGRESSION_TRACKS,
                                                                ERROR_INVALID_TRACK),
                                                        IntegerArgumentType.getInteger(ctx, amountArg))
                                        )))));
    }

    private int setPoints(
            CommandContext<CommandSourceStack> ctx,
            Entity target,
            Holder<ProgressionTrack> track,
            int amount) {
        ProgressionTracker status = target.getData(WotrAttachments.PROGRESSION_TRACKER);
        status.setPoints(track, amount);
        return Command.SINGLE_SUCCESS;
    }

    private int setRank(
            CommandContext<CommandSourceStack> ctx,
            Entity target,
            Holder<ProgressionTrack> track,
            int amount) {
        ProgressionTracker status = target.getData(WotrAttachments.PROGRESSION_TRACKER);
        status.setRank(track, amount);
        return Command.SINGLE_SUCCESS;
    }
}
