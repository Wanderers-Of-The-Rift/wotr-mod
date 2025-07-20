package com.wanderersoftherift.wotr.world.level.levelgen.jigsaw;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Iterator;
import java.util.List;

public record FilterJigsaws(String namespace, String pathPattern) implements JigsawListProcessor {
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
