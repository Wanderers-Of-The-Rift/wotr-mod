package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Effect that applies physics movement to target entities
 */
public class MovementEffect implements AbilityEffect {
    public static final MapCodec<MovementEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Vec3.CODEC.fieldOf("velocity").forGetter(MovementEffect::getVelocity),
            RelativeFrame.CODEC.optionalFieldOf("relative_frame", RelativeFrame.TARGET_FACING)
                    .forGetter(MovementEffect::getRelativeFrame)
    ).apply(instance, MovementEffect::new));

    private final Vec3 velocity;
    private final RelativeFrame relativeFrame;

    public MovementEffect(Vec3 velocity, RelativeFrame relativeFrame) {
        this.velocity = velocity;
        this.relativeFrame = relativeFrame;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        // TODO: Can we support a source block? Really just need a source position and source rotation/direction
        if (!(targetInfo.source() instanceof EntityHitResult sourceHit)) {
            return;
        }
        Entity source = sourceHit.getEntity();

        targetInfo.targetEntities().forEach(target -> {
            // TODO look into implementing scaling still

            // TODO look into relative vs directional
            Vec3 relativeVelocity = relativeFrame.apply(velocity, source, target);
            target.setDeltaMovement(target.getDeltaMovement().add(relativeVelocity));

            ChunkSource chunk = target.level().getChunkSource();
            if (chunk instanceof ServerChunkCache chunkCache) {
                chunkCache.broadcast(target, new ClientboundSetEntityMotionPacket(target));
            }

            if (target instanceof Player player) {
                // This is the secret sauce to making the movement work for players
                ((ServerPlayer) player).connection.send(new ClientboundSetEntityMotionPacket(player));
            }
        });

    }

    public Vec3 getVelocity() {
        return this.velocity;
    }

    public RelativeFrame getRelativeFrame() {
        return relativeFrame;
    }
}
