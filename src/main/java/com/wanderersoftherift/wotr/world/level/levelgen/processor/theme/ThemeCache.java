package com.wanderersoftherift.wotr.world.level.levelgen.processor.theme;

import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftAdjacencyProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftFinalProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import net.minecraft.world.level.LevelReader;

import java.lang.ref.PhantomReference;
import java.util.Collections;
import java.util.List;

record ThemeCache(PhantomReference<LevelReader> level, List<RiftTemplateProcessor> templateProcessors,
        List<RiftFinalProcessor> finalProcessors, List<RiftAdjacencyProcessor<?>> adjacencyProcessors) {
    public static final ThemeCache EMPTY = new ThemeCache(new PhantomReference<>(null, null), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList());
}
