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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class TeleportEffect extends AbstractEffect {
    public static final MapCodec<TeleportEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbstractEffect.commonFields(instance)
                    .and(TeleportInfo.CODEC.fieldOf("tele_info").forGetter(TeleportEffect::getTeleportInfo))
                    .apply(instance, TeleportEffect::new));

    private TeleportInfo teleInfo;

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

        switch (teleInfo.getTarget()) {
            case USER -> {
                WanderersOfTheRift.LOGGER.info("Teleporting Self");
                Vec3 position = getPosition(user, teleInfo);
                user.teleportTo(position.x, position.y, position.z);
            }

            case TARGET -> {
                for (Entity target : targets) {
                    if (target == null) {
                        continue;
                    }
                    applyParticlesToTarget(target);

                    WanderersOfTheRift.LOGGER.info("Teleporting Target");
                    Vec3 position = getPosition(target, teleInfo);
                    target.teleportTo(position.x, position.y, position.z);

                    // Then apply children affects to targets
                    super.apply(target, getTargeting().getBlocks(user), context);
                }
            }
        }

        if (targets.isEmpty()) {
            super.apply(null, getTargeting().getBlocks(user), context);
        }
    }

    private Vec3 getPosition(Entity entity, TeleportInfo teleInfo) {
        Level level = entity.level();

        Vec3 worldPosition = teleInfo.getPositionInfo().worldPosition(entity);
        BlockPos worldBlockPos = new BlockPos((int) worldPosition.x, (int) worldPosition.y, (int) worldPosition.z);
        boolean isAir = level.getBlockState(worldBlockPos).getBlock() instanceof AirBlock;

        if (!isAir || !level.isInWorldBounds(worldBlockPos)) {
            BlockHitResult result = level.clip(new ClipContext(entity.position(), worldPosition,
                    ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
            if (result.getType() == HitResult.Type.BLOCK || !level.isInWorldBounds(worldBlockPos)) {
                return result.getLocation();
            }
        }

        return worldPosition;
    }
}
