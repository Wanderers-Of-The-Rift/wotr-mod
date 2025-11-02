package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.PolynomialRiftParameter;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.PowRiftParameter;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RandomRangeRiftParameter;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RegisteredRiftParameter;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.SumRiftParameter;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.TableRiftParameter;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.TierRiftParameter;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrRiftParameterTypes {
    public static final DeferredRegister<MapCodec<? extends RegisteredRiftParameter>> PARAMETER_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.RIFT_PARAMETER_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<PolynomialRiftParameter>> POLYNOMIAL = PARAMETER_TYPES.register(
            "polynomial", () -> PolynomialRiftParameter.CODEC);
    public static final Supplier<MapCodec<SumRiftParameter>> SUM = PARAMETER_TYPES.register(
            "sum", () -> SumRiftParameter.CODEC);
    public static final Supplier<MapCodec<PowRiftParameter>> POWER = PARAMETER_TYPES.register(
            "power", () -> PowRiftParameter.CODEC);
    public static final Supplier<MapCodec<TierRiftParameter>> TIER = PARAMETER_TYPES.register(
            "tier", () -> TierRiftParameter.CODEC);
    public static final Supplier<MapCodec<TableRiftParameter>> TABLE = PARAMETER_TYPES.register(
            "table", () -> TableRiftParameter.CODEC);
    public static final Supplier<MapCodec<RandomRangeRiftParameter>> RANDOM_RANGE = PARAMETER_TYPES.register(
            "random_range", () -> RandomRangeRiftParameter.CODEC);
}
