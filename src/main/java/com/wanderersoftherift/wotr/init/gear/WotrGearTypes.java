package com.wanderersoftherift.wotr.init.gear;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.GearAbility;
import com.wanderersoftherift.wotr.abilities.StandardAbility;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.gear.AbstractGear;
import com.wanderersoftherift.wotr.item.gear.MainHandGear;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrGearTypes {

    public static final DeferredRegister<MapCodec<? extends AbstractGear>> GEAR_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.GEAR_TYPES, WanderersOfTheRift.MODID);

    /*
     * This is where we register the different "types" of gear that can be created and configured using datapacks
     */
    public static final Supplier<MapCodec<? extends AbstractGear>> MAIN_HAND_GEAR = GEAR_TYPES
            .register("main_hand", () -> MainHandGear.CODEC);


}
