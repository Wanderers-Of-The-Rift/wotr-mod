package com.wanderersoftherift.wotr.world.level.levelgen.jigsaw;

import com.wanderersoftherift.wotr.util.JavaRandomFromRandomSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Collections;
import java.util.List;

public record ShuffleJigsaws() implements JigsawListProcessor {
    @Override
    public void processJigsaws(List<StructureTemplate.JigsawBlockInfo> jigsaws, RandomSource random) {
        Collections.shuffle(jigsaws, JavaRandomFromRandomSource.of(random));
    }
}
