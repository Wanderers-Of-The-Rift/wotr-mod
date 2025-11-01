package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.core.guild.GuildStatus;
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
public class GuildCommands extends BaseCommand {

    private static final DynamicCommandExceptionType ERROR_INVALID_GUILD = new DynamicCommandExceptionType(
            templateId -> Component.translatableEscape(WanderersOfTheRift.translationId("commands", "guild.invalid"),
                    templateId));

    public GuildCommands() {
        super("guild", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        String guildArg = "guild";
        String amountArg = "amount";
        String targetArg = "target";
        builder.then(Commands.literal("setReputation")
                .then(Commands.argument(targetArg, EntityArgument.entity())
                        .then(Commands.argument(guildArg, ResourceKeyArgument.key(WotrRegistries.Keys.GUILDS))
                                .then(Commands.argument(amountArg, IntegerArgumentType.integer(0))
                                        .executes(
                                                ctx -> setReputation(
                                                        ctx, EntityArgument.getEntity(ctx, targetArg),
                                                        ResourceKeyArgument.resolveKey(ctx, guildArg,
                                                                WotrRegistries.Keys.GUILDS, ERROR_INVALID_GUILD),
                                                        IntegerArgumentType.getInteger(ctx, amountArg))
                                        )))));
        builder.then(Commands.literal("setRank")
                .then(Commands.argument(targetArg, EntityArgument.entity())
                        .then(Commands.argument(guildArg, ResourceKeyArgument.key(WotrRegistries.Keys.GUILDS))
                                .then(Commands.argument(amountArg, IntegerArgumentType.integer(0))
                                        .executes(
                                                ctx -> setRank(
                                                        ctx, EntityArgument.getEntity(ctx, targetArg),
                                                        ResourceKeyArgument.resolveKey(ctx, guildArg,
                                                                WotrRegistries.Keys.GUILDS, ERROR_INVALID_GUILD),
                                                        IntegerArgumentType.getInteger(ctx, amountArg))
                                        )))));
    }

    private int setReputation(CommandContext<CommandSourceStack> ctx, Entity target, Holder<Guild> guild, int amount) {
        GuildStatus status = target.getData(WotrAttachments.GUILD_STATUS);
        status.setReputation(guild, amount);
        return Command.SINGLE_SUCCESS;
    }

    private int setRank(CommandContext<CommandSourceStack> ctx, Entity target, Holder<Guild> guild, int amount) {
        GuildStatus status = target.getData(WotrAttachments.GUILD_STATUS);
        status.setRank(guild, amount);
        return Command.SINGLE_SUCCESS;
    }
}
