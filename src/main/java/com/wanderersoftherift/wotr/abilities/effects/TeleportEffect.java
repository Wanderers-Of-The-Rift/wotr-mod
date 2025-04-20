package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.effects.util.TeleportInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbstractTargeting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;

public class TeleportEffect extends AbstractEffect {
    public static final MapCodec<TeleportEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbstractEffect.commonFields(instance)
                    .and(TeleportInfo.CODEC.fieldOf("tele_info").forGetter(TeleportEffect::getTeleportInfo))
                    .apply(instance, TeleportEffect::new));

    private TeleportInfo teleInfo;

    // TODO look into handling different types of teleports and better handle relative motion
    // TODO also look into teleporting "towards" a location to find the nearest safe spot that isnt the exact location

    public TeleportEffect(AbstractTargeting targeting, List<AbstractEffect> effects, Optional<ParticleInfo> particles,
            TeleportInfo teleInfo) {
        super(targeting, effects, particles);
        this.teleInfo = teleInfo;
    }

    @Override
    public MapCodec<? extends AbstractEffect> getCodec() {
        return CODEC;
    }

    public TeleportInfo getTeleportInfo() {
        return this.teleInfo;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);
        applyParticlesToUser(user);
        for (Entity target : targets) {
            applyParticlesToTarget(target);

            switch (teleInfo.getTarget()) {
                case USER -> {
                    WanderersOfTheRift.LOGGER.info("Teleporting Self");
                    Entity random = targets.get(user.getRandom().nextIntBetweenInclusive(0, targets.size() - 1));
                    user.teleportTo(random.getX() + teleInfo.getPosition().x, random.getY() + teleInfo.getPosition().y,
                            random.getZ() + teleInfo.getPosition().z);

                }

                case TARGET -> {
                    WanderersOfTheRift.LOGGER.info("Teleporting Target");
                    if (teleInfo.isRelative().isEmpty()
                            || (teleInfo.isRelative().isPresent() && teleInfo.isRelative().get())) {
                        target.teleportRelative(teleInfo.getPosition().x, teleInfo.getPosition().y,
                                teleInfo.getPosition().z);
                    } else {
                        target.teleportTo(teleInfo.getPosition().x, teleInfo.getPosition().y, teleInfo.getPosition().z);
                    }
                }
            }

            // Then apply children affects to targets
            super.apply(target, getTargeting().getBlocks(user), context);
        }

        if (targets.isEmpty()) {
            super.apply(null, getTargeting().getBlocks(user), context);
        }
    }

}
