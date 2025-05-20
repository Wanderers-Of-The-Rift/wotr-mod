package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.LevelRiftThemeData;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.ThemePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.init.worldgen.WotrProcessors.RIFT_THEME;

public class ThemeProcessor extends StructureProcessor implements RiftTemplateProcessor, RiftFinalProcessor {
    public static final MapCodec<ThemeProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(ThemePieceType.CODEC.fieldOf("piece_type").forGetter(ThemeProcessor::getThemePieceType)
            ).apply(builder, ThemeProcessor::new));

    private ThemePieceType themePieceType;
    private ThemeCache lastThemeTemplateProcessorCache = null;

    public ThemeProcessor(ThemePieceType themePieceType) {
        this.themePieceType = themePieceType;
    }

    public ThemePieceType getThemePieceType() {
        return themePieceType;
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(
            LevelReader world,
            BlockPos piecePos,
            BlockPos structurePos,
            StructureTemplate.StructureBlockInfo rawBlockInfo,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructurePlaceSettings settings,
            @Nullable StructureTemplate template) {
        List<StructureProcessor> processors = getThemeProcessors(world, structurePos);

        for (int i = 0; i < processors.size() && blockInfo != null; i++) {
            blockInfo = processors.get(i)
                    .process(world, piecePos, structurePos, rawBlockInfo, blockInfo, settings, template);
        }
        return blockInfo;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor serverLevel,
            BlockPos piecePos,
            BlockPos structurePos,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructurePlaceSettings settings) {
        List<StructureTemplate.StructureBlockInfo> result = processedBlockInfos;

        for (StructureProcessor structureprocessor : getThemeProcessors(serverLevel.getLevel(), structurePos)) {
            result = structureprocessor.finalizeProcessing(serverLevel, piecePos, structurePos, originalBlockInfos,
                    result, settings);
        }

        return result;
    }

    private List<StructureProcessor> getThemeProcessors(LevelReader world, BlockPos structurePos) {
        if (world instanceof ServerLevel serverLevel) {
            LevelRiftThemeData riftThemeData = LevelRiftThemeData.getFromLevel(serverLevel);
            var result = (riftThemeData.getTheme() != null)
                    ? riftThemeData.getTheme().value().getProcessors(themePieceType)
                    : defaultThemeProcessors(serverLevel, structurePos);
            return result;
        }
        return new ArrayList<>();
    }

    private List<RiftTemplateProcessor> getThemeTemplateProcessors(LevelReader world, BlockPos structurePos) {
        var currentCache = lastThemeTemplateProcessorCache;
        if (world != null && currentCache != null && currentCache.level.refersTo(world)) {
            return currentCache.templateProcessors;
        }
        if (world instanceof ServerLevel serverLevel) {
            return reloadCache(serverLevel, structurePos).templateProcessors;
        }
        return new ArrayList<>();
    }

    private List<RiftFinalProcessor> getFinalTemplateProcessors(LevelReader world, BlockPos structurePos) {
        var currentCache = lastThemeTemplateProcessorCache;
        if (world != null && currentCache != null && currentCache.level.refersTo(world)) {
            return currentCache.finalProcessors;
        }
        if (world instanceof ServerLevel serverLevel) {
            return reloadCache(serverLevel, structurePos).finalProcessors;
        }
        return new ArrayList<>();
    }

    private List<ReplaceThisOrAdjacentRiftProcessor<?>> getAirReplaceTemplateProcessors(
            LevelReader world,
            BlockPos structurePos) {
        var currentCache = lastThemeTemplateProcessorCache;
        if (world != null && currentCache != null && currentCache.level.refersTo(world)) {
            return currentCache.airReplaceProcessors;
        }
        if (world instanceof ServerLevel serverLevel) {
            return reloadCache(serverLevel, structurePos).airReplaceProcessors;
        }
        return new ArrayList<>();
    }

    private ThemeCache reloadCache(ServerLevel serverLevel, BlockPos structurePos) {
        LevelRiftThemeData riftThemeData = LevelRiftThemeData.getFromLevel(serverLevel);
        var structureProcessors = (riftThemeData.getTheme() != null)
                ? riftThemeData.getTheme().value().getProcessors(themePieceType)
                : defaultThemeProcessors(serverLevel, structurePos);
        var newCache = new ThemeCache(new PhantomReference<>(serverLevel, null), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());
        for (var processor : structureProcessors) {
            var used = false;
            if (processor instanceof RiftTemplateProcessor riftTemplateProcessor) {
                newCache.templateProcessors.add(riftTemplateProcessor);
                used = true;
            }
            if (processor instanceof RiftFinalProcessor riftTemplateProcessor) {
                newCache.finalProcessors.add(riftTemplateProcessor);
                used = true;
            }
            if (processor instanceof ReplaceThisOrAdjacentRiftProcessor<?> replaceThisOrAdjacentRiftProcessor) {
                newCache.airReplaceProcessors.add(replaceThisOrAdjacentRiftProcessor);
                used = true;
            }
            if (!used) {
                WanderersOfTheRift.LOGGER.warn("incompatible processor type:" + processor.getClass());
            }
        }
        return lastThemeTemplateProcessorCache = newCache;

    }

    private List<StructureProcessor> defaultThemeProcessors(ServerLevel world, BlockPos structurePos) {
        Optional<Registry<RiftTheme>> registryReference = world.registryAccess()
                .lookup(WotrRegistries.Keys.RIFT_THEMES);
        return registryReference.get()
                .get(ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "cave"))
                .get()
                .value()
                .getProcessors(themePieceType);
    }

    protected StructureProcessorType<?> getType() {
        return RIFT_THEME.get();
    }

    @Override
    public BlockState processBlockState(
            BlockState currentState,
            int x,
            int y,
            int z,
            ServerLevelAccessor world,
            BlockPos structurePos,
            CompoundTag nbt,
            boolean isVisible) {
        var processors = getThemeTemplateProcessors(world.getLevel(), structurePos);

        for (int i = 0; i < processors.size() && currentState != null; i++) {
            currentState = processors.get(i)
                    .processBlockState(currentState, x, y, z, world, structurePos, nbt, isVisible);
        }
        return currentState;

    }

    @Override
    public void finalizeRoomProcessing(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Vec3i pieceSize) {

        var processors = getAirReplaceTemplateProcessors(world.getLevel(), structurePos);
        var pairs = new ReplaceThisOrAdjacentRiftProcessor.ProcessorDataPair<?>[processors.size()];
        for (int i = 0; i < pairs.length; i++) {
            pairs[i] = ReplaceThisOrAdjacentRiftProcessor.ProcessorDataPair.create(processors.get(i), structurePos,
                    pieceSize);
        }

        var directionBlocksArray = new BlockState[7];
        var preloaded = new BlockState[4][pieceSize.getZ() + 2][pieceSize.getX() + 2];
        var saveMask = new boolean[4][pieceSize.getZ() + 2][pieceSize.getX() + 2];
        ReplaceThisOrAdjacentRiftProcessor.preloadLayer(room, structurePos.getX(), structurePos.getY(),
                structurePos.getZ(), pieceSize, preloaded[0], saveMask[0]);
        for (int y = 0; y < pieceSize.getY(); y++) {
            ReplaceThisOrAdjacentRiftProcessor.preloadLayer(room, structurePos.getX(), structurePos.getY() + y + 1,
                    structurePos.getZ(), pieceSize, preloaded[(y + 1) & 3], saveMask[(y + 1) & 3]);
            var pre = preloaded[y & 3];
            var sav = saveMask[y & 3];
            for (int z = 0; z < pieceSize.getZ(); z++) {
                var preDown = preloaded[(y - 1) & 3][z + 1];
                var preUp = preloaded[(y + 1) & 3][z + 1];
                var preNorth = pre[z];
                var preSouth = pre[z + 2];
                var preCenter = pre[z + 1];
                var savDown = saveMask[(y - 1) & 3][z + 1];
                var savUp = saveMask[(y + 1) & 3][z + 1];
                var savNorth = sav[z];
                var savSouth = sav[z + 2];
                var savCenter = sav[z + 1];
                for (int x = 0; x < pieceSize.getX(); x++) {

                    BlockState currentState = preCenter[x + 1];
                    if (currentState != null) { // todo get hidden from template
                        // var hidden = true;
                        var midair = true;
                        var block = directionBlocksArray[0] = preDown[x + 1];
                        if (block != null) {
                            // hidden &= block.canOcclude();
                            midair &= block.isAir();
                        }
                        block = directionBlocksArray[1] = preUp[x + 1];
                        if (block != null) {
                            // hidden &= block.canOcclude();
                            midair &= block.isAir();
                        }
                        block = directionBlocksArray[2] = preNorth[x + 1];
                        if (block != null) {
                            // hidden &= block.canOcclude();
                            midair &= block.isAir();
                        }
                        block = directionBlocksArray[3] = preSouth[x + 1];
                        if (block != null) {
                            // hidden &= block.canOcclude();
                            midair &= block.isAir();
                        }
                        block = directionBlocksArray[4] = preCenter[x];
                        if (block != null) {
                            // hidden &= block.canOcclude();
                            midair &= block.isAir();
                        }
                        block = directionBlocksArray[5] = preCenter[x + 2];
                        if (block != null) {
                            // hidden &= block.canOcclude();
                            midair &= block.isAir();
                        }

                        if (/* hidden || */midair) {
                            continue;
                        }
                        directionBlocksArray[6] = currentState;
                        int modifyMask = 0;
                        for (int i = 0; i < pairs.length; i++) {
                            modifyMask |= pairs[i].run(directionBlocksArray, false);
                        }
                        if (modifyMask != 0) {
                            if ((modifyMask & 0b111) != 0) {
                                if ((modifyMask & 0b1) != 0) {
                                    preDown[x + 1] = directionBlocksArray[0];
                                    savDown[x + 1] = true;
                                }
                                if ((modifyMask & 0b10) != 0) {
                                    preUp[x + 1] = directionBlocksArray[1];
                                    savUp[x + 1] = true;
                                }
                                if ((modifyMask & 0b100) != 0) {
                                    preNorth[x + 1] = directionBlocksArray[2];
                                    savNorth[x + 1] = true;
                                }
                            }
                            if ((modifyMask & 0b111000) != 0) {
                                if ((modifyMask & 0b1000) != 0) {
                                    preSouth[x + 1] = directionBlocksArray[3];
                                    savSouth[x + 1] = true;
                                }
                                if ((modifyMask & 0b10000) != 0) {
                                    preCenter[x] = directionBlocksArray[4];
                                    savCenter[x] = true;
                                }
                                if ((modifyMask & 0b100000) != 0) {
                                    preCenter[x + 2] = directionBlocksArray[5];
                                    savCenter[x + 2] = true;
                                }
                            }

                            if ((modifyMask & 0b1000000) != 0) {
                                preCenter[x + 1] = directionBlocksArray[6];
                                savCenter[x + 1] = true;
                            }
                        }

                    }
                }
            }
            ReplaceThisOrAdjacentRiftProcessor.saveLayer(room, structurePos.getX(), structurePos.getY() + y - 1,
                    structurePos.getZ(), pieceSize, preloaded[(y - 1) & 3], saveMask[(y - 1) & 3]);

        }

        ReplaceThisOrAdjacentRiftProcessor.saveLayer(room, structurePos.getX(),
                structurePos.getY() + pieceSize.getY() - 1, structurePos.getZ(), pieceSize,
                preloaded[(pieceSize.getY() - 1) & 3], saveMask[(pieceSize.getY() - 1) & 3]);

        var processors2 = getFinalTemplateProcessors(world.getLevel(), structurePos);

        for (int i = 0; i < processors2.size(); i++) {
            processors2.get(i).finalizeRoomProcessing(room, world, structurePos, pieceSize);
        }
    }

    private record ThemeCache(PhantomReference<LevelReader> level, List<RiftTemplateProcessor> templateProcessors,
            List<RiftFinalProcessor> finalProcessors,
            List<ReplaceThisOrAdjacentRiftProcessor<?>> airReplaceProcessors) {
    }
}