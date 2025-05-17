package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.input.DefaultInputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.input.InputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.input.StateInputBlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrInputBlockStateTypes {

    public static final DeferredRegister<MapCodec<? extends InputBlockState>> INPUT_BLOCKSTATE_TYPES = DeferredRegister
            .create(WotrRegistries.INPUT_BLOCKSTATE_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends InputBlockState>> DEFAULT_BLOCKSTATE = INPUT_BLOCKSTATE_TYPES
            .register("default", () -> DefaultInputBlockState.CODEC);

    public static final Supplier<MapCodec<? extends InputBlockState>> STATE_BLOCKSTATE = INPUT_BLOCKSTATE_TYPES
            .register("blockstate", () -> StateInputBlockState.CODEC);

}
