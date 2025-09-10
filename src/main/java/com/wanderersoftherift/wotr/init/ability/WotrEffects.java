package com.wanderersoftherift.wotr.init.ability;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.effects.AbilityEffect;
import com.wanderersoftherift.wotr.abilities.effects.ApplyStatusEffect;
import com.wanderersoftherift.wotr.abilities.effects.AttachEffect;
import com.wanderersoftherift.wotr.abilities.effects.BreakBlockEffect;
import com.wanderersoftherift.wotr.abilities.effects.ConditionalEffect;
import com.wanderersoftherift.wotr.abilities.effects.DamageEffect;
import com.wanderersoftherift.wotr.abilities.effects.DetachOwnEffect;
import com.wanderersoftherift.wotr.abilities.effects.HealEffect;
import com.wanderersoftherift.wotr.abilities.effects.MovementEffect;
import com.wanderersoftherift.wotr.abilities.effects.NoopEffect;
import com.wanderersoftherift.wotr.abilities.effects.ParticleEffect;
import com.wanderersoftherift.wotr.abilities.effects.ProjectileEffect;
import com.wanderersoftherift.wotr.abilities.effects.SimpleProjectileEffect;
import com.wanderersoftherift.wotr.abilities.effects.SoundEffect;
import com.wanderersoftherift.wotr.abilities.effects.SummonEffect;
import com.wanderersoftherift.wotr.abilities.effects.TargetingEffect;
import com.wanderersoftherift.wotr.abilities.effects.TeleportEffect;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrEffects {

    public static final DeferredRegister<MapCodec<? extends AbilityEffect>> EFFECTS = DeferredRegister
            .create(WotrRegistries.Keys.EFFECTS, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends AttachEffect>> ATTACH = EFFECTS.register("attach",
            () -> AttachEffect.CODEC);
    public static final Supplier<MapCodec<? extends DetachOwnEffect>> DETACH_OWN = EFFECTS.register("detach_own",
            () -> DetachOwnEffect.CODEC);
    public static final Supplier<MapCodec<? extends SoundEffect>> SOUND = EFFECTS.register("sound",
            () -> SoundEffect.CODEC);
    public static final Supplier<MapCodec<? extends NoopEffect>> NOOP = EFFECTS.register("noop",
            () -> NoopEffect.CODEC);
    public static final Supplier<MapCodec<? extends SimpleProjectileEffect>> SIMPLE_PROJECTILE = EFFECTS
            .register("simple_projectile", () -> SimpleProjectileEffect.CODEC);
    public static final Supplier<MapCodec<? extends ProjectileEffect>> PROJECTILE = EFFECTS.register("projectile",
            () -> ProjectileEffect.CODEC);
    public static final Supplier<MapCodec<? extends SummonEffect>> SUMMON = EFFECTS.register("summon",
            () -> SummonEffect.CODEC);
    public static final Supplier<MapCodec<? extends BreakBlockEffect>> BREAK = EFFECTS.register("break",
            () -> BreakBlockEffect.CODEC);
    public static final Supplier<MapCodec<? extends ApplyStatusEffect>> STATUS = EFFECTS.register("status",
            () -> ApplyStatusEffect.CODEC);
    public static final Supplier<MapCodec<? extends TeleportEffect>> TELEPORT = EFFECTS.register("teleport",
            () -> TeleportEffect.CODEC);
    public static final Supplier<MapCodec<? extends MovementEffect>> MOVEMENT = EFFECTS.register("movement",
            () -> MovementEffect.CODEC);
    public static final Supplier<MapCodec<? extends DamageEffect>> DAMAGE = EFFECTS.register("damage",
            () -> DamageEffect.CODEC);
    public static final Supplier<MapCodec<? extends AbilityEffect>> HEAL = EFFECTS.register("heal",
            () -> HealEffect.CODEC);
    public static final Supplier<MapCodec<? extends AbilityEffect>> TARGETING = EFFECTS.register("targeting",
            () -> TargetingEffect.CODEC);
    public static final Supplier<MapCodec<? extends AbilityEffect>> CONDITIONAL = EFFECTS.register("conditional",
            () -> ConditionalEffect.CODEC);
    public static final Supplier<MapCodec<? extends AbilityEffect>> PARTICLE = EFFECTS.register("particle",
            () -> ParticleEffect.CODEC);
}
