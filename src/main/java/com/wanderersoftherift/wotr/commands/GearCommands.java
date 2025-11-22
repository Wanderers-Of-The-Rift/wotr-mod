package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.item.implicit.UnrolledGearImplicits;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class GearCommands extends BaseCommand {

    private static final Component INVALID_FOR_SOCKETS = Component
            .translatable(WanderersOfTheRift.translationId("command", "gear.socket.invalid"));
    private static final Component INVALID_FOR_IMPLICITS = Component
            .translatable(WanderersOfTheRift.translationId("command", "gear.implicit.invalid"));

    private static final String SOCKETS_ARG = "sockets";

    public GearCommands() {
        super("gear", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {

        builder.then(Commands.literal("reroll")
                .then(Commands.literal("all")
                        .executes(this::rerollAll)
                        .then(Commands.argument(SOCKETS_ARG, IntegerArgumentType.integer(0, 6))
                                .executes(ctx -> rerollAll(ctx, IntegerArgumentType.getInteger(ctx, SOCKETS_ARG)))))
                .then(Commands.literal("implicits").executes(this::rerollImplicits))
                .then(Commands.literal("sockets")
                        .executes(this::rerollSockets)
                        .then(Commands.argument(SOCKETS_ARG, IntegerArgumentType.integer(0, 6))
                                .executes(
                                        ctx -> rerollSockets(ctx, IntegerArgumentType.getInteger(ctx, SOCKETS_ARG))))));
    }

    private int rerollAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return rerollAll(ctx, ctx.getSource().getPlayerOrException().getRandom().nextInt(0, 6));
    }

    private int rerollAll(CommandContext<CommandSourceStack> ctx, int sockets) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack item = player.getInventory().getSelected();
        if (!rerollSockets(player, item, sockets)) {
            ctx.getSource().sendSystemMessage(INVALID_FOR_SOCKETS);
            return 0;
        }

        if (!rerollImplicits(item)) {
            ctx.getSource().sendSystemMessage(INVALID_FOR_IMPLICITS);
            return 0;
        }
        return Command.SINGLE_SUCCESS;
    }

    private int rerollSockets(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return rerollSockets(ctx, ctx.getSource().getPlayerOrException().getRandom().nextInt(0, 6));
    }

    private int rerollSockets(CommandContext<CommandSourceStack> ctx, int sockets) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack item = player.getInventory().getSelected();
        if (rerollSockets(player, item, sockets)) {
            return Command.SINGLE_SUCCESS;
        }
        ctx.getSource().sendSystemMessage(INVALID_FOR_SOCKETS);
        return 0;
    }

    private int rerollImplicits(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack item = player.getInventory().getSelected();
        if (rerollImplicits(item)) {
            return Command.SINGLE_SUCCESS;
        }
        ctx.getSource().sendSystemMessage(INVALID_FOR_IMPLICITS);
        return 0;
    }

    private static boolean rerollSockets(ServerPlayer player, ItemStack item, int sockets) {
        if (item.isEmpty()) {
            return false;
        }
        if (!item.is(WotrTags.Items.SOCKETABLE)) {
            return false;
        }
        item.set(WotrDataComponentType.GEAR_SOCKETS, GearSockets.generate(sockets, player.getRandom()));
        return true;
    }

    private static boolean rerollImplicits(ItemStack item) {
        if (item.isEmpty()) {
            return false;
        }
        item.set(WotrDataComponentType.GEAR_IMPLICITS, new UnrolledGearImplicits());
        return item.has(WotrDataComponentType.GEAR_IMPLICITS);
    }

}
