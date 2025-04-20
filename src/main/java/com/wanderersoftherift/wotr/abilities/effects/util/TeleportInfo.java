package com.wanderersoftherift.wotr.abilities.effects.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.IntFunction;

public class TeleportInfo {
    public static final MapCodec<TeleportInfo> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(TeleportTarget.CODEC.fieldOf("teleport_target").forGetter(TeleportInfo::getTarget),
                    Vec3.CODEC.fieldOf("position").forGetter(TeleportInfo::getPosition),
                    Codec.BOOL.optionalFieldOf("relative").forGetter(TeleportInfo::isRelative))
            .apply(instance, TeleportInfo::new));

    private final TeleportTarget target;
    private final Vec3 position;
    private final Optional<Boolean> isRelative;

    public TeleportInfo(TeleportTarget target, Vec3 position, Optional<Boolean> isRelative) {
        this.target = target;
        this.position = position;
        this.isRelative = isRelative;
    }

    public Optional<Boolean> isRelative() {
        return this.isRelative;
    }

    public Vec3 getPosition() {
        return position;
    }

    public TeleportTarget getTarget() {
        return this.target;
    }

    public enum TeleportTarget implements StringRepresentable {
        USER("user", 0),
        TARGET("target", 1);

        public static final IntFunction<TeleportTarget> BY_ID = ByIdMap.continuous(TeleportInfo.TeleportTarget::id,
                values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, TeleportInfo.TeleportTarget> STREAM_CODEC = ByteBufCodecs
                .idMapper(TeleportInfo.TeleportTarget.BY_ID, TeleportInfo.TeleportTarget::id);
        public static final Codec<TeleportInfo.TeleportTarget> CODEC = StringRepresentable
                .fromEnum(TeleportInfo.TeleportTarget::values);
        private final String name;
        private final int id;

        private TeleportTarget(String name, int value) {
            this.name = name;
            this.id = value;
        }

        public int id() {
            return this.id;
        }

        public String getSerializedName() {
            return this.name;
        }
    }
}
