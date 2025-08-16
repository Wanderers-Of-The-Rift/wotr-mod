package com.wanderersoftherift.wotr.world.level.levelgen.jigsaw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ReplaceJigsawsBulk(HashMap<ResourceLocation, Replacement> replacementMap) implements JigsawListProcessor {
    public static final MapCodec<ReplaceJigsawsBulk> CODEC = Codec
            .unboundedMap(ResourceLocation.CODEC, Replacement.CODEC)
            .xmap(ReplaceJigsawsBulk::new, ReplaceJigsawsBulk::replacementMap)
            .fieldOf("values");

    public ReplaceJigsawsBulk(Map<ResourceLocation, Replacement> replacementMap) {
        this(new HashMap<>(replacementMap));
    }

    @Override
    public MapCodec<? extends JigsawListProcessor> codec() {
        return CODEC;
    }

    @Override
    public void processJigsaws(List<StructureTemplate.JigsawBlockInfo> jigsaws, RandomSource random) {

        for (int i = 0; i < jigsaws.size(); i++) {
            // spotless:off
            if (jigsaws.get(i)
                    instanceof StructureTemplate.JigsawBlockInfo(
                        var info, var jointType, var name, var pool,
                        var target, var placementPriority, var selectionPriority)) {
                // spotless:on
                if (replacementMap.get(pool) instanceof Replacement(var newPool, var chance)
                        && random.nextFloat() < chance) {
                    jigsaws.set(i, new StructureTemplate.JigsawBlockInfo(info, jointType, name, newPool, target,
                            placementPriority, selectionPriority));
                }
            }
        }
    }

    public record Replacement(ResourceLocation newPool, float chance) {

        public static final Codec<Replacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("new_pool").forGetter(Replacement::newPool),
                Codec.FLOAT.fieldOf("chance").forGetter(Replacement::chance)
        ).apply(instance, Replacement::new));
    }
}
