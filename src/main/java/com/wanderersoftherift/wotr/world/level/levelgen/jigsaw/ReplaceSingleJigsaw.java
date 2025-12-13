package com.wanderersoftherift.wotr.world.level.levelgen.jigsaw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Replaces up to one pool in a generatable
 * 
 * @param replacePools A map of what pools can be replaced, and by what pool
 * @param chance       The chance a generatable will have a pool replaced
 */
public record ReplaceSingleJigsaw(Map<ResourceLocation, ResourceLocation> replacePools, float chance)
        implements JigsawListProcessor {

    public static final MapCodec<ReplaceSingleJigsaw> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC)
                    .fieldOf("replace_pools")
                    .forGetter(ReplaceSingleJigsaw::replacePools),
            Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(ReplaceSingleJigsaw::chance)
    ).apply(instance, ReplaceSingleJigsaw::new));

    @Override
    public MapCodec<? extends JigsawListProcessor> codec() {
        return CODEC;
    }

    @Override
    public void processJigsaws(List<StructureTemplate.JigsawBlockInfo> jigsaws, RandomSource random) {
        if (jigsaws.isEmpty() || random.nextFloat() >= chance) {
            return;
        }
        int[] indices = IntStream.range(0, jigsaws.size()).filter(index -> {
            var jigsaw = jigsaws.get(index);
            var pool = jigsaw.pool();
            return replacePools.containsKey(pool);
        }).toArray();
        if (indices.length > 0) {
            int index = indices[random.nextInt(indices.length)];
            StructureTemplate.JigsawBlockInfo previous = jigsaws.get(index);
            jigsaws.set(index, jigsaws.getFirst());
            jigsaws.set(0,
                    new StructureTemplate.JigsawBlockInfo(previous.info(), previous.jointType(), previous.name(),
                            replacePools.get(previous.pool()), previous.target(), previous.placementPriority(),
                            previous.selectionPriority()));
        }
    }
}
