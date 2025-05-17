package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.DefaultOutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.OutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.StateOutputBlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrOutputBlockStateTypes {

    public static final DeferredRegister<MapCodec<? extends OutputBlockState>> OUTPUT_BLOCKSTATE_TYPES = DeferredRegister
            .create(WotrRegistries.OUTPUT_BLOCKSTATE_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends OutputBlockState>> DEFAULT_BLOCKSTATE = OUTPUT_BLOCKSTATE_TYPES
            .register("default", () -> DefaultOutputBlockState.CODEC);

    public static final Supplier<MapCodec<? extends OutputBlockState>> STATE_BLOCKSTATE = OUTPUT_BLOCKSTATE_TYPES
            .register("blockstate", () -> StateOutputBlockState.CODEC);

}
