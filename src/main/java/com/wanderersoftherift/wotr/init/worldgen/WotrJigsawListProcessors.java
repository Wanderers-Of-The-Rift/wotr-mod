package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.FilterJigsaws;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.ReplaceJigsaws;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.ReplaceJigsawsBulk;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.ShuffleJigsaws;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrJigsawListProcessors {
    public static final DeferredRegister<MapCodec<? extends JigsawListProcessor>> JIGSAW_LIST_PROCESSORS = DeferredRegister
            .create(WotrRegistries.JIGSAW_LIST_PROCESSOR_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<ShuffleJigsaws>> SHUFFLE_JIGSAWS = JIGSAW_LIST_PROCESSORS.register("shuffle",
            () -> ShuffleJigsaws.CODEC);

    public static final Supplier<MapCodec<ReplaceJigsaws>> REPLACE_JIGSAWS = JIGSAW_LIST_PROCESSORS.register("replace",
            () -> ReplaceJigsaws.CODEC);

    public static final Supplier<MapCodec<FilterJigsaws>> FILTER_JIGSAWS = JIGSAW_LIST_PROCESSORS.register("filter",
            () -> FilterJigsaws.CODEC);
    public static final Supplier<MapCodec<ReplaceJigsawsBulk>> FILTER_JIGSAWS_BOLK = JIGSAW_LIST_PROCESSORS
            .register("replace_bulk", () -> ReplaceJigsawsBulk.CODEC);

}
