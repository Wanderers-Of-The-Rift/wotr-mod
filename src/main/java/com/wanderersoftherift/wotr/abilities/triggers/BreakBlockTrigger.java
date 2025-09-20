package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.abilities.attachment.TargetComponent;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;

public record BreakBlockTrigger(BlockPos pos, Direction dir) implements TrackedAbilityTrigger {

    public static final MapCodec<BreakBlockTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(BreakBlockTrigger::pos),
            Direction.CODEC.fieldOf("direction").forGetter(BreakBlockTrigger::dir)
    ).apply(instance, BreakBlockTrigger::new));

    public static final TriggerType<BreakBlockTrigger> TYPE = new TriggerType<>(CODEC, null);

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
