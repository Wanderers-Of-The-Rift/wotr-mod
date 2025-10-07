package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.attachment.TargetComponent;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Trigger for when a player breaks a block
 * 
 * @param pos The block that was broken
 * @param dir The direction in which the block was broken
 */
public record BreakBlockTrigger(BlockState state, BlockPos pos, Direction dir) implements TrackableTrigger {

    public static final MapCodec<BreakBlockTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockState.CODEC.fieldOf("state").forGetter(BreakBlockTrigger::state),
            BlockPos.CODEC.fieldOf("pos").forGetter(BreakBlockTrigger::pos),
            Direction.CODEC.fieldOf("direction").forGetter(BreakBlockTrigger::dir)
    ).apply(instance, BreakBlockTrigger::new));

    public static final TriggerType<BreakBlockTrigger> TYPE = new TriggerType<>(BreakBlockPredicate.CODEC, null);

    @Override
    public TriggerType<BreakBlockTrigger> type() {
        return TYPE;
    }

    @Override
    public void addComponents(AbilityContext context) {
        context.set(WotrDataComponentType.AbilityContextData.TRIGGER_TARGET,
                new TargetComponent(new BlockHitResult(pos.getCenter(), dir, pos, false)));
    }
}
