package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.StandardAbility;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ModAbilityTypes {

    public static final ResourceKey<Registry<MapCodec<? extends AbstractAbility>>> ABILITY_TYPES_KEY = ResourceKey
            .createRegistryKey(WanderersOfTheRift.id("ability_types"));
    public static final Registry<MapCodec<? extends AbstractAbility>> ABILITY_TYPES_REGISTRY = new RegistryBuilder<>(
            ABILITY_TYPES_KEY).create();
    public static final DeferredRegister<MapCodec<? extends AbstractAbility>> ABILITY_TYPES = DeferredRegister
            .create(ABILITY_TYPES_KEY, WanderersOfTheRift.MODID);

    /*
     * This is where we register the different "types" of abilities that can be created and configured using datapacks
     */
    public static final Supplier<MapCodec<? extends AbstractAbility>> STANDARD_ABILITY_TYPE = ABILITY_TYPES
            .register("standard_ability", () -> StandardAbility.CODEC);

}
