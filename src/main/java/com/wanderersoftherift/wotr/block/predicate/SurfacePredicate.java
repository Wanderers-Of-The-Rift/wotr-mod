package com.wanderersoftherift.wotr.block.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import org.jetbrains.annotations.NotNull;

/**
 * Predicate for blocks that form the surface - they have a full top side and have a configurable amount of space above
 * them (so susceptible to standard spawn proofing techniques)
 * 
 * @param space
 */
public record SurfacePredicate(int space) implements BlockPredicate {

    public static final MapCodec<SurfacePredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("space", 2).forGetter(SurfacePredicate::space)
    ).apply(instance, SurfacePredicate::new));

    public static final BlockPredicateType<SurfacePredicate> TYPE = () -> CODEC;

    @Override
    public @NotNull BlockPredicateType<?> type() {
        return TYPE;
    }

    @Override
    public boolean test(WorldGenLevel worldGenLevel, BlockPos blockPos) {
        BlockPos.MutableBlockPos pos = blockPos.mutable();
        if (!worldGenLevel.getBlockState(pos).isFaceSturdy(worldGenLevel, pos, Direction.UP)) {
            return false;
        }
        for (int i = 1; i <= space; i++) {
            pos.setY(pos.getY() + 1);
            BlockState state = worldGenLevel.getBlockState(pos);
            if (!state.isAir()) {
                return false;
            }
        }
        return true;
    }
}
