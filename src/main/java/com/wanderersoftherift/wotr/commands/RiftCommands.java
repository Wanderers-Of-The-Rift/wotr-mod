package com.wanderersoftherift.wotr.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterData;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterInstance;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import oshi.util.tuples.Pair;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Commands relating to the rift level for testing
 */
public class RiftCommands extends BaseCommand {
    static final DynamicCommandExceptionType ERROR_INVALID_RIFT_PARAMETER = new DynamicCommandExceptionType(
            id -> Component.translatableEscape("command." + WanderersOfTheRift.MODID + ".invalid_rift_parameter", id));
    private static final BiConsumer<RiftParameterInstance, Double> NOOP_SETTER = (parameterInstance, oldValue) -> {
    };
    private static final String PARAMETER_SET_TRANSLATION_KEY = "command." + WanderersOfTheRift.MODID
            + ".rift_parameter.set";
    private static final String PARAMETER_GET_TRANSLATION_KEY = "command." + WanderersOfTheRift.MODID
            + ".rift_parameter.get";
    private static final String PARAMETER_MISSING_TRANSLATION_KEY = "command." + WanderersOfTheRift.MODID
            + ".rift_parameter.missing";
    private static final Component VOID_ROOM = Component
            .translatable(WanderersOfTheRift.translationId("command", "rift.roominfo.void"));
    private static final Component INVALID_LEVEL = Component
            .translatable(WanderersOfTheRift.translationId("command", "rift.roominfo.invalid"));

    public RiftCommands() {
        super("rift", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        String parameterNameArg = "parameter";
        String playersArg = "players";
        String playerArg = "player";
        builder.then(Commands.literal("exit")
                .executes(ctx -> exitRift(ctx, List.of(ctx.getSource().getPlayerOrException())))
                .then(
                        Commands.argument(playersArg, EntityArgument.players())
                                .executes(ctx -> exitRift(ctx, EntityArgument.getPlayers(ctx, playersArg)))));
        builder.then(Commands.literal("parameter")
                .then(Commands
                        .argument(parameterNameArg, ResourceKeyArgument.key(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS))
                        .executes(ctx -> getParameter(ctx,
                                ResourceKeyArgument.resolveKey(ctx, parameterNameArg,
                                        WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, ERROR_INVALID_RIFT_PARAMETER)))
                        .then(buildParameterComponent("base", RiftParameterInstance::getBase,
                                new Pair<>("set", (param, oldValue, cmdValue) -> param.setBase(cmdValue)),
                                new Pair<>("add", (param, oldValue, cmdValue) -> param.addBase(cmdValue))))
                        .then(buildParameterComponent("accumulated_multiplier",
                                RiftParameterInstance::getAccumulatedMultiplier,
                                new Pair<>("set",
                                        (param, oldValue, cmdValue) -> param.setAccumulatedMultiplier(cmdValue)),
                                new Pair<>("add",
                                        (param, oldValue, cmdValue) -> param.addAccumulatedMultiplier(cmdValue))))
                        .then(buildParameterComponent("total_multiplier", RiftParameterInstance::getTotalMultiplier,
                                new Pair<>("set", (param, oldValue, cmdValue) -> param.setTotalMultiplier(cmdValue)),
                                new Pair<>("multiply",
                                        (param, oldValue, cmdValue) -> param.multiplyTotal(cmdValue))))));
        builder.then(Commands.literal("list").executes(this::listRifts));
        builder.then(Commands.literal("close").then(Commands.literal("all").executes(this::closeRifts)));
        builder.then(
                Commands.literal("room")
                        .then(Commands.literal("info")
                                .executes(ctx -> printRoomInfo(ctx, ctx.getSource().getPlayerOrException()))
                                .then(
                                        Commands.argument(playerArg, EntityArgument.player())
                                                .executes(ctx -> printRoomInfo(ctx,
                                                        EntityArgument.getPlayer(ctx, playerArg)))
                                ))
                        .then(Commands.literal("anomalyCount")
                                .executes(ctx -> printRoomAnomalyCount(ctx, ctx.getSource().getPlayerOrException()))
                                .then(Commands.argument(playerArg, EntityArgument.player())
                                        .executes(ctx -> printRoomAnomalyCount(ctx,
                                                EntityArgument.getPlayer(ctx, playerArg))))));
    }

    private int printRoomAnomalyCount(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        if (RiftLevelManager.isRift(player.serverLevel()) && player.serverLevel()
                .getChunkSource()
                .getGenerator() instanceof FastRiftGenerator fastRiftGenerator) {
            RiftSpace chunkSpace = fastRiftGenerator.getOrCreateLayout(player.server)
                    .getChunkSpace(SectionPos.of(player.blockPosition()));
            if (chunkSpace instanceof RoomRiftSpace room) {
                int result = fastRiftGenerator.getJigsawCounts(room, ctx.getSource().getLevel())
                        .object2IntEntrySet()
                        .stream()
                        .filter(entry -> entry.getKey().startsWith("wotr:rift/anomaly/"))
                        .mapToInt(Object2IntMap.Entry::getIntValue)
                        .sum();
                ctx.getSource().sendSystemMessage(Component.literal(Integer.toString(result)));
            } else {
                ctx.getSource().sendSystemMessage(Component.literal("0"));
            }
            return Command.SINGLE_SUCCESS;
        }
        ctx.getSource().sendSystemMessage(INVALID_LEVEL);
        return 0;
    }

    private int printRoomInfo(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        if (RiftLevelManager.isRift(player.serverLevel()) && player.serverLevel()
                .getChunkSource()
                .getGenerator() instanceof FastRiftGenerator fastRiftGenerator) {
            RiftSpace chunkSpace = fastRiftGenerator.getOrCreateLayout(player.server)
                    .getChunkSpace(SectionPos.of(player.blockPosition()));
            if (chunkSpace instanceof VoidRiftSpace) {
                ctx.getSource().sendSystemMessage(VOID_ROOM);
            } else {
                ctx.getSource()
                        .sendSystemMessage(Component.translatable(
                                WanderersOfTheRift.translationId("command", "rift.roominfo.origin"),
                                chunkSpace.origin().getX(), chunkSpace.origin().getY(), chunkSpace.origin().getZ()));
                ctx.getSource()
                        .sendSystemMessage(Component.translatable(
                                WanderersOfTheRift.translationId("command", "rift.roominfo.size"),
                                chunkSpace.size().getX(), chunkSpace.size().getY(), chunkSpace.size().getZ()));
                ctx.getSource()
                        .sendSystemMessage(Component.translatable(
                                WanderersOfTheRift.translationId("command", "rift.roominfo.transform"),
                                chunkSpace.templateTransform().x(), chunkSpace.templateTransform().z(),
                                chunkSpace.templateTransform().diagonal()));
                ctx.getSource()
                        .sendSystemMessage(Component.translatable(
                                WanderersOfTheRift.translationId("command", "rift.roominfo.room"),
                                chunkSpace.template().identifier()));
            }
            return Command.SINGLE_SUCCESS;
        }
        ctx.getSource().sendSystemMessage(INVALID_LEVEL);
        return 0;
    }

    private int closeRifts(CommandContext<CommandSourceStack> context) {
        for (ServerLevel level : ImmutableList.copyOf(context.getSource().getLevel().getServer().getAllLevels())) {
            if (RiftLevelManager.isRift(level)) {
                RiftLevelManager.forceClose(level);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private int listRifts(CommandContext<CommandSourceStack> context) {
        int count = 0;
        for (ServerLevel level : context.getSource().getLevel().getServer().getAllLevels()) {
            if (RiftLevelManager.isRift(level)) {
                count++;
                context.getSource().sendSystemMessage(Component.literal(level.dimension().location().toString()));
            }
        }
        context.getSource()
                .sendSystemMessage(Component.translatable(WanderersOfTheRift.translationId("command", "total"), count));
        return Command.SINGLE_SUCCESS;
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildParameterComponent(
            String name,
            Function<RiftParameterInstance, Double> getter,
            Pair<String, ParamComponentSetter>... updaters) {
        var parameterName = "parameterArg";
        var value = "valueArg";

        var node = Commands.literal(name)
                .executes(ctx -> updateParameter(
                        ctx, ResourceKeyArgument.resolveKey(ctx, parameterName,
                                WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, ERROR_INVALID_RIFT_PARAMETER),
                        getter, NOOP_SETTER, PARAMETER_GET_TRANSLATION_KEY));
        for (var updater : updaters) {
            node = node.then(Commands.literal(updater.getA())
                    .then(Commands.argument(value, DoubleArgumentType.doubleArg())
                            .executes(ctx -> updateParameter(ctx,
                                    ResourceKeyArgument.resolveKey(ctx, parameterName,
                                            WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, ERROR_INVALID_RIFT_PARAMETER),
                                    getter,
                                    (param, oldValue) -> updater.getB()
                                            .set(param, oldValue, DoubleArgumentType.getDouble(ctx, value)),
                                    PARAMETER_SET_TRANSLATION_KEY))));
        }
        return node;
    }

    interface ParamComponentSetter {
        void set(RiftParameterInstance parameter, double oldValue, double commandValue);
    }

    private int updateParameter(
            CommandContext<CommandSourceStack> ctx,
            Holder.Reference<RiftParameter> riftParameterReference,
            Function<RiftParameterInstance, Double> getter,
            BiConsumer<RiftParameterInstance, Double> setter,
            String messageKey) {
        var level = ctx.getSource().getLevel();
        var parameterData = RiftParameterData.forLevel(level);

        var parameter = parameterData.getParameter(riftParameterReference.key());
        if (parameter == null) {
            ctx.getSource().sendSuccess(() -> Component.translatable(PARAMETER_MISSING_TRANSLATION_KEY), true);
            return 0;
        }
        var oldValue = getter.apply(parameter);
        setter.accept(parameter, oldValue);
        var newValue = getter.apply(parameter);

        ctx.getSource().sendSuccess(() -> Component.translatable(messageKey, oldValue, newValue), true);
        return 1;
    }

    private int getParameter(
            CommandContext<CommandSourceStack> ctx,
            Holder.Reference<RiftParameter> riftParameterReference) {
        return updateParameter(ctx, riftParameterReference, RiftParameterInstance::get, NOOP_SETTER,
                PARAMETER_GET_TRANSLATION_KEY);
    }

    private int exitRift(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players) {
        int result = 0;
        for (ServerPlayer player : players) {
            if (RiftLevelManager.isRift(player.serverLevel())) {
                RiftLevelManager.returnPlayerFromRift(player);
                result++;
            }
        }
        return result;
    }
}
