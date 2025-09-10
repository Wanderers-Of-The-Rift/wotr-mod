package com.wanderersoftherift.wotr.world.level.levelgen.processor.theme;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftAdjacencyProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftFinalProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ThemeCache(PhantomReference<LevelReader> level, List<RiftTemplateProcessor> templateProcessors,
        List<RiftFinalProcessor> finalProcessors, List<RiftAdjacencyProcessor<?>> adjacencyProcessors) {

    public static final ThemeCache EMPTY = new ThemeCache(new PhantomReference<>(null, null), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList());

    public static ThemeCache fromProcessors(ServerLevel serverLevel, List<StructureProcessor> processors) {
        var newCache = new ThemeCache(new PhantomReference<>(serverLevel, null), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());
        for (var processor : processors) {
            var used = false;
            if (processor instanceof RiftTemplateProcessor riftTemplateProcessor) {
                newCache.templateProcessors().add(riftTemplateProcessor);
                used = true;
            }
            if (processor instanceof RiftFinalProcessor riftTemplateProcessor) {
                newCache.finalProcessors().add(riftTemplateProcessor);
                used = true;
            }
            if (processor instanceof RiftAdjacencyProcessor<?> replaceThisOrAdjacentRiftProcessor) {
                newCache.adjacencyProcessors().add(replaceThisOrAdjacentRiftProcessor);
                used = true;
            }
            if (!used) {
                WanderersOfTheRift.LOGGER.warn("incompatible processor type: {}", processor.getClass());
            }
        }
        return newCache;
    }
}
