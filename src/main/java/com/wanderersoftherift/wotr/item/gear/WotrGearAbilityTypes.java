package com.wanderersoftherift.wotr.item.gear;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.StandardAbility;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrGearAbilityTypes {

    public static final DeferredRegister<MapCodec<? extends AbstractGearAbility>> GEAR_ABILITY_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.GEAR_ABILITY_TYPES, WanderersOfTheRift.MODID);

    /*
     * Used for declaring the different abilities tied to gear pieces
     */
    public static final Supplier<MapCodec<? extends AbstractGearAbility>> BASIC_GEAR_ABILITY = GEAR_ABILITY_TYPES
            .register("gear_basic", () -> BasicGearAbility.CODEC);
    public static final Supplier<MapCodec<? extends AbstractGearAbility>> SECONDARY_GEAR_ABILITY = GEAR_ABILITY_TYPES
            .register("gear_secondary", () -> SecondaryGearAbility.CODEC);

}
