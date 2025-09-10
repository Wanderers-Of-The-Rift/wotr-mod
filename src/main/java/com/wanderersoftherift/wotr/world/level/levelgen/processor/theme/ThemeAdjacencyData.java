package com.wanderersoftherift.wotr.world.level.levelgen.processor.theme;

import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftAdjacencyProcessor;

import java.util.List;

public record ThemeAdjacencyData(List<? extends RiftAdjacencyProcessor.ProcessorDataPair<?>> list) {
}
