package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.predicate.SurfacePredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class WotrBlockPredicates {
    public static final DeferredRegister<BlockPredicateType<?>> BLOCK_PREDICATES = DeferredRegister
            .create(BuiltInRegistries.BLOCK_PREDICATE_TYPE, WanderersOfTheRift.MODID);

    public static final Supplier<BlockPredicateType<?>> SURFACE = BLOCK_PREDICATES.register("surface",
            () -> SurfacePredicate.TYPE);
}
