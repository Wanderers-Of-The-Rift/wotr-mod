package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Data component for capturing a target
 */
public final class TargetComponent {
    public static final Codec<TargetComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Vec3.CODEC.fieldOf("location").forGetter(TargetComponent::location),
            UUIDUtil.CODEC.optionalFieldOf("entity_id").forGetter(TargetComponent::entityId),
            BlockPos.CODEC.optionalFieldOf("block_pos").forGetter(TargetComponent::blockPos),
            Direction.CODEC.fieldOf("direction").forGetter(TargetComponent::direction)
    ).apply(instance, TargetComponent::new));

    private final Vec3 location;
    private final Optional<UUID> entityId;
    private final Optional<BlockPos> blockPos;
    private final Direction direction;

    public TargetComponent(Vec3 location, Optional<UUID> entityId, Optional<BlockPos> blockPos, Direction direction) {
        this.location = location;
        this.entityId = entityId;
        this.blockPos = blockPos;
        this.direction = direction;
    }

    public TargetComponent(HitResult hit) {
        Preconditions.checkNotNull(hit);
        this.location = hit.getLocation();
        switch (hit) {
            case BlockHitResult blockHit -> {
                if (blockHit.getType() == HitResult.Type.MISS) {
                    this.blockPos = Optional.empty();
                } else {
                    this.blockPos = Optional.of(blockHit.getBlockPos());
                }
                this.direction = blockHit.getDirection();
                this.entityId = Optional.empty();
            }
            case EntityHitResult entityHit -> {
                this.entityId = Optional.of(entityHit.getEntity().getUUID());
                this.blockPos = Optional.empty();
                this.direction = Direction.UP;
            }
            default -> {
                this.entityId = Optional.empty();
                this.blockPos = Optional.empty();
                this.direction = Direction.UP;
            }
        }
    }

    public Vec3 location() {
        return location;
    }

    public Optional<UUID> entityId() {
        return entityId;
    }

    public Optional<BlockPos> blockPos() {
        return blockPos;
    }

    public Direction direction() {
        return direction;
    }

    public HitResult asHitResult(@NotNull ServerLevel level) {
        Entity entity = entityId.map(level::getEntity).orElse(null);
        if (entity != null) {
            return new EntityHitResult(entity, location);
        } else if (blockPos.isPresent()) {
            return new BlockHitResult(location, direction, blockPos.get(), false);
        }
        return BlockHitResult.miss(location, direction, BlockPos.containing(location));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof TargetComponent other) {
            return location.equals(other.location) && entityId.equals(other.entityId) && blockPos.equals(other.blockPos)
                    && direction == other.direction;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(location, entityId, blockPos, direction);
    }
}
