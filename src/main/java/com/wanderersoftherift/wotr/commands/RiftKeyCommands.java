package com.wanderersoftherift.wotr.commands;

import com.google.common.collect.ImmutableList;
import com.google.gson.FormattingStyle;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.JsonOps;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftGenerationConfig;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.util.listedit.Append;
import com.wanderersoftherift.wotr.util.listedit.Clear;
import com.wanderersoftherift.wotr.util.listedit.Drop;
import com.wanderersoftherift.wotr.util.listedit.DropLast;
import com.wanderersoftherift.wotr.util.listedit.ListEdit;
import com.wanderersoftherift.wotr.util.listedit.Prepend;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.ChaosLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.PredefinedRoomLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.RingLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.storage.LevelResource;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;

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
    private static final DynamicCommandExceptionType ERROR_INVALID_TEMPLATE_POOL = new DynamicCommandExceptionType(
            id -> Component.translatableEscape("commands." + WanderersOfTheRift.MODID + ".invalid_template_pool", id));

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
                .then(generatorCommands());
    }

    private LiteralArgumentBuilder generatorCommands() {
        String generatorArg = "generator";
        String templatePool = "pool";
        String position = "position";
        String radius = "radius";
        String count = "count";
        String start = "start";
        String export = "exportKey";
        return Commands.literal("generator")
                .then(Commands.literal("setPreset")
                        .then(Commands
                                .argument(generatorArg, ResourceKeyArgument.key(WotrRegistries.Keys.GENERATOR_PRESETS))
                                .executes(ctx -> configGeneratorPreset(ctx,
                                        ResourceKeyArgument.resolveKey(ctx, generatorArg,
                                                WotrRegistries.Keys.GENERATOR_PRESETS,
                                                ERROR_INVALID_GENERATOR_PRESET)))))
                .then(Commands.literal("layout")
                        .then(Commands.literal("addRoom")
                                .then(Commands.argument(templatePool, ResourceKeyArgument.key(Registries.TEMPLATE_POOL))
                                        .then(Commands.argument(position, BlockPosArgument.blockPos())
                                                .then(
                                                        Commands.argument(start, BoolArgumentType.bool())
                                                                .executes(
                                                                        ctx -> configAddRoom(ctx,
                                                                                ResourceKeyArgument.resolveKey(ctx,
                                                                                        templatePool,
                                                                                        Registries.TEMPLATE_POOL,
                                                                                        ERROR_INVALID_TEMPLATE_POOL),
                                                                                BlockPosArgument.getBlockPos(ctx,
                                                                                        position),
                                                                                BoolArgumentType.getBool(ctx, start))
                                                                )
                                                )
                                                .executes(ctx -> configAddRoom(ctx,
                                                        ResourceKeyArgument.resolveKey(ctx, templatePool,
                                                                Registries.TEMPLATE_POOL, ERROR_INVALID_TEMPLATE_POOL),
                                                        BlockPosArgument.getBlockPos(ctx, position), false)))))
                        .then(Commands.literal("addRing")
                                .then(Commands.argument(templatePool, ResourceKeyArgument.key(Registries.TEMPLATE_POOL))
                                        .then(Commands.argument(radius, IntegerArgumentType.integer(0))
                                                .then(
                                                        Commands.argument(start, BoolArgumentType.bool())
                                                                .executes(
                                                                        ctx -> configAddRing(ctx,
                                                                                ResourceKeyArgument.resolveKey(ctx,
                                                                                        templatePool,
                                                                                        Registries.TEMPLATE_POOL,
                                                                                        ERROR_INVALID_TEMPLATE_POOL),
                                                                                IntegerArgumentType.getInteger(ctx,
                                                                                        radius),
                                                                                BoolArgumentType.getBool(ctx, start))
                                                                )
                                                )
                                                .executes(ctx -> configAddRing(ctx,
                                                        ResourceKeyArgument.resolveKey(ctx, templatePool,
                                                                Registries.TEMPLATE_POOL, ERROR_INVALID_TEMPLATE_POOL),
                                                        IntegerArgumentType.getInteger(ctx, radius), false)))))
                        .then(Commands.literal("addChaos")
                                .then(Commands.argument(templatePool, ResourceKeyArgument.key(Registries.TEMPLATE_POOL))
                                        .then(
                                                Commands.argument(start, BoolArgumentType.bool())
                                                        .executes(
                                                                ctx -> configAddChaos(ctx,
                                                                        ResourceKeyArgument.resolveKey(ctx,
                                                                                templatePool, Registries.TEMPLATE_POOL,
                                                                                ERROR_INVALID_TEMPLATE_POOL),
                                                                        BoolArgumentType.getBool(ctx, start))
                                                        )
                                        )
                                        .executes(ctx -> configAddChaos(ctx,
                                                ResourceKeyArgument.resolveKey(ctx, templatePool,
                                                        Registries.TEMPLATE_POOL, ERROR_INVALID_TEMPLATE_POOL),
                                                false))))
                        .then(Commands.literal("undo").executes(this::configLayerUndo))
                        .then(Commands.literal("reset").executes(this::configLayerClearEdits))
                        .then(Commands.literal("removeAll").executes(this::configLayerRemoveAll))
                        .then(Commands.literal("remove")
                                .then(
                                        Commands.argument(count, IntegerArgumentType.integer(0))
                                                .then(Commands.argument(start, BoolArgumentType.bool())
                                                        .executes(ctx -> configLayerRemoveSome(ctx,
                                                                IntegerArgumentType.getInteger(ctx, count),
                                                                BoolArgumentType.getBool(ctx, start))))
                                                .executes(ctx -> configLayerRemoveSome(ctx,
                                                        IntegerArgumentType.getInteger(ctx, count), false))
                                )
                                .executes(ctx -> configLayerRemoveSome(ctx, 1, false))))
                .then(Commands.literal("bake").executes(this::configBakeGenerator))
                .then(Commands.literal("export")
                        .then(
                                Commands.argument(export, ResourceLocationArgument.id())
                                        .executes(ctx -> configExportGenerator(ctx,
                                                ResourceLocationArgument.getId(ctx, export)))
                        ));
    }

    private int configAddRoom(
            CommandContext<CommandSourceStack> context,
            Holder.Reference<StructureTemplatePool> structureTemplatePoolReference,
            BlockPos blockPos,
            boolean start) {
        return configAddLayer(context,
                new PredefinedRoomLayer.Factory(new RoomRandomizerImpl.Factory(structureTemplatePoolReference,
                        RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY), blockPos),
                start);
    }

    private int configAddChaos(
            CommandContext<CommandSourceStack> context,
            Holder.Reference<StructureTemplatePool> structureTemplatePoolReference,
            boolean start) {
        return configAddLayer(context,
                new ChaosLayer.Factory(new RoomRandomizerImpl.Factory(structureTemplatePoolReference,
                        RoomRandomizerImpl.MULTI_SIZE_SPACE_HOLDER_FACTORY)),
                start);
    }

    private int configAddRing(
            CommandContext<CommandSourceStack> context,
            Holder.Reference<StructureTemplatePool> structureTemplatePoolReference,
            int radius,
            boolean start) {
        return configAddLayer(context,
                new RingLayer.Factory(new RoomRandomizerImpl.Factory(structureTemplatePoolReference,
                        RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY), radius),
                start);
    }

    private int configAddLayer(
            CommandContext<CommandSourceStack> context,
            LayeredRiftLayout.LayoutLayer.Factory layer,
            boolean start) {
        return applyToRiftKey(context, key -> {
            var edits = ImmutableList.<ListEdit<LayeredRiftLayout.LayoutLayer.Factory>>builder();
            var oldEdits = key.get(WotrDataComponentType.RiftKeyData.LAYOUT_LAYER_EDIT);
            if (oldEdits != null) {
                edits.addAll(oldEdits);
            }
            if (start) {
                edits.add(new Prepend<>(List.of(layer)));
            } else {
                edits.add(new Append<>(List.of(layer)));
            }
            key.set(WotrDataComponentType.RiftKeyData.LAYOUT_LAYER_EDIT, edits.build());
            return Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.add_layer"));
        });
    }

    private int configLayerUndo(CommandContext<CommandSourceStack> context) {
        return applyToRiftKey(context, key -> {
            var oldEdits = key.get(WotrDataComponentType.RiftKeyData.LAYOUT_LAYER_EDIT);
            if (oldEdits != null) {
                var edits = oldEdits.subList(0, oldEdits.size() - 1);
                key.set(WotrDataComponentType.RiftKeyData.LAYOUT_LAYER_EDIT, edits);
            }
            return Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.undo"));
        });
    }

    private int configLayerClearEdits(CommandContext<CommandSourceStack> context) {
        return applyToRiftKey(context, key -> {
            key.set(WotrDataComponentType.RiftKeyData.LAYOUT_LAYER_EDIT, List.of());
            return Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.clear"));
        });
    }

    private int configBakeGenerator(CommandContext<CommandSourceStack> context) {
        return applyToRiftKey(context, key -> {
            key.set(WotrDataComponentType.RiftKeyData.GENERATOR_PRESET, new Holder.Direct<>(
                    RiftGenerationConfig.initialize(key, 0L, context.getSource().registryAccess())));
            key.set(WotrDataComponentType.RiftKeyData.LAYOUT_LAYER_EDIT, List.of());
            key.set(WotrDataComponentType.RiftKeyData.POST_STEPS_EDIT, List.of());
            key.set(WotrDataComponentType.RiftKeyData.JIGSAW_PROCESSORS_EDIT, List.of());
            return Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.bake"));
        });
    }

    private int configExportGenerator(CommandContext<CommandSourceStack> context, ResourceLocation path) {
        if (path.getPath().contains(".") || path.getNamespace().contains(".")) { // dots forbidden due to security
                                                                                 // concerns
            context.getSource()
                    .sendFailure(Component.translatable(
                            WanderersOfTheRift.translationId("command", "rift_key.export.output_contains_dot")));
            return 0;
        }
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }

        var preset = key.get(WotrDataComponentType.RiftKeyData.GENERATOR_PRESET);
        if (preset == null || preset.unwrapKey().isPresent()) {
            context.getSource()
                    .sendFailure(Component
                            .translatable(WanderersOfTheRift.translationId("command", "rift_key.export.not_custom")));
            return 0;
        }

        var generated = context.getSource().getServer().getWorldPath(LevelResource.GENERATED_DIR).normalize();
        var registry = WotrRegistries.Keys.GENERATOR_PRESETS.location();
        var resourcePath = FileUtil.createPathToResource(
                generated.resolve(path.getNamespace()).resolve(registry.getNamespace()).resolve(registry.getPath()),
                path.getPath(), ".json");
        try {
            var resourceParent = resourcePath.getParent();
            if (resourceParent != null && !Files.exists(resourceParent)) {
                Files.createDirectories(Files.exists(resourceParent) ? resourceParent.toRealPath() : resourceParent);
            }
        } catch (IOException var13) {
            WanderersOfTheRift.LOGGER.error("Failed to create parent directory for exporting preset", var13);
            return 0;
        }

        var jsonResult = RiftGenerationConfig.CODEC.encodeStart(
                context.getSource().registryAccess().createSerializationContext(JsonOps.INSTANCE), preset.value());
        if (!jsonResult.isSuccess()) {
            context.getSource()
                    .sendFailure(Component.translatable(
                            WanderersOfTheRift.translationId("command", "rift_key.export.encode_failed")));
            return 0;
        }
        var json = jsonResult.getOrThrow();

        try {
            try (var writer = new FileWriter(resourcePath.toFile()); var jsonWriter = new JsonWriter(writer)) {
                jsonWriter.setFormattingStyle(FormattingStyle.PRETTY.withIndent("    "));
                Streams.write(json, jsonWriter);
            }

            context.getSource()
                    .sendSuccess(
                            () -> Component
                                    .translatable(WanderersOfTheRift.translationId("command", "rift_key.export")),
                            true);

            return 1;
        } catch (Throwable var12) {
            WanderersOfTheRift.LOGGER.error("Failed to write encoded preset to file", var12);
            return 0;
        }
    }

    private int configLayerRemoveAll(CommandContext<CommandSourceStack> context) {
        return applyToRiftKey(context, key -> {
            var edits = ImmutableList.<ListEdit<LayeredRiftLayout.LayoutLayer.Factory>>builder();
            var oldEdits = key.get(WotrDataComponentType.RiftKeyData.LAYOUT_LAYER_EDIT);
            if (oldEdits != null) {
                edits.addAll(oldEdits);
            }
            edits.add(Clear.instance());

            key.set(WotrDataComponentType.RiftKeyData.LAYOUT_LAYER_EDIT, edits.build());
            return Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.remove_all"));
        });
    }

    private int configLayerRemoveSome(CommandContext<CommandSourceStack> context, int n, boolean start) {
        return applyToRiftKey(context, key -> {
            var edits = ImmutableList.<ListEdit<LayeredRiftLayout.LayoutLayer.Factory>>builder();
            var oldEdits = key.get(WotrDataComponentType.RiftKeyData.LAYOUT_LAYER_EDIT);
            if (oldEdits != null) {
                edits.addAll(oldEdits);
            }
            if (start) {
                edits.add(new Drop<>(n));
            } else {
                edits.add(new DropLast<>(n));
            }

            key.set(WotrDataComponentType.RiftKeyData.LAYOUT_LAYER_EDIT, edits.build());
            return Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.remove"));
        });
    }

    private int configGeneratorPreset(
            CommandContext<CommandSourceStack> context,
            Holder.Reference<RiftGenerationConfig> riftGenerationConfigReference) {
        return applyToRiftKey(context, key -> {
            key.set(WotrDataComponentType.RiftKeyData.GENERATOR_PRESET, riftGenerationConfigReference);
            return Component.translatable(
                    WanderersOfTheRift.translationId("command", "rift_key.set_preset"),
                    riftGenerationConfigReference.getRegisteredName()
            );
        });
    }

    private int configTier(CommandContext<CommandSourceStack> context, int tier) {
        return applyToRiftKey(context, key -> {
            key.set(WotrDataComponentType.RiftKeyData.RIFT_TIER, tier);
            return Component.translatable(
                    WanderersOfTheRift.translationId("command", "rift_key.set_tier"), tier
            );
        });
    }

    private int configTheme(CommandContext<CommandSourceStack> context, Holder<RiftTheme> theme) {
        return applyToRiftKey(context, key -> {
            if (theme == null) {
                key.remove(WotrDataComponentType.RiftKeyData.RIFT_THEME);
            } else {
                key.set(WotrDataComponentType.RiftKeyData.RIFT_THEME, theme);
            }
            return Component.translatable(
                    WanderersOfTheRift.translationId("command", "rift_key.set_theme"),
                    theme != null ? theme.getRegisteredName() : "random"
            );
        });
    }

    private int configObjective(CommandContext<CommandSourceStack> context, Holder<ObjectiveType> objective) {
        return applyToRiftKey(context, key -> {
            if (objective == null) {
                key.remove(WotrDataComponentType.RiftKeyData.RIFT_OBJECTIVE);
            } else {
                key.set(WotrDataComponentType.RiftKeyData.RIFT_OBJECTIVE, objective);
            }
            return Component.translatable(
                    WanderersOfTheRift.translationId("command", "rift_key.set_objective"),
                    (objective != null ? objective.getRegisteredName() : "random")
            );
        });
    }

    private int configSeed(CommandContext<CommandSourceStack> context, Long seed) {
        return applyToRiftKey(context, key -> {
            if (seed == null) {
                key.remove(WotrDataComponentType.RiftKeyData.RIFT_SEED);
            } else {
                key.set(WotrDataComponentType.RiftKeyData.RIFT_SEED, seed);
            }
            return Component.translatable(
                    WanderersOfTheRift.translationId("command", "rift_key.set_seed"), (seed != null ? seed : "random")
            );
        });
    }

    private static ItemStack getRiftKey(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable("command.wotr.invalid_player"));
            return ItemStack.EMPTY;
        }
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.getItem() != WotrItems.RIFT_KEY.asItem()) {
            context.getSource().sendFailure(Component.translatable("command.wotr.rift_key.invalid_item"));
            return ItemStack.EMPTY;
        }
        return heldItem;
    }

    private static int applyToRiftKey(
            CommandContext<CommandSourceStack> context,
            Function<ItemStack, Component> function) {

        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable("command.wotr.invalid_player"));
            return 0;
        }
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.getItem() != WotrItems.RIFT_KEY.asItem()) {
            context.getSource().sendFailure(Component.translatable("command.wotr.rift_key.invalid_item"));
            return 0;
        }
        var message = function.apply(heldItem);

        context.getSource().sendSuccess(() -> message, true);
        return 1;
    }

}
