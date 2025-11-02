package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Effect that plays a sound. The sounds plays either as a Player or Hostile sound depending on the caster.
 */
public record SoundEffect(Holder<SoundEvent> sound) implements AbilityEffect {

    public static final MapCodec<SoundEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SoundEvent.CODEC.fieldOf("sound").forGetter(SoundEffect::sound)
    ).apply(instance, SoundEffect::new));

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        targetInfo.targets().forEach(target -> {
            if (target instanceof EntityHitResult entityHit) {
                SoundSource source;
                if (context.caster() instanceof Player) {
                    source = SoundSource.PLAYERS;
                } else {
                    source = SoundSource.HOSTILE;
                }
                context.level().playSound(null, entityHit.getEntity(), sound.value(), source, 1.0f, 1.0f);
            } else if (target instanceof BlockHitResult blockHit) {
                context.level().playSound(null, blockHit.getBlockPos(), sound.value(), SoundSource.BLOCKS);
            }
        });
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }
}
