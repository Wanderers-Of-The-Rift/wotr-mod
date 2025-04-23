package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class DebugCommands extends BaseCommand {

    public DebugCommands() {
        super("debug", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        builder.then(Commands.literal("devWorld").executes(this::devWorld));
        builder.then(Commands.literal("getItemStackComponents").executes(this::getItemStackComponents));

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

    private int getItemStackComponents(CommandContext<CommandSourceStack> stack) {
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
                            () -> Component
                                    .translatable(
                                            "command." + WanderersOfTheRift.MODID
                                                    + ".get_item_stack_components.success",
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
