package com.wanderersoftherift.wotr.world.level.levelgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.space.SerializableCorridorValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.WEST;
import static net.minecraft.world.level.block.Blocks.AIR;

public record CorridorBlender(SerializableCorridorValidator validator) implements RiftPostProcessingStep {

    public static final MapCodec<CorridorBlender> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(SerializableCorridorValidator.CODEC.fieldOf("validator").forGetter(CorridorBlender::validator))
            .apply(instance, CorridorBlender::new));

    public static final int CORRIDOR_START_X = 6;
    public static final int CORRIDOR_START_Y = 5;
    public static final int CORRIDOR_WIDTH = 5;
    public static final int CORRIDOR_HEIGHT = 7;
    public static final int CORRIDOR_OPTIONAL_START_X = 1; // optional region is relative to the corridor
    public static final int CORRIDOR_OPTIONAL_START_Y = 2;
    public static final int CORRIDOR_OPTIONAL_END_X = 3;
    public static final int CORRIDOR_OPTIONAL_END_Y = 4;

    @Override
    public MapCodec<? extends RiftPostProcessingStep> codec() {
        return CODEC;
    }

    public void runPostProcessing(
            FastRiftGenerator generator,
            ChunkAccess chunk,
            PositionalRandomFactory randomFactory,
            WorldGenLevel level) {
        var layerCount = generator.layerCount();
        var rng = randomFactory.at(chunk.getPos().x, 0, chunk.getPos().z);
        for (int i = 0; i < layerCount; i++) {
            var chunkX = chunk.getPos().x;
            var chunkY = i - layerCount / 2;
            var chunkZ = chunk.getPos().z;
            runCorridorBlenderDirectional(generator, chunkX, chunkY, chunkZ, NORTH, level, rng);
            runCorridorBlenderDirectional(generator, chunkX, chunkY, chunkZ, WEST, level, rng);
        }
    }

    private void runCorridorBlenderDirectional(
            FastRiftGenerator generator,
            int chunkX,
            int chunkY,
            int chunkZ,
            Direction direction,
            WorldGenLevel level,
            RandomSource rng) {
        if (validator.validateCorridor(chunkX, chunkY, chunkZ, direction, generator, level.getServer())) {
            for (int x = 0; x < CORRIDOR_WIDTH; x++) {
                for (int y = 0; y < CORRIDOR_HEIGHT; y++) {
                    var pos = new BlockPos(
                            (chunkX << RiftProcessedChunk.CHUNK_WIDTH_SHIFT)
                                    - (x + CORRIDOR_START_X) * direction.getStepZ(),
                            (chunkY << RiftProcessedChunk.CHUNK_HEIGHT_SHIFT) + CORRIDOR_START_Y + y,
                            (chunkZ << RiftProcessedChunk.CHUNK_WIDTH_SHIFT)
                                    - (x + CORRIDOR_START_X) * direction.getStepX());
                    var posOffset = pos.relative(direction);
                    var state = level.getBlockState(pos.relative(direction.getOpposite()));
                    if (x >= CORRIDOR_OPTIONAL_START_X && x <= CORRIDOR_OPTIONAL_END_X && y >= CORRIDOR_OPTIONAL_START_Y
                            && y <= CORRIDOR_OPTIONAL_END_Y) {
                        if (rng.nextBoolean()) {
                            state = level.getBlockState(posOffset);
                        }
                    } else {
                        if (rng.nextBoolean() || !isStateAllowedByCorridorBlender(state, direction)) {
                            var newState = level.getBlockState(posOffset);
                            if (isStateAllowedByCorridorBlender(newState, direction)) {
                                state = level.getBlockState(posOffset);
                            }
                        }
                    }
                    if (!isStateAllowedByCorridorBlender(state, direction)) {
                        state = AIR.defaultBlockState();
                    }
                    level.setBlock(pos, state, 0);
                }
            }
        }
    }

    private boolean isStateAllowedByCorridorBlender(BlockState state, Direction direction) {
        if (!state.canOcclude()) {
            return false;
        }
        var shape = state.getOcclusionShape();
        return ProcessorUtil.isFaceFullFast(shape, direction) == ProcessorUtil.isFaceFullFast(shape,
                direction.getOpposite());
    }

}
