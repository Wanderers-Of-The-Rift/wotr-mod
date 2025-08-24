package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilitySource;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrAbilitySourceTypes {
    public static final DeferredRegister<DualCodec<? extends AbilitySource>> ABILITY_SOURCE = DeferredRegister
            .create(WotrRegistries.ABILITY_SOURCES, WanderersOfTheRift.MODID);

    public static final Supplier<DualCodec<? extends AbilitySource>> MODIFIER_SOURCE = ABILITY_SOURCE
            .register("modifier", () -> AbilitySource.ModifierAbilitySource.TYPE);
    public static final Supplier<DualCodec<? extends AbilitySource>> MAIN_SOURCE = ABILITY_SOURCE.register("main",
            () -> AbilitySource.MainAbilitySource.TYPE);
}
