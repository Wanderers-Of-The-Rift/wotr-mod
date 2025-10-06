package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.TeleportInfo;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public record TeleportEffect(TeleportInfo teleportInfo) implements AbilityEffect {
    public static final MapCodec<TeleportEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TeleportInfo.CODEC.fieldOf("tele_info").forGetter(TeleportEffect::teleportInfo)
    ).apply(instance, TeleportEffect::new));

    // TODO look into handling different types of teleports and better handle relative motion
    // TODO also look into teleporting "towards" a location to find the nearest safe spot that isnt the exact location

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        switch (teleportInfo.getTarget()) {
            case USER -> {
                if (!(targetInfo.source() instanceof EntityHitResult entitySource)) {
                    return;
                }
                HitResult randomTarget = targetInfo.getRandomTarget(context.level().getRandom());
                entitySource.getEntity()
                        .teleportTo(randomTarget.getLocation().x + teleportInfo.getPosition().x,
                                randomTarget.getLocation().y + teleportInfo.getPosition().y,
                                randomTarget.getLocation().z + teleportInfo.getPosition().z);
            }
            case TARGET -> {
                targetInfo.targetEntities().forEach(target -> {
                    if (teleportInfo.isRelative().isEmpty()
                            || (teleportInfo.isRelative().isPresent() && teleportInfo.isRelative().get())) {
                        target.teleportRelative(teleportInfo.getPosition().x, teleportInfo.getPosition().y,
                                teleportInfo.getPosition().z);
                    } else {
                        target.teleportTo(teleportInfo.getPosition().x, teleportInfo.getPosition().y,
                                teleportInfo.getPosition().z);
                    }
                });
            }
        }
    }

}
