package com.wanderersoftherift.wotr.world.level.levelgen.space;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.function.Function;

public interface SerializableCorridorValidator extends CorridorValidator {

    Codec<SerializableCorridorValidator> CODEC = WotrRegistries.RIFT_CORRIDOR_VALIDATORS.byNameCodec()
            .dispatch(SerializableCorridorValidator::codec, Function.identity());

    MapCodec<? extends SerializableCorridorValidator> codec();

    record GeneratorLayout() implements SerializableCorridorValidator {

        public static final GeneratorLayout INSTANCE = new GeneratorLayout();

        public static final MapCodec<GeneratorLayout> CODEC = RecordCodecBuilder
                .mapCodec(instance -> instance.point(INSTANCE));

        @Override
        public MapCodec<? extends SerializableCorridorValidator> codec() {
            return CODEC;
        }

        @Override
        public boolean validateCorridor(
                int x,
                int y,
                int z,
                Direction d,
                FastRiftGenerator generator,
                MinecraftServer server) {
            return generator.getOrCreateLayout(server).validateCorridor(x, y, z, d, generator, server);
        }
    }

    record Identity(SerializableCorridorValidator base) implements SerializableCorridorValidator {

        public static final MapCodec<Identity> CODEC = SerializableCorridorValidator.CODEC.fieldOf("base")
                .xmap(Identity::new, Identity::base);

        @Override
        public MapCodec<? extends SerializableCorridorValidator> codec() {
            return CODEC;
        }

        @Override
        public boolean validateCorridor(
                int x,
                int y,
                int z,
                Direction d,
                FastRiftGenerator generator,
                MinecraftServer server) {
            return base().validateCorridor(x, y, z, d, generator, server);
        }
    }

    record Opposite(SerializableCorridorValidator base) implements SerializableCorridorValidator {

        public static final MapCodec<Opposite> CODEC = SerializableCorridorValidator.CODEC.fieldOf("base")
                .xmap(Opposite::new, Opposite::base);

        @Override
        public MapCodec<? extends SerializableCorridorValidator> codec() {
            return CODEC;
        }

        @Override
        public boolean validateCorridor(
                int x,
                int y,
                int z,
                Direction d,
                FastRiftGenerator generator,
                MinecraftServer server) {
            return base().validateCorridor(x + d.getStepX(), y + d.getStepY(), z + d.getStepZ(), d.getOpposite(),
                    generator, server);
        }
    }

    record Inverted(SerializableCorridorValidator base) implements SerializableCorridorValidator {

        public static final MapCodec<Inverted> CODEC = SerializableCorridorValidator.CODEC.fieldOf("base")
                .xmap(Inverted::new, Inverted::base);

        @Override
        public MapCodec<? extends SerializableCorridorValidator> codec() {
            return CODEC;
        }

        @Override
        public boolean validateCorridor(
                int x,
                int y,
                int z,
                Direction d,
                FastRiftGenerator generator,
                MinecraftServer server) {
            return !base().validateCorridor(x, y, z, d, generator, server);
        }
    }

    record Or(List<SerializableCorridorValidator> base) implements SerializableCorridorValidator {

        public static final MapCodec<Or> CODEC = SerializableCorridorValidator.CODEC.listOf()
                .fieldOf("values")
                .xmap(Or::new, Or::base);

        public Or(SerializableCorridorValidator... values) {
            this(ImmutableList.copyOf(values));
        }

        @Override
        public MapCodec<? extends SerializableCorridorValidator> codec() {
            return CODEC;
        }

        @Override
        public boolean validateCorridor(
                int x,
                int y,
                int z,
                Direction d,
                FastRiftGenerator generator,
                MinecraftServer server) {
            return base.stream().anyMatch(it -> it.validateCorridor(x, y, z, d, generator, server));
        }
    }

    record And(List<SerializableCorridorValidator> base) implements SerializableCorridorValidator {

        public static final MapCodec<And> CODEC = SerializableCorridorValidator.CODEC.listOf()
                .fieldOf("values")
                .xmap(And::new, And::base);

        public And(SerializableCorridorValidator... values) {
            this(ImmutableList.copyOf(values));
        }

        @Override
        public MapCodec<? extends SerializableCorridorValidator> codec() {
            return CODEC;
        }

        @Override
        public boolean validateCorridor(
                int x,
                int y,
                int z,
                Direction d,
                FastRiftGenerator generator,
                MinecraftServer server) {
            return base.stream().allMatch(it -> it.validateCorridor(x, y, z, d, generator, server));
        }
    }
}
