package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
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
public class MerchantCommands extends BaseCommand {

    private static final Component SUCCESS = Component
            .translatable(WanderersOfTheRift.translationId("command", "generic.success"));

    public MerchantCommands() {
        super("merchant", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        String playerArg = "player";
        String npcArg = "merchant";

        builder.then(
                Commands.literal("reset")
                        .then(Commands.literal("@all")
                                .executes(ctx -> resetAll(ctx, ctx.getSource().getPlayerOrException()))
                                .then(Commands.argument(playerArg, EntityArgument.player())
                                        .executes(ctx -> resetAll(ctx, EntityArgument.getPlayer(ctx, playerArg)))))
                        .then(Commands.argument(npcArg, ResourceArgument.resource(context, WotrRegistries.Keys.NPCS))
                                .executes(
                                        ctx -> reset(ctx,
                                                ResourceArgument.getResource(ctx, npcArg, WotrRegistries.Keys.NPCS),
                                                ctx.getSource().getPlayerOrException()))
                                .then(Commands.argument(playerArg, EntityArgument.player())
                                        .executes(ctx -> reset(ctx,
                                                ResourceArgument.getResource(ctx, npcArg, WotrRegistries.Keys.NPCS),
                                                EntityArgument.getPlayer(ctx, playerArg))))));
    }

    private int reset(CommandContext<CommandSourceStack> ctx, Holder.Reference<NpcIdentity> npc, ServerPlayer player) {
        player.getExistingData(WotrAttachments.AVAILABLE_TRADES).ifPresent(trades -> trades.clear(npc));
        ctx.getSource().sendSystemMessage(SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

    private int resetAll(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        player.removeData(WotrAttachments.AVAILABLE_TRADES);
        ctx.getSource().sendSystemMessage(SUCCESS);
        return Command.SINGLE_SUCCESS;
    }

}