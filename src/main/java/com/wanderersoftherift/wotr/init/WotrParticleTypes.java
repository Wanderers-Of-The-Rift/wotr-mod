package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class WotrParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
            .create(Registries.PARTICLE_TYPE, WanderersOfTheRift.MODID);

    public static final Supplier<ParticleType<SimpleParticleType>> SWEEP_ATTACK_RIGHT = PARTICLE_TYPES.register(
            "sweep_attack_right", () -> new SimpleParticleType(true));

    public static final Supplier<ParticleType<SimpleParticleType>> SWEEP_ATTACK_OVERHEAD = PARTICLE_TYPES.register(
            "sweep_attack_overhead", () -> new SimpleParticleType(true));

    @SubscribeEvent
    private static void registerFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SWEEP_ATTACK_RIGHT.get(), AttackSweepParticle.Provider::new);
        event.registerSpriteSet(SWEEP_ATTACK_OVERHEAD.get(), AttackSweepParticle.Provider::new);
    }
}
