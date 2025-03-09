package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.modifier.effect.AbstractModifierEffect;
import com.dimensiondelvers.dimensiondelvers.modifier.effect.AttributeModifierEffect;
import com.dimensiondelvers.dimensiondelvers.world.level.levelgen.processor.output.DefaultOutputBlockState;
import com.dimensiondelvers.dimensiondelvers.world.level.levelgen.processor.output.OutputBlockState;
import com.dimensiondelvers.dimensiondelvers.world.level.levelgen.processor.output.StateOutputBlockState;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ModOutputBlockStateTypes {

    public static final ResourceKey<Registry<MapCodec<? extends OutputBlockState>>> OUTPUT_BLOCKSTATE_TYPE_KEY = ResourceKey.createRegistryKey(DimensionDelvers.id("output_blockstate_type"));
    public static final Registry<MapCodec<? extends OutputBlockState>> OUTPUT_BLOCKSTATE_TYPE_REGISTRY = new RegistryBuilder<>(OUTPUT_BLOCKSTATE_TYPE_KEY).create();

    public static final DeferredRegister<MapCodec<? extends OutputBlockState>> OUTPUT_BLOCKSTATE_TYPES = DeferredRegister.create(OUTPUT_BLOCKSTATE_TYPE_REGISTRY, DimensionDelvers.MODID);

    public static final Supplier<MapCodec<? extends OutputBlockState>> DEFAULT_BLOCKSTATE = OUTPUT_BLOCKSTATE_TYPES.register(
            "default", () -> DefaultOutputBlockState.CODEC
    );

    public static final Supplier<MapCodec<? extends OutputBlockState>> STATE_BLOCKSTATE = OUTPUT_BLOCKSTATE_TYPES.register(
            "blockstate", () -> StateOutputBlockState.CODEC
    );

}
