package com.wanderersoftherift.wotr.init.ability;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import com.wanderersoftherift.wotr.abilities.requirement.FoodLevelCost;
import com.wanderersoftherift.wotr.abilities.requirement.LifeCost;
import com.wanderersoftherift.wotr.abilities.requirement.ManaCost;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrAbilityRequirementTypes {

    public static final DeferredRegister<MapCodec<? extends AbilityRequirement>> ABILITY_REQUIREMENT_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.ABILITY_REQUIREMENT_TYPES, WanderersOfTheRift.MODID);

    /*
     * This is where we register the different "types" of ability requirements that can be created and configured using
     * datapacks
     */
    public static final Supplier<MapCodec<? extends AbilityRequirement>> MANA = ABILITY_REQUIREMENT_TYPES
            .register("mana", () -> ManaCost.CODEC);

    public static final Supplier<MapCodec<? extends AbilityRequirement>> FOOD_LEVEL = ABILITY_REQUIREMENT_TYPES
            .register("food_level", () -> FoodLevelCost.CODEC);

    public static final Supplier<MapCodec<? extends AbilityRequirement>> LIFE = ABILITY_REQUIREMENT_TYPES
            .register("life", () -> LifeCost.CODEC);

}
