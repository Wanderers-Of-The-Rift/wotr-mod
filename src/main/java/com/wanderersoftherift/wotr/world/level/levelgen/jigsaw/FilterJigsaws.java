package com.wanderersoftherift.wotr.world.level.levelgen.jigsaw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Iterator;
import java.util.List;

public record FilterJigsaws(String namespace, String pathPattern) implements JigsawListProcessor {
    public static final MapCodec<FilterJigsaws> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("namespace").forGetter(FilterJigsaws::namespace),
            Codec.STRING.fieldOf("path_prefix").forGetter(FilterJigsaws::pathPattern)
    ).apply(instance, FilterJigsaws::new));

    @Override
    public MapCodec<? extends JigsawListProcessor> codec() {
        return CODEC;
    }

    @Override
    public void processJigsaws(List<StructureTemplate.JigsawBlockInfo> jigsaws, RandomSource random) {
        Iterator<StructureTemplate.JigsawBlockInfo> iterator = jigsaws.iterator();
        while (iterator.hasNext()) {
            var pool = iterator.next().pool();
            if (pool.getPath().contains(pathPattern) && namespace.equals(pool.getNamespace())) {
                iterator.remove();
            }
        }
    }
}
