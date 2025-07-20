package com.wanderersoftherift.wotr.world.level.levelgen.jigsaw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;

public record ReplaceJigsaws(ResourceLocation oldPool, ResourceLocation newPool, float chance)
        implements JigsawListProcessor {

    public static final MapCodec<ReplaceJigsaws> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("match_pool").forGetter(ReplaceJigsaws::oldPool),
            ResourceLocation.CODEC.fieldOf("new_pool").forGetter(ReplaceJigsaws::newPool),
            Codec.FLOAT.fieldOf("chance").forGetter(ReplaceJigsaws::chance)
    ).apply(instance, ReplaceJigsaws::new));

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
                                    var target, var placementPriority, var selectionPriority
                    ) && pool.equals(oldPool) && random.nextFloat() < chance) {
                // spotless:on
                jigsaws.set(i, new StructureTemplate.JigsawBlockInfo(info, jointType, name, newPool, target,
                        placementPriority, selectionPriority));
            }
        }
    }
}
