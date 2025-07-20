package com.wanderersoftherift.wotr.world.level.levelgen.jigsaw;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;

public interface JigsawListProcessor {
    void processJigsaws(List<StructureTemplate.JigsawBlockInfo> jigsaws, RandomSource random);
}