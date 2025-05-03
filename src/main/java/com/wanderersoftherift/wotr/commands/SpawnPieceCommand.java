package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Pair;
import com.wanderersoftherift.wotr.init.ModRiftThemes;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.ThemeProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.LevelRiftThemeData;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.ThemePieceType;
import net.minecraft.ResourceLocationException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.TemplateMirrorArgument;
import net.minecraft.commands.arguments.TemplateRotationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SpawnPieceCommand {
    private static final ResourceLocation EMPTY_TEMPLATE = ResourceLocation.fromNamespaceAndPath("minecraft", "empty");
    private static final DynamicCommandExceptionType ERROR_TEMPLATE_INVALID = new DynamicCommandExceptionType(
            templateId -> Component.translatableEscape("commands.place.template.invalid", templateId));
    private static final SimpleCommandExceptionType ERROR_TEMPLATE_FAILED = new SimpleCommandExceptionType(
            Component.translatable("commands.place.template.failed"));
    private static final DynamicCommandExceptionType ERROR_INVALID_PROCESSOR = new DynamicCommandExceptionType(
            processorId -> Component.translatableEscape("commands.place.processor.invalid", processorId));
    private static MinecraftServer currentMinecraftServer = null;
    private static Set<ResourceLocation> cachedSuggestion = new HashSet<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        String commandString = "spawnpiece";
        String rlArg = "resourcelocationpath";
        String locationArg = "location";
        String themeArg = "theme";
        String terminateArg = "includeTerminators";
        String processorArg = "processor";
        String rotationArg = "rotation";
        String mirrorArg = "mirror";
        String seedArg = "seed";

        LiteralCommandNode<CommandSourceStack> source = dispatcher.register(Commands.literal(commandString)
                .requires((permission) -> permission.hasPermission(2))
                .then(Commands.argument(rlArg, ResourceLocationArgument.id())
                        .suggests((ctx, sb) -> SharedSuggestionProvider.suggestResource(templatePathsSuggestions(ctx),
                                sb))
                        .executes(cs -> {
                            WorldCoordinates worldCoordinates = new WorldCoordinates(
                                    new WorldCoordinate(false, cs.getSource().getPosition().x()),
                                    new WorldCoordinate(false, cs.getSource().getPosition().y()),
                                    new WorldCoordinate(false, cs.getSource().getPosition().z()));
                            spawnPiece(cs.getArgument(rlArg, ResourceLocation.class), worldCoordinates, null,
                                    Rotation.NONE, Mirror.NONE, 0, cs);
                            return 0;
                        })
                        .then(Commands.literal("withPOIs")
                                .then(Commands.argument(themeArg, ResourceKeyArgument.key(ModRiftThemes.RIFT_THEME_KEY))
                                        .executes(cs -> {
                                            WorldCoordinates worldCoordinates = new WorldCoordinates(
                                                    new WorldCoordinate(false, cs.getSource().getPosition().x()),
                                                    new WorldCoordinate(false, cs.getSource().getPosition().y()),
                                                    new WorldCoordinate(false, cs.getSource().getPosition().z()));
                                            spawnPieceFully(cs.getArgument(rlArg, ResourceLocation.class),
                                                    worldCoordinates,
                                                    ResourceKeyArgument.resolveKey(cs, themeArg,
                                                            ModRiftThemes.RIFT_THEME_KEY, ERROR_INVALID_PROCESSOR),
                                                    true, cs.getSource().getLevel().getRandom().nextInt(), cs);
                                            return 0;
                                        })
                                        .then(Commands.argument(locationArg, Vec3Argument.vec3()).executes(cs -> {
                                            spawnPieceFully(cs.getArgument(rlArg, ResourceLocation.class),
                                                    Vec3Argument.getCoordinates(cs, locationArg),
                                                    ResourceKeyArgument.resolveKey(cs, themeArg,
                                                            ModRiftThemes.RIFT_THEME_KEY, ERROR_INVALID_PROCESSOR),
                                                    true, cs.getSource().getLevel().getRandom().nextInt(), cs);
                                            return 0;
                                        })
                                                .then(Commands.argument(terminateArg, BoolArgumentType.bool())
                                                        .executes(cs -> {
                                                            spawnPieceFully(
                                                                    cs.getArgument(rlArg, ResourceLocation.class),
                                                                    Vec3Argument.getCoordinates(cs, locationArg),
                                                                    ResourceKeyArgument.resolveKey(cs, themeArg,
                                                                            ModRiftThemes.RIFT_THEME_KEY,
                                                                            ERROR_INVALID_PROCESSOR),
                                                                    BoolArgumentType.getBool(cs, terminateArg),
                                                                    cs.getSource().getLevel().getRandom().nextInt(),
                                                                    cs);
                                                            return 0;
                                                        })
                                                        .then(Commands.argument(seedArg, IntegerArgumentType.integer())
                                                                .executes(cs -> {
                                                                    spawnPieceFully(
                                                                            cs.getArgument(rlArg,
                                                                                    ResourceLocation.class),
                                                                            Vec3Argument.getCoordinates(cs,
                                                                                    locationArg),
                                                                            ResourceKeyArgument.resolveKey(cs, themeArg,
                                                                                    ModRiftThemes.RIFT_THEME_KEY,
                                                                                    ERROR_INVALID_PROCESSOR),
                                                                            BoolArgumentType.getBool(cs, terminateArg),
                                                                            IntegerArgumentType.getInteger(cs, seedArg),
                                                                            cs);
                                                                    return 0;
                                                                }))))))
                        .then(Commands.argument(locationArg, Vec3Argument.vec3()).executes(cs -> {
                            spawnPiece(cs.getArgument(rlArg, ResourceLocation.class),
                                    Vec3Argument.getCoordinates(cs, locationArg), null, Rotation.NONE, Mirror.NONE, 0,
                                    cs);
                            return 0;
                        })
                                .then(Commands
                                        .argument(processorArg, ResourceKeyArgument.key(Registries.PROCESSOR_LIST))
                                        .executes(cs -> {
                                            spawnPiece(cs.getArgument(rlArg, ResourceLocation.class),
                                                    Vec3Argument.getCoordinates(cs, locationArg),
                                                    ResourceKeyArgument.resolveKey(cs, processorArg,
                                                            Registries.PROCESSOR_LIST, ERROR_INVALID_PROCESSOR),
                                                    Rotation.NONE, Mirror.NONE, 0, cs);
                                            return 0;
                                        })
                                        .then(Commands
                                                .argument(rotationArg, TemplateRotationArgument.templateRotation())
                                                .executes(cs -> {
                                                    spawnPiece(cs.getArgument(rlArg, ResourceLocation.class),
                                                            Vec3Argument.getCoordinates(cs, locationArg),
                                                            ResourceKeyArgument.resolveKey(cs, processorArg,
                                                                    Registries.PROCESSOR_LIST, ERROR_INVALID_PROCESSOR),
                                                            TemplateRotationArgument.getRotation(cs, rotationArg),
                                                            Mirror.NONE, 0, cs);
                                                    return 0;
                                                })
                                                .then(Commands
                                                        .argument(mirrorArg, TemplateMirrorArgument.templateMirror())
                                                        .executes(cs -> {
                                                            spawnPiece(cs.getArgument(rlArg, ResourceLocation.class),
                                                                    Vec3Argument.getCoordinates(cs, locationArg),
                                                                    ResourceKeyArgument.resolveKey(cs, processorArg,
                                                                            Registries.PROCESSOR_LIST,
                                                                            ERROR_INVALID_PROCESSOR),
                                                                    TemplateRotationArgument.getRotation(cs,
                                                                            rotationArg),
                                                                    TemplateMirrorArgument.getMirror(cs, mirrorArg), 0,
                                                                    cs);
                                                            return 0;
                                                        })
                                                        .then(

                                                                Commands.argument(seedArg,
                                                                        IntegerArgumentType.integer()).executes(cs -> {
                                                                            spawnPiece(
                                                                                    cs.getArgument(rlArg,
                                                                                            ResourceLocation.class),
                                                                                    Vec3Argument.getCoordinates(cs,
                                                                                            locationArg),
                                                                                    ResourceKeyArgument.resolveKey(cs,
                                                                                            processorArg,
                                                                                            Registries.PROCESSOR_LIST,
                                                                                            ERROR_INVALID_PROCESSOR),
                                                                                    TemplateRotationArgument
                                                                                            .getRotation(cs,
                                                                                                    rotationArg),
                                                                                    TemplateMirrorArgument.getMirror(cs,
                                                                                            mirrorArg),
                                                                                    IntegerArgumentType.getInteger(cs,
                                                                                            seedArg),
                                                                                    cs);
                                                                            return 0;
                                                                        }))))))));

        dispatcher.register(Commands.literal(commandString).redirect(source));
    }

    private static Set<ResourceLocation> templatePathsSuggestions(CommandContext<CommandSourceStack> cs) {
        if (currentMinecraftServer == cs.getSource().getServer()) {
            return cachedSuggestion;
        }
        StructureTemplateManager structuretemplatemanager = cs.getSource().getLevel().getStructureManager();
        Set<ResourceLocation> rlSet = structuretemplatemanager.listTemplates().collect(Collectors.toSet());

        currentMinecraftServer = cs.getSource().getServer();
        cachedSuggestion = rlSet;
        return rlSet;
    }

    public static void spawnPiece(
            ResourceLocation path,
            Coordinates coordinates,
            Holder.Reference<StructureProcessorList> processorList,
            Rotation rotation,
            Mirror mirror,
            int seed,
            CommandContext<CommandSourceStack> cs) throws CommandSyntaxException {
        ServerLevel level = cs.getSource().getLevel();
        Player player;
        if (cs.getSource().getEntity() instanceof Player player1) {
            player = player1;
        } else {
            player = null;
        }
        BlockPos pos = coordinates.getBlockPos(cs.getSource());
        var processors = processorList != null ? processorList.value() : new StructureProcessorList(new ArrayList<>());

        generateStructurePiece(level, pos, player, path, processors, rotation, mirror, seed, cs);
    }

    public static void spawnPieceFully(
            ResourceLocation path,
            Coordinates coordinates,
            Holder.Reference<RiftTheme> theme,
            boolean includeTerminators,
            int seed,
            CommandContext<CommandSourceStack> cs) throws CommandSyntaxException {
        if (cs.getSource().getEntity() instanceof Player player) {
            player.displayClientMessage(Component.translatable("command.spawn_piece.generating", path.toString()),
                    true);
        }
        BlockPos pos = coordinates.getBlockPos(cs.getSource());
        placeStructure(path, pos.mutable(), theme, includeTerminators, seed, cs);
    }

    private static void generateStructurePiece(
            ServerLevel world,
            BlockPos pos,
            Player player,
            ResourceLocation nbt,
            StructureProcessorList processorList,
            Rotation rotation,
            Mirror mirror,
            int seed,
            CommandContext<CommandSourceStack> cs) throws CommandSyntaxException {
        if (player != null) {
            player.displayClientMessage(Component.translatable("command.spawn_piece.generating", nbt.toString()), true);
        }

        if (processorList.list().isEmpty()) {
            world.setBlock(pos.below(),
                    Blocks.STRUCTURE_BLOCK.defaultBlockState().setValue(StructureBlock.MODE, StructureMode.LOAD), 3);
            if (world.getBlockEntity(pos.below()) instanceof StructureBlockEntity structureBlockTileEntity) {
                structureBlockTileEntity.setStructureName(nbt);
                structureBlockTileEntity.setMode(StructureMode.LOAD);
                structureBlockTileEntity.setIgnoreEntities(false);
                fillStructureVoidSpace(world, pos.mutable());
                structureBlockTileEntity.placeStructure(world);
                structureBlockTileEntity.setMode(StructureMode.SAVE);
                structureBlockTileEntity.setIgnoreEntities(false);
            }
        } else {
            placeTemplate(nbt, pos.mutable(), processorList.list(), rotation, mirror, seed, cs);
        }
    }

    // Needed so that structure void is preserved in structure pieces.
    private static void fillStructureVoidSpace(ServerLevel world, BlockPos startSpot) {
        StructureTemplateManager structuremanager = world.getStructureManager();
        Optional<StructureTemplate> optional = structuremanager
                .get(ResourceLocation.withDefaultNamespace("structure_void"));
        optional.ifPresent(template -> {
            BlockPos.MutableBlockPos mutable = startSpot.mutable();
            ChunkAccess chunk = world.getChunk(mutable);
            for (int x = 0; x < template.getSize().getX(); x++) {
                for (int z = 0; z < template.getSize().getZ(); z++) {
                    for (int y = 0; y < template.getSize().getY(); y++) {
                        mutable.set(startSpot).move(x, y, z);
                        if (chunk.getPos().x != mutable.getX() >> 4 || chunk.getPos().z != mutable.getZ() >> 4) {
                            chunk = world.getChunk(mutable);
                        }

                        BlockState oldState = chunk.setBlockState(mutable, Blocks.STRUCTURE_VOID.defaultBlockState(),
                                false);
                        if (oldState != null) {
                            world.getChunkSource().blockChanged(mutable);
                        }
                    }
                }
            }
        });
    }

    public static int placeStructure(
            ResourceLocation template,
            BlockPos pos,
            Holder<RiftTheme> theme,
            boolean includeTerminators,
            int seed,
            CommandContext<CommandSourceStack> cs) throws CommandSyntaxException {
        ServerLevel serverlevel = cs.getSource().getLevel();
        StructureTemplateManager structuretemplatemanager = serverlevel.getStructureManager();

        LevelRiftThemeData riftThemeData = LevelRiftThemeData.getFromLevel(serverlevel);
        Holder<RiftTheme> originalTheme = riftThemeData.getTheme();
        riftThemeData.setTheme(theme);
        try {
            Optional<StructureTemplate> optionalTemplate = structuretemplatemanager.get(template);
            if (optionalTemplate.isEmpty()) {
                throw ERROR_TEMPLATE_INVALID.create(template);
            }
            StructureTemplate structuretemplate = optionalTemplate.get();
            checkLoaded(serverlevel, new ChunkPos(pos), new ChunkPos(pos.offset(structuretemplate.getSize())));

            Holder<StructureProcessorList> structureProcessorList = Holder.direct(
                    new StructureProcessorList(Collections.singletonList(new ThemeProcessor(ThemePieceType.ROOM))));
            Registry<StructureTemplatePool> poolRegistry = serverlevel.registryAccess()
                    .lookupOrThrow(Registries.TEMPLATE_POOL);
            StructureTemplatePool pool = new StructureTemplatePool(poolRegistry.get(EMPTY_TEMPLATE).get(),
                    Collections.singletonList(
                            new Pair<>(StructurePoolElement.single(template.toString(), structureProcessorList)
                                    .apply(StructureTemplatePool.Projection.RIGID), 1)));
            Structure.GenerationContext context = new Structure.GenerationContext(serverlevel.registryAccess(),
                    serverlevel.getChunkSource().getGenerator(),
                    serverlevel.getChunkSource().getGenerator().getBiomeSource(),
                    serverlevel.getChunkSource().randomState(), serverlevel.getStructureManager(), seed,
                    new ChunkPos(pos), serverlevel, (biomeHolder -> true));

            Optional<Structure.GenerationStub> pieceGenerator = JigsawPlacement.addPieces(context, Holder.direct(pool),
                    Optional.empty(), 1, pos, false, Optional.empty(), 80, PoolAliasLookup.EMPTY, DimensionPadding.ZERO,
                    LiquidSettings.IGNORE_WATERLOGGING);

            List<StructurePiece> structurePieceList = pieceGenerator.get().getPiecesBuilder().build().pieces();

            if (structurePieceList.isEmpty()) {
                throw ERROR_TEMPLATE_INVALID.create(template);
            }

            // Correct location for rotation
            BoundingBox boundingBox = structurePieceList.getFirst().getBoundingBox();
            Vec3i offset = new Vec3i(boundingBox.minX() < pos.getX() ? boundingBox.getXSpan() - 1 : 0, 0,
                    boundingBox.minZ() < pos.getZ() ? boundingBox.getZSpan() - 1 : 0);

            structurePieceList.forEach(piece -> {
                if (!includeTerminators && piece instanceof PoolElementStructurePiece poolPiece
                        && poolPiece.getElement().toString().contains("terminator")) {
                    return;
                }
                piece.move(offset.getX(), 0, offset.getZ());
                piece.postProcess(serverlevel, serverlevel.structureManager(),
                        serverlevel.getChunkSource().getGenerator(), new WorldgenRandom(new LegacyRandomSource(seed)),
                        BoundingBox.infinite(), new ChunkPos(pos), pos);
            });

            cs.getSource()
                    .sendSuccess(() -> Component.translatable("commands.place.template.success",
                            Component.translationArg(template), pos.getX(), pos.getY(), pos.getZ()), true);
            return 0;

        } finally {
            riftThemeData.setTheme(originalTheme);
        }
    }

    public static int placeTemplate(
            ResourceLocation template,
            BlockPos pos,
            List<StructureProcessor> processorList,
            Rotation rotation,
            Mirror mirror,
            int seed,
            CommandContext<CommandSourceStack> cs) throws CommandSyntaxException {
        ServerLevel serverlevel = cs.getSource().getLevel();
        StructureTemplateManager structuretemplatemanager = serverlevel.getStructureManager();

        Optional<StructureTemplate> optional;
        try {
            optional = structuretemplatemanager.get(template);
        } catch (ResourceLocationException resourcelocationexception) {
            throw ERROR_TEMPLATE_INVALID.create(template);
        }

        if (optional.isEmpty()) {
            throw ERROR_TEMPLATE_INVALID.create(template);
        } else {
            StructureTemplate structuretemplate = optional.get();
            checkLoaded(serverlevel, new ChunkPos(pos), new ChunkPos(pos.offset(structuretemplate.getSize())));
            StructurePlaceSettings structureplacesettings = new StructurePlaceSettings().setMirror(mirror)
                    .setRotation(rotation);
            structureplacesettings.clearProcessors().setRandom(StructureBlockEntity.createRandom(seed));

            for (StructureProcessor processor : processorList) {
                structureplacesettings.addProcessor(processor);
            }

            boolean flag = structuretemplate.placeInWorld(serverlevel, pos, pos, structureplacesettings,
                    StructureBlockEntity.createRandom((long) seed), 2);
            if (!flag) {
                throw ERROR_TEMPLATE_FAILED.create();
            } else {
                cs.getSource()
                        .sendSuccess(() -> Component.translatable("commands.place.template.success",
                                Component.translationArg(template), pos.getX(), pos.getY(), pos.getZ()), true);
                return 0;
            }
        }
    }

    private static void checkLoaded(ServerLevel level, ChunkPos start, ChunkPos end) throws CommandSyntaxException {
        if (ChunkPos.rangeClosed(start, end).anyMatch(pos -> !level.isLoaded(pos.getWorldPosition()))) {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        }
    }
}