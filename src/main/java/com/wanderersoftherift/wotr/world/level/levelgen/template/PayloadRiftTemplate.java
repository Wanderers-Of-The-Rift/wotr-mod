package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.util.Ref;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftAdjacencyProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftAdjacencyProcessorEvaluator;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftFinalProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
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
                WanderersOfTheRift.LOGGER.warn("incompatible processor type: {}", processor.getClass());
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
    public MapCodec<? extends RiftGeneratable> codec() {
        return null;
    }

    @Override
    public void processAndPlace(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            Vec3i placementShift,
            TripleMirror mirror) {
        var structurePos = new BlockPos(room.space.origin().getX() << RiftProcessedChunk.CHUNK_WIDTH_SHIFT,
                room.space.origin().getY() << RiftProcessedChunk.CHUNK_HEIGHT_SHIFT,
                room.space.origin().getZ() << RiftProcessedChunk.CHUNK_WIDTH_SHIFT).offset(placementShift);
        payload.processPayloadBlocks(this, room, world, structurePos, mirror);
        payload.processPayloadEntities(this, room, world, structurePos, mirror);

        var pieceSize = mirror.diagonal() ? new Vec3i(size().getZ(), size().getY(), size().getX()) : this.size;
        RiftAdjacencyProcessorEvaluator.applyAdjacencyProcessors(room, world, structurePos, pieceSize,
                adjacencyProcessors);
        applyFinalProcessors(room, world, structurePos, pieceSize);
    }

    private void applyFinalProcessors(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Vec3i pieceSize) {
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

    public void processBlock(
            BlockState blockState,
            BlockPos.MutableBlockPos mutablePosition,
            ServerLevelAccessor world,
            BlockPos offset,
            BlockEntity entity,
            boolean isVisible,
            RiftProcessedChunk roomChunk) {
        var x = mutablePosition.getX();
        var y = mutablePosition.getY();
        var z = mutablePosition.getZ();
        var processors = templateProcessors;
        var entityRef = new Ref<BlockEntity>(entity);
        blockState = ((RiftTemplateProcessor) JigsawReplacementProcessor.INSTANCE).processBlockState(
                blockState, x, y, z, world, offset, entityRef, isVisible);
        if (blockState == null) {
            return;
        }
        for (int k = 0; k < processors.size(); k++) {
            blockState = processors.get(k).processBlockState(blockState, x, y, z, world, offset, entityRef, isVisible);
            if (blockState == null) {
                return;
            }
        }
        var xWithinChunk = x & RiftProcessedChunk.CHUNK_WIDTH_MASK;
        var yWithinChunk = y & RiftProcessedChunk.CHUNK_HEIGHT_MASK;
        var zWithinChunk = z & RiftProcessedChunk.CHUNK_WIDTH_MASK;
        var idx = zWithinChunk | (yWithinChunk << RiftProcessedChunk.CHUNK_WIDTH_SHIFT); // todo maybe re-add mid-air
                                                                                         // flag
        roomChunk.blocks[(xWithinChunk) | (idx << RiftProcessedChunk.CHUNK_WIDTH_SHIFT)] = blockState;
        var mask = (short) (1 << xWithinChunk);
        roomChunk.newlyAdded[idx] |= mask;
        if (!isVisible) {
            roomChunk.hidden[idx] |= mask;
        }

        entity = entityRef.getValue();
        if (entity != null && blockState.hasBlockEntity()) {
            roomChunk.blockEntities.add(entity);
        }
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
