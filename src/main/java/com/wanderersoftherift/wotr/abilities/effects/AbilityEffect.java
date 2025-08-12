package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbilityEffect {
    public static final Codec<AbilityEffect> DIRECT_CODEC = WotrRegistries.EFFECTS.byNameCodec()
            .dispatch(AbilityEffect::getCodec, Function.identity());

    private final AbilityTargeting targeting;
    private final List<AbilityEffect> effects;
    private final Optional<ParticleInfo> particles;

    public AbilityEffect(AbilityTargeting targeting, List<AbilityEffect> effects, Optional<ParticleInfo> particles) {
        this.targeting = targeting;
        this.effects = effects;
        this.particles = particles;
    }

    protected static <T extends AbilityEffect> Products.P3<RecordCodecBuilder.Mu<T>, AbilityTargeting, List<AbilityEffect>, Optional<ParticleInfo>> commonFields(
            RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                AbilityTargeting.DIRECT_CODEC.fieldOf("targeting").forGetter(AbilityEffect::getTargeting),
                Codec.list(AbilityEffect.DIRECT_CODEC)
                        .optionalFieldOf("effects", Collections.emptyList())
                        .forGetter(AbilityEffect::getEffects),
                Codec.optionalField("particles", ParticleInfo.CODEC.codec(), true)
                        .forGetter(AbilityEffect::getParticles)
        );
    }

    public abstract MapCodec<? extends AbilityEffect> getCodec();

    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        for (AbilityEffect effect : getEffects()) {
            effect.apply(user, blocks, context);
        }
    }

    // TODO consolidate this code below
    public void applyParticlesToUser(Entity user) {
        if (user != null && particles.isPresent() && particles.get().userParticle().isPresent()) {
            if (!user.level().isClientSide()) {
                ServerLevel level = (ServerLevel) user.level();
                applyParticlesToPos(level, user.position(), particles.get().userParticle().get());
            }
        }
    }

    public void applyParticlesToTarget(Entity target) {
        if (target != null && particles.isPresent() && particles.get().targetParticle().isPresent()) {
            if (!target.level().isClientSide()) {
                ServerLevel level = (ServerLevel) target.level();
                applyParticlesToPos(level, target.position(), particles.get().targetParticle().get());
            }
        }
    }

    protected void applyParticlesToTargetBlocks(Level level, List<BlockPos> blocks) {
        if (blocks != null && !blocks.isEmpty() && particles.isPresent()
                && particles.get().targetBlockParticle().isPresent()) {
            if (!level.isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) level;
                for (BlockPos pos : blocks) {
                    applyParticlesToPos(serverLevel, new Vec3(pos.getX(), pos.getY(), pos.getZ()),
                            particles.get().targetBlockParticle().get());
                }
            }
        }
    }

    public void applyParticlesToPos(ServerLevel level, Vec3 position, ParticleOptions particleOptions) {
        level.sendParticles(particleOptions, false, true, position.x, position.y + 1.5, position.z, 10, Math.random(),
                Math.random(), Math.random(), 2);
    }

    public AbilityTargeting getTargeting() {
        return targeting;
    }

    public List<AbilityEffect> getEffects() {
        return this.effects;
    }

    public Optional<ParticleInfo> getParticles() {
        return this.particles;
    }

    public Set<Holder<Attribute>> getApplicableAttributes() {
        return getEffects().stream()
                .map(AbilityEffect::getApplicableAttributes)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    /**
     * @param modifierEffect
     * @return Whether the modifier applies to this effect or its children
     */
    public final boolean isRelevant(AbstractModifierEffect modifierEffect) {
        if (isRelevantToThis(modifierEffect)) {
            return true;
        }
        if (getTargeting().isRelevant(modifierEffect)) {
            return true;
        }
        for (AbilityEffect child : effects) {
            if (child.isRelevant(modifierEffect)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether a given modifier effect is relevant to this effect. Used to determine what upgrades can apply
     * to abilities that include this effect.
     * 
     * @param modifierEffect
     * @return Whether the modifier is relevant to this effect
     */
    protected boolean isRelevantToThis(AbstractModifierEffect modifierEffect) {
        return false;
    };
}
