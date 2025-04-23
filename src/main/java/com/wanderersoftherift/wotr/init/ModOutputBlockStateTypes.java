package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.DefaultOutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.OutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.StateOutputBlockState;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ModOutputBlockStateTypes {

    public static final ResourceKey<Registry<MapCodec<? extends OutputBlockState>>> OUTPUT_BLOCKSTATE_TYPE_KEY = ResourceKey
            .createRegistryKey(WanderersOfTheRift.id("output_blockstate_type"));
    public static final Registry<MapCodec<? extends OutputBlockState>> OUTPUT_BLOCKSTATE_TYPE_REGISTRY = new RegistryBuilder<>(
            OUTPUT_BLOCKSTATE_TYPE_KEY).create();

    public static final DeferredRegister<MapCodec<? extends OutputBlockState>> OUTPUT_BLOCKSTATE_TYPES = DeferredRegister
            .create(OUTPUT_BLOCKSTATE_TYPE_REGISTRY, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends OutputBlockState>> DEFAULT_BLOCKSTATE = OUTPUT_BLOCKSTATE_TYPES
            .register("default", () -> DefaultOutputBlockState.CODEC);

    public static final Supplier<MapCodec<? extends OutputBlockState>> STATE_BLOCKSTATE = OUTPUT_BLOCKSTATE_TYPES
            .register("blockstate", () -> StateOutputBlockState.CODEC);

}
