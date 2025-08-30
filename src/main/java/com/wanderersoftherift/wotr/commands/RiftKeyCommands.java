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
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        var edits = ImmutableList.<ListEdit<LayeredRiftLayout.LayoutLayer.Factory>>builder();
        var oldEdits = key.get(WotrDataComponentType.RiftConfigWotrDataComponentType.LAYOUT_LAYER_EDIT);
        if (oldEdits != null) {
            edits.addAll(oldEdits);
        }
        if (start) {
            edits.add(new Prepend(List.of(layer)));
        } else {
            edits.add(new Append(List.of(layer)));
        }
        key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.LAYOUT_LAYER_EDIT, edits.build());

        context.getSource()
                .sendSuccess(
                        () -> Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.add_layer")),
                        true);
        return 1;
    }

    private int configLayerUndo(CommandContext<CommandSourceStack> context) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        var oldEdits = key.get(WotrDataComponentType.RiftConfigWotrDataComponentType.LAYOUT_LAYER_EDIT);
        var edits = oldEdits.subList(0, oldEdits.size() - 1);
        key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.LAYOUT_LAYER_EDIT, edits);

        context.getSource()
                .sendSuccess(
                        () -> Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.undo")),
                        true);
        return 1;
    }

    private int configLayerClearEdits(CommandContext<CommandSourceStack> context) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.LAYOUT_LAYER_EDIT, List.of());

        context.getSource()
                .sendSuccess(
                        () -> Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.clear")),
                        true);
        return 1;
    }

    private int configBakeGenerator(CommandContext<CommandSourceStack> context) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }

        key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.GENERATOR_PRESET,
                new Holder.Direct<>(RiftGenerationConfig.initialize(key, 0L, context.getSource().registryAccess())));
        key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.LAYOUT_LAYER_EDIT, List.of());
        key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.POST_STEPS_EDIT, List.of());
        key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.JIGSAW_PROCESSORS_EDIT, List.of());

        context.getSource()
                .sendSuccess(
                        () -> Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.bake")),
                        true);
        return 1;
    }

    private int configExportGenerator(CommandContext<CommandSourceStack> context, ResourceLocation path) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }

        var preset = key.get(WotrDataComponentType.RiftConfigWotrDataComponentType.GENERATOR_PRESET);
        if (preset.unwrapKey().isPresent()) {
            return 0;
        }

        var generated = context.getSource().getServer().getWorldPath(LevelResource.GENERATED_DIR).normalize();
        var registry = WotrRegistries.Keys.GENERATOR_PRESETS.location();
        var resourcePath = FileUtil.createPathToResource(
                generated.resolve(path.getNamespace()).resolve(registry.getNamespace()).resolve(registry.getPath()),
                path.getPath(), ".json");
        var resourceParent = resourcePath.getParent();

        if (resourceParent == null) {
            return 0;
        }
        try {
            Files.createDirectories(Files.exists(resourceParent) ? resourceParent.toRealPath() : resourceParent);
        } catch (IOException var13) {
            // WanderersOfTheRift.LOGGER.error("Failed to create parent directory: {}", resourceParent);
            return 0;
        }

        var jsonResult = RiftGenerationConfig.CODEC.encodeStart(
                context.getSource().registryAccess().createSerializationContext(JsonOps.INSTANCE), preset.value());
        if (!jsonResult.isSuccess()) {
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
            return 0;
        }
    }

    private int configLayerRemoveAll(CommandContext<CommandSourceStack> context) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        var edits = ImmutableList.<ListEdit<LayeredRiftLayout.LayoutLayer.Factory>>builder();
        var oldEdits = key.get(WotrDataComponentType.RiftConfigWotrDataComponentType.LAYOUT_LAYER_EDIT);
        if (oldEdits != null) {
            edits.addAll(oldEdits);
        }
        edits.add(new Clear());

        key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.LAYOUT_LAYER_EDIT, edits.build());

        context.getSource()
                .sendSuccess(
                        () -> Component
                                .translatable(WanderersOfTheRift.translationId("command", "rift_key.remove_all")),
                        true);
        return 1;
    }

    private int configLayerRemoveSome(CommandContext<CommandSourceStack> context, int n, boolean start) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        var edits = ImmutableList.<ListEdit<LayeredRiftLayout.LayoutLayer.Factory>>builder();
        var oldEdits = key.get(WotrDataComponentType.RiftConfigWotrDataComponentType.LAYOUT_LAYER_EDIT);
        if (oldEdits != null) {
            edits.addAll(oldEdits);
        }
        if (start) {
            edits.add(new Drop(n));
        } else {
            edits.add(new DropLast(n));
        }

        key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.LAYOUT_LAYER_EDIT, edits.build());

        context.getSource()
                .sendSuccess(
                        () -> Component.translatable(WanderersOfTheRift.translationId("command", "rift_key.remove")),
                        true);
        return 1;
    }

    private int configGeneratorPreset(
            CommandContext<CommandSourceStack> context,
            Holder.Reference<RiftGenerationConfig> riftGenerationConfigReference) {
        ItemStack key = getRiftKey(context);
        if (key.isEmpty()) {
            return 0;
        }
        key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.GENERATOR_PRESET, riftGenerationConfigReference);

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
        key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.ITEM_RIFT_TIER, tier);
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
            key.remove(WotrDataComponentType.RiftConfigWotrDataComponentType.RIFT_THEME);
        } else {
            key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.RIFT_THEME, theme);
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
            key.remove(WotrDataComponentType.RiftConfigWotrDataComponentType.RIFT_OBJECTIVE);
        } else {
            key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.RIFT_OBJECTIVE, objective);
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
            key.remove(WotrDataComponentType.RiftConfigWotrDataComponentType.RIFT_SEED);
        } else {
            key.set(WotrDataComponentType.RiftConfigWotrDataComponentType.RIFT_SEED, seed);
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
