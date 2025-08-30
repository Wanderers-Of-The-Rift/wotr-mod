package com.wanderersoftherift.wotr.init.ability;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.InstantAbility;
import com.wanderersoftherift.wotr.abilities.PersistentAbility;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrAbilityTypes {

    public static final DeferredRegister<MapCodec<? extends Ability>> ABILITY_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.ABILITY_TYPES, WanderersOfTheRift.MODID);

    /*
     * This is where we register the different "types" of abilities that can be created and configured using datapacks
     */
    public static final Supplier<MapCodec<? extends Ability>> INSTANT = ABILITY_TYPES.register("instant",
            () -> InstantAbility.CODEC);

    public static final Supplier<MapCodec<? extends Ability>> PERSISTENT = ABILITY_TYPES.register("persistent",
            () -> PersistentAbility.CODEC);

}
