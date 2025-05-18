package com.wanderersoftherift.wotr.abilities.effects.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.LocalCoordinates;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public record PositionInfo(Vec3 position, Mode mode) {
	public enum Mode implements StringRepresentable {
		// Following Minecraft's naming conventions for position modes
		// Relative is just relative to the entity's world position
		RELATIVE("relative", 0),
		// World is basically the global position
		WORLD("world", 1),
		// Local is relative to the entity's looking direction
		LOCAL("local", 2);

		public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);

		private final String name;
		private final int id;

		Mode(String name, int id) {
			this.name = name;
			this.id = id;
		}

		public String getName() {
			return this.name;
		}

		public int getId() {
			return this.id;
		}

		@Override
		public String getSerializedName() {
			return this.getName();
		}
	}

	public static final MapCodec<PositionInfo> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Vec3.CODEC.fieldOf("values").forGetter(PositionInfo::position),
					Mode.CODEC.optionalFieldOf("mode", Mode.LOCAL).forGetter(PositionInfo::mode)
			).apply(instance, PositionInfo::new)
	);

	@Override
	public Vec3 position() {
		return position;
	}

	public Vec3 worldPosition(Entity entity) {
		switch (mode) {
			case RELATIVE -> {
				return entity.position().add(position);
			}
			case WORLD -> {
				return position;
			}
			case LOCAL -> {
				LocalCoordinates coordinates = new LocalCoordinates(position.x, position.y, position.z);
				return coordinates.getPosition(new CommandSourceStack(CommandSource.NULL, entity.position(), new Vec2(entity.getXRot(), entity.getYRot()), (ServerLevel) entity.level(), 0, "", Component.empty(), entity.level().getServer(), entity));
			}
		}
		return position;
	}

	@Override
	public Mode mode() {
		return mode;
	}
}
