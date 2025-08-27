package com.wanderersoftherift.wotr.world.level.levelgen.jigsaw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;

public interface JigsawListProcessor {

    Codec<JigsawListProcessor> CODEC = WotrRegistries.JIGSAW_LIST_PROCESSOR_TYPES.byNameCodec()
            .dispatch(fac -> fac.codec(), codec -> codec);

    MapCodec<? extends JigsawListProcessor> codec();

    void processJigsaws(List<StructureTemplate.JigsawBlockInfo> jigsaws, RandomSource random);
}