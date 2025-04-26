package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.init.ModRiftThemes;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class RiftKeyCommands extends BaseCommand {

    private static final DynamicCommandExceptionType ERROR_INVALID_THEME = new DynamicCommandExceptionType(
            id -> Component.translatableEscape("commands." + WanderersOfTheRift.MODID + ".invalid_theme", id));

    public RiftKeyCommands() {
        super("riftKey", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        String themeArg = "theme";
        String tierArg = "tier";
        String seedArg = "seed";
        builder.then(Commands.literal("tier")
                .then(Commands.argument(tierArg, IntegerArgumentType.integer(0, 7))
                        .executes(ctx -> configKey(ctx, IntegerArgumentType.getInteger(ctx, tierArg), null, null))
                        .then(Commands.literal("theme")
                                .then(Commands.argument(themeArg, ResourceKeyArgument.key(ModRiftThemes.RIFT_THEME_KEY))
                                        .executes(ctx -> configKey(ctx, IntegerArgumentType.getInteger(ctx, tierArg),
                                                ResourceKeyArgument.resolveKey(ctx, themeArg,
                                                        ModRiftThemes.RIFT_THEME_KEY, ERROR_INVALID_THEME),
                                                null))
                                        .then(Commands.literal("seed")
                                                .then(Commands.argument(seedArg, IntegerArgumentType.integer())
                                                        .executes(ctx -> configKey(ctx,
                                                                IntegerArgumentType.getInteger(ctx, tierArg),
                                                                ResourceKeyArgument.resolveKey(ctx, themeArg,
                                                                        ModRiftThemes.RIFT_THEME_KEY,
                                                                        ERROR_INVALID_THEME),
                                                                IntegerArgumentType.getInteger(ctx, seedArg)))))))));
    }

    private int configKey(CommandContext<CommandSourceStack> context, int tier, Holder<RiftTheme> theme, Integer seed) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();

            if (heldItem.getItem() != ModItems.RIFT_KEY.asItem()) {
                context.getSource().sendFailure(Component.translatable("command.wotr.rift_key.invalid_item"));
                return 0;
            }

            RiftConfig config = new RiftConfig(tier, Optional.ofNullable(theme), Optional.ofNullable(seed));

            heldItem.set(ModDataComponentType.RIFT_CONFIG, config);
            context.getSource().sendSuccess(() -> Component.translatable("command.wotr.rift_key.success"), true);
            return 1;
        }

        context.getSource().sendFailure(Component.translatable("command.wotr.invalid_player"));
        return 0;
    }

}
