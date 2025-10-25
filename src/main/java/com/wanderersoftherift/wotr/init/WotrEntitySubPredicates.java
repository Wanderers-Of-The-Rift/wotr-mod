package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.predicate.AndPredicate;
import com.wanderersoftherift.wotr.entity.predicate.GuildRankPredicate;
import com.wanderersoftherift.wotr.entity.predicate.OrPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrEntitySubPredicates {
    public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> ENTITY_SUBPREDICATES = DeferredRegister
            .create(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends EntitySubPredicate>> AND = ENTITY_SUBPREDICATES.register("and",
            () -> AndPredicate.CODEC);
    public static final Supplier<MapCodec<? extends EntitySubPredicate>> OR = ENTITY_SUBPREDICATES.register("or",
            () -> OrPredicate.CODEC);
    public static final Supplier<MapCodec<? extends EntitySubPredicate>> GUILD_RANK = ENTITY_SUBPREDICATES
            .register("guild_rank", () -> GuildRankPredicate.CODEC);

}
