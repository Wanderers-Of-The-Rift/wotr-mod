package com.wanderersoftherift.wotr.world.level.levelgen.jigsaw;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.util.JavaRandomFromRandomSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Collections;
import java.util.List;

public record ShuffleJigsaws() implements JigsawListProcessor {
    public static final ShuffleJigsaws INSTANCE = new ShuffleJigsaws();
    public static final MapCodec<ShuffleJigsaws> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.point(INSTANCE));

    @Override
    public MapCodec<? extends JigsawListProcessor> codec() {
        return CODEC;
    }

    @Override
    public void processJigsaws(List<StructureTemplate.JigsawBlockInfo> jigsaws, RandomSource random) {
        Collections.shuffle(jigsaws, JavaRandomFromRandomSource.of(random));
    }
}
