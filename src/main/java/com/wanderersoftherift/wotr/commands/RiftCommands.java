package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.core.rift.RiftParameterData;
import com.wanderersoftherift.wotr.core.rift.RiftParameterInstance;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameter;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import oshi.util.tuples.Pair;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Commands relating to the rift level for testing
 */
public class RiftCommands extends BaseCommand {
    private static final DynamicCommandExceptionType ERROR_INVALID_RIFT_PARAMETER = new DynamicCommandExceptionType(
            id -> Component.translatableEscape("command." + WanderersOfTheRift.MODID + ".invalid_rift_parameter", id));

    public RiftCommands() {
        super("rift", Commands.LEVEL_GAMEMASTERS);
    }

    @Override
    protected void buildCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        var parameterName = "parameterArg";
        builder.then(Commands.literal("exit").executes(this::exitRift))
                .then(Commands.literal("parameter")
                        .then(Commands
                                .argument(parameterName,
                                        ResourceKeyArgument.key(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS))
                                .executes(ctx -> getParameter(ctx, ResourceKeyArgument.resolveKey(ctx, parameterName,
                                        WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, ERROR_INVALID_RIFT_PARAMETER)))
                                .then(buildParameterComponent("base", RiftParameterInstance::getBase,
                                        new Pair<>("set", (param, oldValue, cmdValue) -> param.setBase(cmdValue)),
                                        new Pair<>("add", (param, oldValue, cmdValue) -> param.addBase(cmdValue))))
                                .then(buildParameterComponent("accumulated_multiplier",
                                        RiftParameterInstance::getAccumulatedMultiplier,
                                        new Pair<>("set",
                                                (param, oldValue, cmdValue) -> param
                                                        .setAccumulatedMultiplier(cmdValue)),
                                        new Pair<>("add",
                                                (param, oldValue, cmdValue) -> param
                                                        .addAccumulatedMultiplier(cmdValue))))
                                .then(buildParameterComponent("total_multiplier",
                                        RiftParameterInstance::getTotalMultiplier,
                                        new Pair<>("set",
                                                (param, oldValue, cmdValue) -> param.setTotalMultiplier(cmdValue)),
                                        new Pair<>("multiply",
                                                (param, oldValue, cmdValue) -> param.multiplyTotal(cmdValue))))));
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
                        getter, (parameterInstance, oldValue) -> {
                        }, "command." + WanderersOfTheRift.MODID + ".rift_parameter.get"));
        for (var updater : updaters) {
            node = node.then(Commands.literal(updater.getA())
                    .then(Commands.argument(value, DoubleArgumentType.doubleArg())
                            .executes(ctx -> updateParameter(
                                    ctx,
                                    ResourceKeyArgument.resolveKey(ctx, parameterName,
                                            WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, ERROR_INVALID_RIFT_PARAMETER),
                                    getter,
                                    (param, oldValue) -> updater.getB()
                                            .set(param, oldValue, DoubleArgumentType.getDouble(ctx, value)),
                                    "command." + WanderersOfTheRift.MODID + ".rift_parameter.set"
                            ))));
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

        var parameter = parameterData.getParameter(riftParameterReference.key().location());
        var oldValue = getter.apply(parameter);
        setter.accept(parameter, oldValue);
        var newValue = getter.apply(parameter);

        ctx.getSource().sendSuccess(() -> Component.translatable(messageKey, oldValue, newValue), true);
        return 1;
    }

    private int getParameter(
            CommandContext<CommandSourceStack> ctx,
            Holder.Reference<RiftParameter> riftParameterReference) {
        var level = ctx.getSource().getLevel();
        var parameterData = RiftParameterData.forLevel(level);
        ctx.getSource()
                .sendSuccess(() -> Component.translatable("command." + WanderersOfTheRift.MODID + ".rift_parameter.get",
                        parameterData.getParameter(riftParameterReference.key().location()).getBase()), true);
        return 1;
    }

    private int exitRift(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        ServerLevel level = ctx.getSource().getLevel();

        if (player != null && RiftLevelManager.isRift(level)) {
            RiftLevelManager.returnPlayerFromRift(player);
            return 1;
        }

        return 0;
    }
}
