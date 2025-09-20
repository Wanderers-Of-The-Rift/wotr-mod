package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.TeleportInfo;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class TeleportEffect implements AbilityEffect {
    public static final MapCodec<TeleportEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TeleportInfo.CODEC.fieldOf("tele_info").forGetter(TeleportEffect::getTeleportInfo)
    ).apply(instance, TeleportEffect::new));

    private final TeleportInfo teleInfo;

    // TODO look into handling different types of teleports and better handle relative motion
    // TODO also look into teleporting "towards" a location to find the nearest safe spot that isnt the exact location

    public TeleportEffect(TeleportInfo teleInfo) {
        this.teleInfo = teleInfo;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    public TeleportInfo getTeleportInfo() {
        return this.teleInfo;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        switch (teleInfo.getTarget()) {
            case USER -> {
                if (!(targetInfo.source() instanceof EntityHitResult entitySource)) {
                    return;
                }
                HitResult randomTarget = targetInfo.getRandomTarget(context.level().getRandom());
                entitySource.getEntity()
                        .teleportTo(randomTarget.getLocation().x + teleInfo.getPosition().x,
                                randomTarget.getLocation().y + teleInfo.getPosition().y,
                                randomTarget.getLocation().z + teleInfo.getPosition().z);
            }
            case TARGET -> {
                targetInfo.targetEntities().forEach(target -> {
                    if (teleInfo.isRelative().isEmpty()
                            || (teleInfo.isRelative().isPresent() && teleInfo.isRelative().get())) {
                        target.teleportRelative(teleInfo.getPosition().x, teleInfo.getPosition().y,
                                teleInfo.getPosition().z);
                    } else {
                        target.teleportTo(teleInfo.getPosition().x, teleInfo.getPosition().y, teleInfo.getPosition().z);
                    }
                });
            }
        }
    }

}
