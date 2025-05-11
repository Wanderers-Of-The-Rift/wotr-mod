package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.StandardAbility;
import com.wanderersoftherift.wotr.abilities.effects.DamageEffect;
import com.wanderersoftherift.wotr.abilities.effects.HealEffect;
import com.wanderersoftherift.wotr.abilities.effects.MovementEffect;
import com.wanderersoftherift.wotr.abilities.effects.RelativeFrame;
import com.wanderersoftherift.wotr.abilities.effects.SimpleProjectileEffect;
import com.wanderersoftherift.wotr.abilities.effects.SoundEffect;
import com.wanderersoftherift.wotr.abilities.effects.predicate.EntitySentiment;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetPredicate;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.CubeAreaTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.SelfTargeting;
import com.wanderersoftherift.wotr.entity.projectile.SimpleProjectileConfig;
import com.wanderersoftherift.wotr.init.ModDamageTypes;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ModAbilityProvider {

    public static void bootstrapAbilities(BootstrapContext<AbstractAbility> bootstrap) {
        bootstrap.register(ResourceKey.create(RegistryEvents.ABILITY_REGISTRY, WanderersOfTheRift.id("dash")),
                new StandardAbility(WanderersOfTheRift.id("dash"),
                        ResourceLocation.parse("minecraft:textures/mob_effect/speed.png"), 200, 10,
                        List.of(new MovementEffect(new SelfTargeting(new TargetPredicate.Builder().build()),
                                Collections.emptyList(), Optional.empty(), new Vec3(0, 0.4F, 1),
                                RelativeFrame.TARGET_FACING),
                                new SoundEffect(new SelfTargeting(new TargetPredicate.Builder().build()),
                                        Collections.emptyList(), Optional.empty(),
                                        Holder.direct(SoundEvents.BREEZE_CHARGE)))));
        HolderGetter<DamageType> damageTypeRegistry = bootstrap.lookup(Registries.DAMAGE_TYPE);
        bootstrap.register(
                ResourceKey.create(RegistryEvents.ABILITY_REGISTRY, WanderersOfTheRift
                        .id("fireball")),
                new StandardAbility(WanderersOfTheRift.id("fireball_ability"),
                        ResourceLocation.parse("minecraft:textures/item/fire_charge.png"), 60, 10, List
                                .of(new SimpleProjectileEffect(
                                        new SelfTargeting(new TargetPredicate.Builder().build()),
                                        List.of(new DamageEffect(
                                                new CubeAreaTargeting(
                                                        new TargetPredicate.Builder().withSentiment(
                                                                EntitySentiment.NOT_FRIEND).build(),
                                                        12, true
                                                ), Collections.emptyList(), Optional.empty(), 10,
                                                damageTypeRegistry.getOrThrow(ModDamageTypes.FIRE_DAMAGE)
                                        )),
                                        Optional.of(new ParticleInfo(Optional.empty(), Optional.empty(),
                                                Optional.of(ParticleTypes.EXPLOSION)
                                        )), new SimpleProjectileConfig(
                                                1, 0, 1.6F, false, 0.05F, 0,
                                                new SimpleProjectileConfig.SimpleProjectileConfigRenderConfig(
                                                        ResourceLocation.parse("wotr:geo/ability/fireball.geo.json"),
                                                        ResourceLocation.parse("wotr:textures/ability/fireball.png"),
                                                        ResourceLocation.parse(
                                                                "wotr:animations/ability/fireball.animations.json")
                                                )
                                        ))
                                )
                )
        );
        bootstrap.register(
                ResourceKey.create(RegistryEvents.ABILITY_REGISTRY, WanderersOfTheRift
                        .id("icicles")),
                new StandardAbility(WanderersOfTheRift.id("icicles_ability"),
                        ResourceLocation.parse("wotr:textures/ability/icon/icicle.png"), 10, 2, List
                                .of(new SimpleProjectileEffect(
                                        new SelfTargeting(new TargetPredicate.Builder().build()),
                                        List.of(new DamageEffect(
                                                new SelfTargeting(
                                                        new TargetPredicate.Builder().withSentiment(
                                                                EntitySentiment.NOT_FRIEND).build()
                                                ), Collections.emptyList(), Optional.empty(), 2,
                                                damageTypeRegistry.getOrThrow(ModDamageTypes.ICE_DAMAGE)
                                        )), Optional.empty(), new SimpleProjectileConfig(
                                                3, 1, 1.7F, false, 0.05F, 20,
                                                new SimpleProjectileConfig.SimpleProjectileConfigRenderConfig(
                                                        ResourceLocation.parse("wotr:geo/ability/icicle.geo.json"),
                                                        ResourceLocation.parse("wotr:textures/ability/icicle.png"),
                                                        ResourceLocation.parse(
                                                                "wotr:animations/ability/icicle.animations.json")
                                                )
                                        ))
                                )
                )
        );
        bootstrap.register(ResourceKey.create(RegistryEvents.ABILITY_REGISTRY, WanderersOfTheRift.id("heal")),
                new StandardAbility(WanderersOfTheRift.id("heal"),
                        ResourceLocation.parse("minecraft:textures/mob_effect/regeneration.png"), 200, 10,
                        List.of(new HealEffect(new CubeAreaTargeting(
                                new TargetPredicate.Builder().withSentiment(EntitySentiment.NOT_FOE).build(), 2, true),
                                Collections.emptyList(), Optional.empty(), 2),
                                new SoundEffect(new SelfTargeting(new TargetPredicate.Builder().build()),
                                        Collections.emptyList(), Optional.empty(),
                                        Holder.direct(SoundEvents.SPLASH_POTION_BREAK)))));

    }
}
