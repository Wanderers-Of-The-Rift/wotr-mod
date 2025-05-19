package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftFinalProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
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
    private final List<RiftTemplateProcessor> templateProcessors;
    private final List<StructureTemplate.JigsawBlockInfo> jigsaws;
    private final String identifier;
    private final TemplatePayload payload;

    public PayloadRiftTemplate(Vec3i size, StructureProcessorList baseProcessors,
            List<StructureTemplate.JigsawBlockInfo> jigsaws, String identifier, TemplatePayload payload) {
        this.payload = payload;
        this.size = size;
        var templateProcessors = new ArrayList<RiftTemplateProcessor>();
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
            if (!used && !(processor instanceof NopProcessor)) {
                WanderersOfTheRift.LOGGER.warn("incompatible processor type:" + processor.getClass());
            }
        }

        this.templateProcessors = ImmutableList.copyOf(templateProcessors);
        this.finalProcessors = ImmutableList.copyOf(finalProcessors);
        this.entityProcessor = ImmutableList.copyOf(baseProcessors.list());

        this.jigsaws = jigsaws;
        this.identifier = identifier;

    }

    @Override
    public void processAndPlace(
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            Vec3i placementShift,
            TripleMirror mirror) {
        var offset = new BlockPos(destination.space.origin().multiply(16)).offset(placementShift);
        payload.processPayloadBlocks(this, destination, world, offset, mirror);
        payload.processPayloadEntities(this, destination, world, offset, mirror);

        var size = mirror.diagonal() ? new Vec3i(size().getZ(), size().getY(), size().getX()) : this.size;
        var processors = finalProcessors;
        for (int k = 0; k < processors.size(); k++) {
            processors.get(k).finalizeRoomProcessing(destination, world, offset, size);
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
