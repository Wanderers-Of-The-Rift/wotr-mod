package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static net.minecraft.core.Direction.Axis;

public record OffsetTargeting(float x, float y, float z, boolean isXZViewRelative, boolean isYViewRelative)
        implements AbilityTargeting {

    public static final MapCodec<OffsetTargeting> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("x").forGetter(OffsetTargeting::x),
                    Codec.FLOAT.fieldOf("y").forGetter(OffsetTargeting::y),
                    Codec.FLOAT.fieldOf("z").forGetter(OffsetTargeting::z),
                    Codec.BOOL.optionalFieldOf("is_xz_view_relative", true)
                            .forGetter(OffsetTargeting::isXZViewRelative),
                    Codec.BOOL.optionalFieldOf("is_y_view_relative", false).forGetter(OffsetTargeting::isYViewRelative)
            ).apply(instance, OffsetTargeting::new)
    );

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        return List.of(new TargetInfo(origin.source(), origin.targets().stream().map(this::mapHitResult).toList()));
    }

    private HitResult mapHitResult(HitResult hitResult) {
        return switch (hitResult) {
            case BlockHitResult blockHit -> new BlockHitResult(
                    mapPosition(blockHit.getLocation(), blockHit.getDirection().getUnitVec3(),
                            blockHit.getDirection().getAxis() == Axis.Y ? new Vec3(0, 0, 1) : new Vec3(0, 1, 0)),
                    blockHit.getDirection(), blockHit.getBlockPos(), blockHit.isInside(), blockHit.isWorldBorderHit());
            case EntityHitResult entityHit -> new EntityHitResult(entityHit.getEntity(),
                    mapPosition(entityHit.getLocation(), entityHit.getEntity().getViewVector(0),
                            entityHit.getEntity()
                                    .calculateViewVector(entityHit.getEntity().getXRot(0) + ((float) Math.PI) / 2,
                                            entityHit.getEntity().getYRot(0))));
            case null, default -> {
                yield hitResult;
            }
        };
    }

    private Vec3 mapPosition(Vec3 location, Vec3 z, Vec3 y) {
        var x = z.cross(y);
        if (isXZViewRelative) {
            location = location.add(x.scale(this.x));
            location = location.add(z.scale(this.z));
        } else {
            location = location.add(this.x, 0, this.z);
        }
        if (isYViewRelative) {
            location = location.add(y.scale(this.y));
        } else {
            location = location.add(0, this.y, 0);
        }

        return location;
    }
}
