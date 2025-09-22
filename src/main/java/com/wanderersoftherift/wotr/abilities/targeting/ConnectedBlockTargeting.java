package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetBlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ConnectedBlockTargeting follows blocks adjacent to each target, up to a maximum count.
 * 
 * @param blocks A predicate for which blocks can be targeted. Connectivity will not flow through blocks that cannot be
 *               targeted.
 * @param count  A count of blocks to target
 */
public record ConnectedBlockTargeting(TargetBlockPredicate blocks, int count) implements AbilityTargeting {
    public static final MapCodec<ConnectedBlockTargeting> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    TargetBlockPredicate.CODEC.optionalFieldOf("blocks", TargetBlockPredicate.ALL)
                            .forGetter(ConnectedBlockTargeting::blocks),
                    Codec.intRange(1, Integer.MAX_VALUE).fieldOf("count").forGetter(ConnectedBlockTargeting::count)
            ).apply(instance, ConnectedBlockTargeting::new));

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        List<TargetInfo> result = new ArrayList<>();
        for (BlockHitResult source : origin.targetBlockHitResults().toList()) {
            List<HitResult> connected = new ArrayList<>();
            Set<BlockPos> closed = new HashSet<>();
            Deque<BlockPos> open = new ArrayDeque<>();
            closed.add(source.getBlockPos());
            findAdjacent(source.getBlockPos(), open, closed, source, context);
            while (connected.size() < count && !open.isEmpty()) {
                BlockPos pos = open.removeFirst();
                Direction hitDir = Direction.getApproximateNearest(pos.getCenter().subtract(source.getLocation()));
                connected.add(new BlockHitResult(pos.getCenter(), hitDir, pos, false));

                findAdjacent(pos, open, closed, source, context);
            }
            if (!connected.isEmpty()) {
                result.add(new TargetInfo(source, connected));
            }
        }
        return result;
    }

    private void findAdjacent(
            BlockPos pos,
            Deque<BlockPos> open,
            Set<BlockPos> closed,
            BlockHitResult source,
            AbilityContext context) {
        for (Direction dir : Direction.values()) {
            BlockPos adj = pos.relative(dir);
            if (closed.add(adj) && blocks.matches(adj, source, context)) {
                open.add(adj);
            }
        }
    }
}
