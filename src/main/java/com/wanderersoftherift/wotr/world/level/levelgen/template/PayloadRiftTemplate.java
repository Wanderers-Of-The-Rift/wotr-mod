package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftAdjacencyProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftFinalProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.NopProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PayloadRiftTemplate implements RiftGeneratable {
    private final Vec3i size;
    private final List<StructureProcessor> entityProcessor;
    private final List<RiftFinalProcessor> finalProcessors;
    private final List<RiftAdjacencyProcessor<?>> adjacencyProcessors;
    private final List<RiftTemplateProcessor> templateProcessors;
    private final List<StructureTemplate.JigsawBlockInfo> jigsaws;
    private final String identifier;
    private final TemplatePayload payload;

    public PayloadRiftTemplate(Vec3i size, StructureProcessorList baseProcessors,
            List<StructureTemplate.JigsawBlockInfo> jigsaws, String identifier, TemplatePayload payload) {
        this.payload = payload;
        this.size = size;
        var templateProcessors = new ArrayList<RiftTemplateProcessor>();
        var adjacencyProcessors = new ArrayList<RiftAdjacencyProcessor<?>>();
        var finalProcessors = new ArrayList<RiftFinalProcessor>();

        for (StructureProcessor processor : baseProcessors.list()) {
            var used = false;
            if (processor instanceof RiftTemplateProcessor riftTemplateProcessor) {
                templateProcessors.add(riftTemplateProcessor);
                used = true;
            }
            if (processor instanceof RiftFinalProcessor riftFinalProcessor) {
                finalProcessors.add(riftFinalProcessor);
                used = true;
            }
            if (processor instanceof RiftAdjacencyProcessor<?> riftAdjacentProcessor) {
                adjacencyProcessors.add(riftAdjacentProcessor);
                used = true;
            }
            if (!used && !(processor instanceof NopProcessor)) {
                WanderersOfTheRift.LOGGER.warn("incompatible processor type:" + processor.getClass());
            }
        }
        this.templateProcessors = ImmutableList.copyOf(templateProcessors);
        this.finalProcessors = ImmutableList.copyOf(finalProcessors);
        this.adjacencyProcessors = ImmutableList.copyOf(adjacencyProcessors);
        this.entityProcessor = ImmutableList.copyOf(baseProcessors.list());

        this.jigsaws = jigsaws;
        this.identifier = identifier;

    }

    @Override
    public void processAndPlace(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            Vec3i placementShift,
            TripleMirror mirror) {
        // todo create a mask of blocks modified by this template
        var structurePos = new BlockPos(room.space.origin().multiply(16)).offset(placementShift);
        payload.processPayloadBlocks(this, room, world, structurePos, mirror);
        payload.processPayloadEntities(this, room, world, structurePos, mirror);

        var pieceSize = mirror.diagonal() ? new Vec3i(size().getZ(), size().getY(), size().getX()) : this.size;

        var processors = adjacencyProcessors;
        var pairs = new RiftAdjacencyProcessor.ProcessorDataPair<?>[processors.size()];

        for (int i = 0; i < pairs.length; i++) {
            pairs[i] = RiftAdjacencyProcessor.ProcessorDataPair.create(processors.get(i), structurePos, pieceSize,
                    world);
        }

        var directionBlocksArray = new BlockState[7];
        var preloaded = new BlockState[4][pieceSize.getZ() + 2][pieceSize.getX() + 2];
        var saveMask = new boolean[4][pieceSize.getZ() + 2][pieceSize.getX() + 2];
        RiftAdjacencyProcessor.preloadLayer(room, structurePos.getX(), structurePos.getY(), structurePos.getZ(),
                pieceSize, preloaded[0], saveMask[0]);
        for (int y = 0; y < pieceSize.getY(); y++) {
            RiftAdjacencyProcessor.preloadLayer(room, structurePos.getX(), structurePos.getY() + y + 1,
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
                        /*
                         * if (block != null) { // hidden &= block.canOcclude(); midair &= block.isAir(); }
                         */
                        block = directionBlocksArray[1] = preUp[x + 1];
                        /*
                         * if (block != null) { // hidden &= block.canOcclude(); midair &= block.isAir(); }
                         */
                        block = directionBlocksArray[2] = preNorth[x + 1];
                        /*
                         * if (block != null) { // hidden &= block.canOcclude(); midair &= block.isAir(); }
                         */
                        block = directionBlocksArray[3] = preSouth[x + 1];
                        /*
                         * if (block != null) { // hidden &= block.canOcclude(); midair &= block.isAir(); }
                         */
                        block = directionBlocksArray[4] = preCenter[x];
                        /*
                         * if (block != null) { // hidden &= block.canOcclude(); midair &= block.isAir(); }
                         */
                        block = directionBlocksArray[5] = preCenter[x + 2];
                        /*
                         * if (block != null) { // hidden &= block.canOcclude(); midair &= block.isAir(); }
                         */

                        if (/* hidden || */midair && false) {
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
            RiftAdjacencyProcessor.saveLayer(room, structurePos.getX(), structurePos.getY() + y - 1,
                    structurePos.getZ(), pieceSize, preloaded[(y - 1) & 3], saveMask[(y - 1) & 3]);

        }

        RiftAdjacencyProcessor.saveLayer(room, structurePos.getX(), structurePos.getY() + pieceSize.getY() - 1,
                structurePos.getZ(), pieceSize, preloaded[(pieceSize.getY() - 1) & 3],
                saveMask[(pieceSize.getY() - 1) & 3]);

        var finalProcessors = this.finalProcessors;
        for (int k = 0; k < finalProcessors.size(); k++) {
            finalProcessors.get(k).finalizeRoomProcessing(room, world, structurePos, pieceSize);
        }
    }

    @Override
    public Collection<StructureTemplate.JigsawBlockInfo> jigsaws() {
        return jigsaws;
    }

    @Override
    public Vec3i size() {
        return size;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    public List<RiftTemplateProcessor> getTemplateProcessors() {
        return templateProcessors;
    }

    public List<StructureProcessor> getEntityProcessor() {
        return entityProcessor;
    }

    public static interface TemplatePayload {
        void processPayloadBlocks(
                PayloadRiftTemplate template,
                RiftProcessedRoom destination,
                ServerLevelAccessor world,
                BlockPos offset,
                TripleMirror mirror);

        void processPayloadEntities(
                PayloadRiftTemplate payloadRiftTemplate,
                RiftProcessedRoom destination,
                ServerLevelAccessor world,
                BlockPos offset,
                TripleMirror mirror);
    }
}
