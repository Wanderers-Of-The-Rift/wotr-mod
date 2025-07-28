package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Commands relating to configuring a rift key for testing
 */
public class RiftKeyCommands extends BaseCommand {

    private static final DynamicCommandExceptionType ERROR_INVALID_THEME = new DynamicCommandExceptionType(
            id -> Component.translatableEscape("commands." + WanderersOfTheRift.MODID + ".invalid_theme", id));
    private static final DynamicCommandExceptionType ERROR_INVALID_OBJECTIVE = new DynamicCommandExceptionType(
            id -> Component.translatableEscape("commands." + WanderersOfTheRift.MODID + ".invalid_objective", id));

    public RiftKeyCommands() {
        super("riftKey", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        String themeArg = "theme";
        String tierArg = "tier";
        String objectiveArg = "objective";
        String seedArg = "seed";
        builder.then(Commands.literal("tier")
                .then(Commands.argument(tierArg, IntegerArgumentType.integer(0, 7))
                        .executes(ctx -> configTier(ctx, IntegerArgumentType.getInteger(ctx, tierArg)))))
                .then(Commands.literal("theme")
                        .then(Commands.argument(themeArg, ResourceKeyArgument.key(WotrRegistries.Keys.RIFT_THEMES))
                                .executes(ctx -> configTheme(ctx,
                                        ResourceKeyArgument.resolveKey(ctx, themeArg, WotrRegistries.Keys.RIFT_THEMES,
                                                ERROR_INVALID_THEME)))))
                .then(Commands.literal("objective")
                        .then(Commands.argument(objectiveArg, ResourceKeyArgument.key(WotrRegistries.Keys.OBJECTIVES))
                                .executes(ctx -> configObjective(ctx,
                                        ResourceKeyArgument.resolveKey(ctx, objectiveArg,
                                                WotrRegistries.Keys.OBJECTIVES, ERROR_INVALID_OBJECTIVE)))))
                .then(Commands.literal("seed")
                        .then(Commands.argument(seedArg, IntegerArgumentType.integer())
                                .executes(ctx -> configSeed(ctx, IntegerArgumentType.getInteger(ctx, seedArg)))));
    }

    private int configTier(CommandContext<CommandSourceStack> context, int tier) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        RiftConfig config = key.getOrDefault(WotrDataComponentType.RIFT_CONFIG, new RiftConfig(tier));
        key.set(WotrDataComponentType.RIFT_CONFIG, config.withTier(tier));
        context.getSource()
                .sendSuccess(() -> Component
                        .translatable(WanderersOfTheRift.translationId("command", "rift_key.set_tier"), tier), true);
        return 1;
    }

    private int configTheme(CommandContext<CommandSourceStack> context, Holder<RiftTheme> theme) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        RiftConfig config = key.getOrDefault(WotrDataComponentType.RIFT_CONFIG, new RiftConfig(0));
        key.set(WotrDataComponentType.RIFT_CONFIG, config.withTheme(theme));
        context.getSource()
                .sendSuccess(
                        () -> Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.set_theme"),
                                theme.getRegisteredName()),
                        true);
        return 1;
    }

    private int configObjective(CommandContext<CommandSourceStack> context, Holder<ObjectiveType> objective) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        RiftConfig config = key.getOrDefault(WotrDataComponentType.RIFT_CONFIG, new RiftConfig(0));
        key.set(WotrDataComponentType.RIFT_CONFIG, config.withObjective(objective));
        context.getSource()
                .sendSuccess(() -> Component.translatable(
                        WanderersOfTheRift.translationId("command", "rift_key.set_objective"),
                        objective.getRegisteredName()), true);
        return 1;
    }

    private int configSeed(CommandContext<CommandSourceStack> context, int seed) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        RiftConfig config = key.getOrDefault(WotrDataComponentType.RIFT_CONFIG, new RiftConfig(0));
        key.set(WotrDataComponentType.RIFT_CONFIG, config.withRiftGenerationConfig(config.riftGen().withSeed(seed)));
        context.getSource()
                .sendSuccess(() -> Component
                        .translatable(WanderersOfTheRift.translationId("command", "rift_key.set_seed"), seed), true);
        return 1;
    }

    private static ItemStack getRiftKey(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();

            if (heldItem.getItem() == WotrItems.RIFT_KEY.asItem()) {
                return heldItem;
            }
            context.getSource().sendFailure(Component.translatable("command.wotr.rift_key.invalid_item"));
        } else {
            context.getSource().sendFailure(Component.translatable("command.wotr.invalid_player"));
        }
        return ItemStack.EMPTY;
    }

}
