package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftGenerationConfig;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrRegistries;
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
    private static final DynamicCommandExceptionType ERROR_INVALID_GENERATOR_PRESET = new DynamicCommandExceptionType(
            id -> Component.translatableEscape("commands." + WanderersOfTheRift.MODID + ".invalid_generator_preset",
                    id));

    public RiftKeyCommands() {
        super("riftKey", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        String themeArg = "theme";
        String tierArg = "tier";
        String objectiveArg = "objective";
        String seedArg = "seed";
        String generatorArg = "generator";
        builder.then(Commands.literal("tier")
                .then(Commands.argument(tierArg, IntegerArgumentType.integer(0))
                        .executes(ctx -> configTier(ctx, IntegerArgumentType.getInteger(ctx, tierArg)))))
                .then(Commands.literal("theme")
                        .then(Commands.argument(themeArg, ResourceKeyArgument.key(WotrRegistries.Keys.RIFT_THEMES))
                                .executes(ctx -> configTheme(ctx,
                                        ResourceKeyArgument.resolveKey(ctx, themeArg, WotrRegistries.Keys.RIFT_THEMES,
                                                ERROR_INVALID_THEME))))
                        .then(Commands.literal("random").executes(ctx -> configTheme(ctx, null))))
                .then(Commands.literal("objective")
                        .then(Commands.argument(objectiveArg, ResourceKeyArgument.key(WotrRegistries.Keys.OBJECTIVES))
                                .executes(ctx -> configObjective(ctx,
                                        ResourceKeyArgument.resolveKey(ctx, objectiveArg,
                                                WotrRegistries.Keys.OBJECTIVES, ERROR_INVALID_OBJECTIVE))))
                        .then(Commands.literal("random").executes(ctx -> configObjective(ctx, null))))
                .then(Commands.literal("seed")
                        .then(Commands.argument(seedArg, LongArgumentType.longArg())
                                .executes(ctx -> configSeed(ctx, LongArgumentType.getLong(ctx, seedArg))))
                        .then(Commands.literal("random").executes(ctx -> configSeed(ctx, null))))
                .then(Commands.literal("generator")
                        .then(Commands
                                .argument(generatorArg, ResourceKeyArgument.key(WotrRegistries.Keys.GENERATOR_PRESETS))
                                .executes(ctx -> configGeneratorPreset(ctx,
                                        ResourceKeyArgument.resolveKey(ctx, generatorArg,
                                                WotrRegistries.Keys.GENERATOR_PRESETS,
                                                ERROR_INVALID_GENERATOR_PRESET)))));
    }

    private int configGeneratorPreset(
            CommandContext<CommandSourceStack> context,
            Holder.Reference<RiftGenerationConfig> riftGenerationConfigReference) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        key.set(WotrDataComponentType.RiftConfig.GENERATOR_PRESET, riftGenerationConfigReference);

        context.getSource()
                .sendSuccess(
                        () -> Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.set_theme"),
                                riftGenerationConfigReference.getRegisteredName()),
                        true);
        return 1;
    }

    private int configTier(CommandContext<CommandSourceStack> context, int tier) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        key.set(WotrDataComponentType.RiftConfig.ITEM_RIFT_TIER, tier);
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
        if (theme == null) {
            key.remove(WotrDataComponentType.RiftConfig.RIFT_THEME);
        } else {
            key.set(WotrDataComponentType.RiftConfig.RIFT_THEME, theme);
        }
        context.getSource()
                .sendSuccess(
                        () -> Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.set_theme"),
                                theme != null ? theme.getRegisteredName() : "random"),
                        true);
        return 1;
    }

    private int configObjective(CommandContext<CommandSourceStack> context, Holder<ObjectiveType> objective) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        if (objective == null) {
            key.remove(WotrDataComponentType.RiftConfig.RIFT_OBJECTIVE);
        } else {
            key.set(WotrDataComponentType.RiftConfig.RIFT_OBJECTIVE, objective);
        }
        context.getSource()
                .sendSuccess(() -> Component.translatable(
                        WanderersOfTheRift.translationId("command", "rift_key.set_objective"),
                        (objective != null ? objective.getRegisteredName() : "random")), true);
        return 1;
    }

    private int configSeed(CommandContext<CommandSourceStack> context, Long seed) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        if (seed == null) {
            key.remove(WotrDataComponentType.RiftConfig.RIFT_SEED);
        } else {
            key.set(WotrDataComponentType.RiftConfig.RIFT_SEED, seed);
        }
        context.getSource()
                .sendSuccess(
                        () -> Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.set_seed"),
                                (seed != null ? seed : "random")),
                        true);
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
