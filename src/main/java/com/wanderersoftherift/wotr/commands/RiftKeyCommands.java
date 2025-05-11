package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.init.ModRiftThemes;
import com.wanderersoftherift.wotr.init.RegistryEvents;
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
                        .then(Commands.argument(themeArg, ResourceKeyArgument.key(ModRiftThemes.RIFT_THEME_KEY))
                                .executes(ctx -> configTheme(ctx,
                                        ResourceKeyArgument.resolveKey(ctx, themeArg, ModRiftThemes.RIFT_THEME_KEY,
                                                ERROR_INVALID_THEME)))))
                .then(Commands.literal("objective")
                        .then(Commands
                                .argument(objectiveArg, ResourceKeyArgument.key(RegistryEvents.OBJECTIVE_REGISTRY))
                                .executes(ctx -> configObjective(ctx,
                                        ResourceKeyArgument.resolveKey(ctx, objectiveArg,
                                                RegistryEvents.OBJECTIVE_REGISTRY, ERROR_INVALID_OBJECTIVE)))))
                .then(Commands.literal("seed")
                        .then(Commands.argument(seedArg, IntegerArgumentType.integer())
                                .executes(ctx -> configSeed(ctx, IntegerArgumentType.getInteger(ctx, seedArg)))));
    }

    private int configTier(CommandContext<CommandSourceStack> context, int tier) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        RiftConfig config = key.getOrDefault(ModDataComponentType.RIFT_CONFIG, new RiftConfig(tier));
        key.set(ModDataComponentType.RIFT_CONFIG, config.modify().tier(tier).build());
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
        RiftConfig config = key.getOrDefault(ModDataComponentType.RIFT_CONFIG, new RiftConfig(0));
        key.set(ModDataComponentType.RIFT_CONFIG, config.modify().theme(theme).build());
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
        RiftConfig config = key.getOrDefault(ModDataComponentType.RIFT_CONFIG, new RiftConfig(0));
        key.set(ModDataComponentType.RIFT_CONFIG, config.modify().objective(objective).build());
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
        RiftConfig config = key.getOrDefault(ModDataComponentType.RIFT_CONFIG, new RiftConfig(0));
        key.set(ModDataComponentType.RIFT_CONFIG, config.modify().seed(seed).build());
        context.getSource()
                .sendSuccess(() -> Component
                        .translatable(WanderersOfTheRift.translationId("command", "rift_key.set_seed"), seed), true);
        return 1;
    }

    private static ItemStack getRiftKey(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();

            if (heldItem.getItem() == ModItems.RIFT_KEY.asItem()) {
                return heldItem;
            }
            context.getSource().sendFailure(Component.translatable("command.wotr.rift_key.invalid_item"));
        } else {
            context.getSource().sendFailure(Component.translatable("command.wotr.invalid_player"));
        }
        return ItemStack.EMPTY;
    }

}
