package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ResourceLocationException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.TemplateMirrorArgument;
import net.minecraft.commands.arguments.TemplateRotationArgument;
import net.minecraft.commands.arguments.coordinates.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SpawnPieceCommand {
    private static final DynamicCommandExceptionType ERROR_TEMPLATE_INVALID = new DynamicCommandExceptionType(
        templateId -> Component.translatableEscape("commands.place.template.invalid", templateId)
    );
    private static final SimpleCommandExceptionType ERROR_TEMPLATE_FAILED = new SimpleCommandExceptionType(
        Component.translatable("commands.place.template.failed")
    );
    private static final DynamicCommandExceptionType ERROR_INVALID_PROCESSOR = new DynamicCommandExceptionType(
        processorId -> Component.translatableEscape("commands.place.processor.invalid", processorId)
    );
    private static MinecraftServer currentMinecraftServer = null;
    private static Set<ResourceLocation> cachedSuggestion = new HashSet<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        String commandString = "spawnpiece";
        String rlArg = "resourcelocationpath";
        String locationArg = "location";
        String processorArg = "processor";
        String rotationArg = "rotation";
        String mirrorArg = "mirror";
        String seedArg = "seed";

        LiteralCommandNode<CommandSourceStack> source = dispatcher.register(Commands.literal(commandString)
                .requires((permission) -> permission.hasPermission(2))
                .then(Commands.argument(rlArg, ResourceLocationArgument.id())
                        .suggests((ctx, sb) -> SharedSuggestionProvider.suggestResource(templatePathsSuggestions(ctx), sb))
                        .executes(cs -> {
                            WorldCoordinates worldCoordinates = new WorldCoordinates(
                                    new WorldCoordinate(false, cs.getSource().getPosition().x()),
                                    new WorldCoordinate(false, cs.getSource().getPosition().y()),
                                    new WorldCoordinate(false, cs.getSource().getPosition().z())
                            );
                            spawnPiece(cs.getArgument(rlArg, ResourceLocation.class),
                                    worldCoordinates,
                                    null,
                                    Rotation.NONE,
                                    Mirror.NONE,
                                    0,
                                    cs);
                            return 0;
                        })
                        .then(Commands.argument(locationArg, Vec3Argument.vec3())
                                .executes(cs -> {
                                    spawnPiece(cs.getArgument(rlArg, ResourceLocation.class),
                                            Vec3Argument.getCoordinates(cs, locationArg),
                                            null,
                                            Rotation.NONE,
                                            Mirror.NONE,
                                            0,
                                            cs);
                                    return 0;
                                })
                                .then(
                                        Commands.argument(processorArg, ResourceKeyArgument.key(Registries.PROCESSOR_LIST))
                                                .executes(
                                                        cs -> {
                                                            spawnPiece(cs.getArgument(rlArg, ResourceLocation.class),
                                                                    Vec3Argument.getCoordinates(cs, locationArg),
                                                                    ResourceKeyArgument.resolveKey(cs, processorArg, Registries.PROCESSOR_LIST, ERROR_INVALID_PROCESSOR),
                                                                    Rotation.NONE,
                                                                    Mirror.NONE,
                                                                    0,
                                                                    cs);
                                                            return 0;
                                                        })
                                .then(
                                        Commands.argument(rotationArg, TemplateRotationArgument.templateRotation())
                                                .executes(
                                                        cs -> {
                                                            spawnPiece(cs.getArgument(rlArg, ResourceLocation.class),
                                                                    Vec3Argument.getCoordinates(cs, locationArg),
                                                                    ResourceKeyArgument.resolveKey(cs, processorArg, Registries.PROCESSOR_LIST, ERROR_INVALID_PROCESSOR),
                                                                    TemplateRotationArgument.getRotation(cs, rotationArg),
                                                                    Mirror.NONE,
                                                                    0,
                                                                    cs);
                                                            return 0;
                                                        })
                                                .then(
                                                        Commands.argument(mirrorArg, TemplateMirrorArgument.templateMirror())
                                                                .executes(
                                                                        cs -> {
                                                                            spawnPiece(cs.getArgument(rlArg, ResourceLocation.class),
                                                                                    Vec3Argument.getCoordinates(cs, locationArg),
                                                                                    ResourceKeyArgument.resolveKey(cs, processorArg, Registries.PROCESSOR_LIST, ERROR_INVALID_PROCESSOR),
                                                                                    TemplateRotationArgument.getRotation(cs, rotationArg),
                                                                                    TemplateMirrorArgument.getMirror(cs, mirrorArg),
                                                                                    0,
                                                                                    cs);
                                                                            return 0;
                                                                        })
                                                                .then(

                                                                        Commands.argument(seedArg, IntegerArgumentType.integer())
                                                                                .executes(
                                                                                        cs -> {
                                                                                            spawnPiece(cs.getArgument(rlArg, ResourceLocation.class),
                                                                                                    Vec3Argument.getCoordinates(cs, locationArg),
                                                                                                    ResourceKeyArgument.resolveKey(cs, processorArg, Registries.PROCESSOR_LIST, ERROR_INVALID_PROCESSOR),
                                                                                                    TemplateRotationArgument.getRotation(cs, rotationArg),
                                                                                                    TemplateMirrorArgument.getMirror(cs, mirrorArg),
                                                                                                    IntegerArgumentType.getInteger(cs, seedArg),
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

    public static void spawnPiece(ResourceLocation path, Coordinates coordinates, Holder.Reference<StructureProcessorList> processorList, Rotation rotation, Mirror mirror, int seed, CommandContext<CommandSourceStack> cs) throws CommandSyntaxException {
        ServerLevel level = cs.getSource().getLevel();
        Player player = cs.getSource().getEntity() instanceof Player player1 ? player1 : null;
        BlockPos pos = coordinates.getBlockPos(cs.getSource());
        var processors = processorList != null ? processorList.value() : new StructureProcessorList(new ArrayList<>());

        generateStructurePiece(level, pos, player, path, processors, rotation, mirror, seed, cs);
    }

    private static void generateStructurePiece(ServerLevel world, BlockPos pos, Player player, ResourceLocation nbt, StructureProcessorList processorList, Rotation rotation, Mirror mirror, int seed, CommandContext<CommandSourceStack> cs) throws CommandSyntaxException {
        if (player != null) {
            player.displayClientMessage(Component.translatable("command.spawn_piece.generating", nbt.toString()), true);
        }

        if (processorList.list().isEmpty()) {
            world.setBlock(pos.below(), Blocks.STRUCTURE_BLOCK.defaultBlockState().setValue(StructureBlock.MODE, StructureMode.LOAD), 3);
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
        Optional<StructureTemplate> optional = structuremanager.get(ResourceLocation.withDefaultNamespace("structure_void"));
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

                        BlockState oldState = chunk.setBlockState(mutable, Blocks.STRUCTURE_VOID.defaultBlockState(), false);
                        if (oldState != null) {
                            world.getChunkSource().blockChanged(mutable);
                        }
                    }
                }
            }
        });
    }

    public static int placeTemplate(ResourceLocation template, BlockPos pos, List<StructureProcessor> processorList, Rotation rotation, Mirror mirror, int seed, CommandContext<CommandSourceStack> cs) throws CommandSyntaxException {
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
            StructurePlaceSettings structureplacesettings = new StructurePlaceSettings().setMirror(mirror).setRotation(rotation);
            structureplacesettings.clearProcessors().setRandom(StructureBlockEntity.createRandom(seed));

            for(StructureProcessor processor : processorList){
                structureplacesettings.addProcessor(processor);
            }

            boolean flag = structuretemplate.placeInWorld(
                    serverlevel, pos, pos, structureplacesettings, StructureBlockEntity.createRandom((long) seed), 2
            );
            if (!flag) {
                throw ERROR_TEMPLATE_FAILED.create();
            } else {
                cs.getSource().sendSuccess(
                        () -> Component.translatable(
                                "commands.place.template.success", Component.translationArg(template), pos.getX(), pos.getY(), pos.getZ()
                        ),
                        true
                );
                return 0;
            }
        }
    }

    private static void checkLoaded(ServerLevel level, ChunkPos start, ChunkPos end) throws CommandSyntaxException {
        if (ChunkPos.rangeClosed(start, end).filter(p_313494_ -> !level.isLoaded(p_313494_.getWorldPosition())).findAny().isPresent()) {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        }
    }
}