package com.wanderersoftherift.wotr.init.ability;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.StandardAbility;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.gear.GearAbility;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrAbilityTypes {

    public static final DeferredRegister<MapCodec<? extends AbstractAbility>> ABILITY_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.ABILITY_TYPES, WanderersOfTheRift.MODID);

    /*
     * This is where we register the different "types" of abilities that can be created and configured using datapacks
     */
    public static final Supplier<MapCodec<? extends AbstractAbility>> STANDARD_ABILITY_TYPE = ABILITY_TYPES
            .register("standard_ability", () -> StandardAbility.CODEC);
    public static final Supplier<MapCodec<? extends AbstractAbility>> GEAR_ABILITY_TYPE = ABILITY_TYPES
            .register("gear_ability", () -> GearAbility.CODEC);

}
