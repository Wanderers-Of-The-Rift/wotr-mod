package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.input.DefaultInputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.input.InputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.input.StateInputBlockState;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ModInputBlockStateTypes {

    public static final ResourceKey<Registry<MapCodec<? extends InputBlockState>>> INPUT_BLOCKSTATE_TYPE_KEY = ResourceKey
            .createRegistryKey(WanderersOfTheRift.id("input_blockstate_type"));
    public static final Registry<MapCodec<? extends InputBlockState>> INPUT_BLOCKSTATE_TYPE_REGISTRY = new RegistryBuilder<>(
            INPUT_BLOCKSTATE_TYPE_KEY).create();

    public static final DeferredRegister<MapCodec<? extends InputBlockState>> INPUT_BLOCKSTATE_TYPES = DeferredRegister
            .create(INPUT_BLOCKSTATE_TYPE_REGISTRY, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends InputBlockState>> DEFAULT_BLOCKSTATE = INPUT_BLOCKSTATE_TYPES
            .register("default", () -> DefaultInputBlockState.CODEC);

    public static final Supplier<MapCodec<? extends InputBlockState>> STATE_BLOCKSTATE = INPUT_BLOCKSTATE_TYPES
            .register("blockstate", () -> StateInputBlockState.CODEC);

}
