package com.wanderersoftherift.wotr.init.ability;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.effects.AbilityEffect;
import com.wanderersoftherift.wotr.abilities.effects.ApplyStatusEffect;
import com.wanderersoftherift.wotr.abilities.effects.AttachEffect;
import com.wanderersoftherift.wotr.abilities.effects.BlankEffect;
import com.wanderersoftherift.wotr.abilities.effects.BreakBlockEffect;
import com.wanderersoftherift.wotr.abilities.effects.DamageEffect;
import com.wanderersoftherift.wotr.abilities.effects.HealEffect;
import com.wanderersoftherift.wotr.abilities.effects.MovementEffect;
import com.wanderersoftherift.wotr.abilities.effects.ProjectileEffect;
import com.wanderersoftherift.wotr.abilities.effects.SimpleProjectileEffect;
import com.wanderersoftherift.wotr.abilities.effects.SoundEffect;
import com.wanderersoftherift.wotr.abilities.effects.SummonEffect;
import com.wanderersoftherift.wotr.abilities.effects.TargetEffect;
import com.wanderersoftherift.wotr.abilities.effects.TeleportEffect;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrEffects {

    public static final DeferredRegister<MapCodec<? extends AbilityEffect>> EFFECTS = DeferredRegister
            .create(WotrRegistries.Keys.EFFECTS, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends AttachEffect>> ATTACH_EFFECT = EFFECTS.register("attach_effect",
            () -> AttachEffect.CODEC);
    public static final Supplier<MapCodec<? extends SoundEffect>> SOUND_EFFECT = EFFECTS.register("sound_effect",
            () -> SoundEffect.CODEC);
    public static final Supplier<MapCodec<? extends BlankEffect>> BLANK_EFFECT = EFFECTS.register("blank_effect",
            () -> BlankEffect.CODEC);
    public static final Supplier<MapCodec<? extends SimpleProjectileEffect>> SIMPLE_PROJECTILE_EFFECT = EFFECTS
            .register("simple_projectile_effect", () -> SimpleProjectileEffect.CODEC);
    public static final Supplier<MapCodec<? extends ProjectileEffect>> PROJECTILE_EFFECT = EFFECTS
            .register("projectile_effect", () -> ProjectileEffect.CODEC);
    public static final Supplier<MapCodec<? extends SummonEffect>> SUMMON_EFFECT = EFFECTS.register("summon_effect",
            () -> SummonEffect.CODEC);
    public static final Supplier<MapCodec<? extends BreakBlockEffect>> BREAK_EFFECT = EFFECTS.register("break_effect",
            () -> BreakBlockEffect.CODEC);
    public static final Supplier<MapCodec<? extends ApplyStatusEffect>> STATUS_EFFECT = EFFECTS
            .register("status_effect", () -> ApplyStatusEffect.CODEC);
    public static final Supplier<MapCodec<? extends TeleportEffect>> TELE_EFFECT = EFFECTS.register("teleport_effect",
            () -> TeleportEffect.CODEC);
    public static final Supplier<MapCodec<? extends MovementEffect>> MOVEMENT_EFFECT = EFFECTS
            .register("movement_effect", () -> MovementEffect.CODEC);
    public static final Supplier<MapCodec<? extends DamageEffect>> DAMAGE_EFFECT = EFFECTS.register("damage_effect",
            () -> DamageEffect.CODEC);
    public static final Supplier<MapCodec<? extends AbilityEffect>> HEAL_EFFECT = EFFECTS.register("heal_effect",
            () -> HealEffect.CODEC);
    public static final Supplier<MapCodec<? extends AbilityEffect>> TARGET_EFFECT = EFFECTS.register("target_effect",
            () -> TargetEffect.CODEC);
}
