package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbstractTargeting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class MovementEffect extends AbstractEffect {
    private final Vec3 velocity;
    private final RelativeFrame relativeFrame;

    public MovementEffect(AbstractTargeting targeting, List<AbstractEffect> effects, Optional<ParticleInfo> particles,
            Vec3 velocity, RelativeFrame relativeFrame) {
        super(targeting, effects, particles);
        this.velocity = velocity;
        this.relativeFrame = relativeFrame;
    }

    public static final MapCodec<MovementEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbstractEffect.commonFields(instance)
                    .and(Vec3.CODEC.fieldOf("velocity").forGetter(MovementEffect::getVelocity))
                    .and(RelativeFrame.CODEC.optionalFieldOf("relativeFrame", RelativeFrame.TARGET_FACING)
                            .forGetter(MovementEffect::getRelativeFrame))
                    .apply(instance, MovementEffect::new));

    @Override
    public MapCodec<? extends AbstractEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);

        applyParticlesToUser(user);

        for (Entity target : targets) {
            applyParticlesToTarget(target);
            // TODO look into implementing scaling still

            // TODO look into relative vs directional
            Vec3 relativeVelocity = relativeFrame.apply(velocity, user, target);
            target.setDeltaMovement(target.getDeltaMovement().add(relativeVelocity));

            ChunkSource chunk = target.level().getChunkSource();
            if (chunk instanceof ServerChunkCache chunkCache) {
                chunkCache.broadcast(target, new ClientboundSetEntityMotionPacket(target));
            }

            if (target instanceof Player player) {
                // This is the secret sauce to making the movement work for players
                ((ServerPlayer) player).connection.send(new ClientboundSetEntityMotionPacket(player));
            }

            // Then apply children affects to targets
            super.apply(target, getTargeting().getBlocks(user), context);
        }

        if (targets.isEmpty()) {
            super.apply(null, getTargeting().getBlocks(user), context);
        }

    }

    public Vec3 getVelocity() {
        return this.velocity;
    }

    public RelativeFrame getRelativeFrame() {
        return relativeFrame;
    }
}
