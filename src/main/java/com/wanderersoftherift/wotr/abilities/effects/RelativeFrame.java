package com.wanderersoftherift.wotr.abilities.effects;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Enumeration of possible frames of reference to use in an ability.
 */
public enum RelativeFrame implements StringRepresentable {
    ABSOLUTE("absolute") {
        @Override
        public Vec3 apply(Vec3 base, Entity source, Entity target) {
            return base;
        }
    },
    SOURCE_FROM_TARGET("source_from_target") {
        @Override
        public Vec3 apply(Vec3 base, Entity source, Entity target) {
            Vector3f dir = source.getPosition(1.0f).toVector3f();
            dir.sub(target.getPosition(1.0f).toVector3f());
            dir.normalize();
            if (dir.lengthSquared() < 0.1) {
                return Vec3.ZERO;
            }
            float pitch = Math.asin(-dir.y);
            float yaw = Math.atan2(dir.x, dir.z);
            Quaternionf rot = new Quaternionf().rotateYXZ(yaw, pitch, 0);
            return new Vec3(rot.transform(base.toVector3f()));
        }
    },
    TARGET_FROM_SOURCE("target_from_source") {
        @Override
        public Vec3 apply(Vec3 base, Entity source, Entity target) {
            Vector3f dir = target.getPosition(1.0f).toVector3f();
            dir.sub(source.getPosition(1.0f).toVector3f());
            dir.normalize();
            if (dir.lengthSquared() < 0.1) {
                return Vec3.ZERO;
            }
            float pitch = Math.asin(-dir.y);
            float yaw = Math.atan2(dir.x, dir.z);
            Quaternionf rot = new Quaternionf().rotateYXZ(yaw, pitch, 0);
            return new Vec3(rot.transform(base.toVector3f()));
        }
    },
    SOURCE_FACING("source_facing") {
        @Override
        public Vec3 apply(Vec3 base, Entity source, Entity target) {
            Quaternionf rotation = new Quaternionf().rotateYXZ(-source.getYRot() * (float) (Math.PI / 180.0),
                    (source.getXRot() * (float) (Math.PI / 180.0)), 0);
            return new Vec3(rotation.transform(base.toVector3f()));
        }
    },
    TARGET_FACING("target_facing") {
        @Override
        public Vec3 apply(Vec3 base, Entity source, Entity target) {
            Quaternionf rotation = new Quaternionf().rotateYXZ(-target.getYRot() * (float) (Math.PI / 180.0),
                    (target.getXRot() * (float) (Math.PI / 180.0)), 0);
            return new Vec3(rotation.transform(base.toVector3f()));
        }
    },
    SOURCE_Y_FACING("source_y_facing") {
        @Override
        public Vec3 apply(Vec3 base, Entity source, Entity target) {
            return base.yRot(-source.getYRot() * (float) (Math.PI / 180.0));
        }
    },
    TARGET_Y_FACING("target_y_facing") {
        @Override
        public Vec3 apply(Vec3 base, Entity source, Entity target) {
            return base.yRot(-target.getYRot() * (float) (Math.PI / 180.0));
        }
    };

    public static final StringRepresentable.StringRepresentableCodec<RelativeFrame> CODEC = StringRepresentable
            .fromEnum(RelativeFrame::values);

    private final String name;

    RelativeFrame(String name) {
        this.name = name;
    }

    public abstract Vec3 apply(Vec3 base, Entity source, Entity target);

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
