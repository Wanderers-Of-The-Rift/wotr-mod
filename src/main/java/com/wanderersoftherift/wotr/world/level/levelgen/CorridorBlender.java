package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.item.riftkey.RiftGenerationConfig;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.space.CorridorValidator;
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

public record CorridorBlender(int layerCount, RiftGenerationConfig config) {

    public static final int CORRIDOR_WIDTH = 5;
    public static final int CORRIDOR_HEIGHT = 7;
    public static final int CORRIDOR_START_X = 6;
    public static final int CORRIDOR_START_Y = 5;
    public static final int CORRIDOR_OPTIONAL_START_X = 1; // optionals are in corridor space
    public static final int CORRIDOR_OPTIONAL_START_Y = 2;
    public static final int CORRIDOR_OPTIONAL_END_X = 3;
    public static final int CORRIDOR_OPTIONAL_END_Y = 4;

    public void runCorridorBlender(
            CorridorValidator validator,
            ChunkAccess chunk,
            PositionalRandomFactory randomFactory,
            WorldGenLevel level) {
        if (!config.generatePassages()) {
            return;
        }
        var rng = randomFactory.at(chunk.getPos().x, 0, chunk.getPos().z);
        for (int i = 0; i < layerCount; i++) {
            var chunkX = chunk.getPos().x;
            var chunkY = i - layerCount / 2;
            var chunkZ = chunk.getPos().z;
            runCorridorBlenderDirectional(validator, chunkX, chunkY, chunkZ, NORTH, level, rng);
            runCorridorBlenderDirectional(validator, chunkX, chunkY, chunkZ, WEST, level, rng);
        }
    }

    private void runCorridorBlenderDirectional(
            CorridorValidator validator,
            int chunkX,
            int chunkY,
            int chunkZ,
            Direction direction,
            WorldGenLevel level,
            RandomSource rng) {
        if (validator.validateCorridor(chunkX, chunkY, chunkZ, direction)) {
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
