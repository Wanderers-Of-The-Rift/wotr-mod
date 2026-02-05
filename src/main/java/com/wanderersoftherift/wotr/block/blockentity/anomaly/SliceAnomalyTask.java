package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public record SliceAnomalyTask(IntProvider hitpoints) implements AnomalyTask<SliceAnomalyTaskState> {

    public static final AnomalyTaskType<SliceAnomalyTaskState> TYPE = new AnomalyTaskType<>(
            RecordCodecBuilder.<SliceAnomalyTask>mapCodec(
                    instance -> instance.group(
                            IntProvider.CODEC.fieldOf("hit_points").forGetter(SliceAnomalyTask::hitpoints)
                    ).apply(instance, SliceAnomalyTask::new)
            ), SliceAnomalyTaskState.CODEC
    );

    @Override
    public InteractionResult interact(
            Player player,
            InteractionHand hand,
            AnomalyBlockEntity anomalyBlockEntity,
            SliceAnomalyTaskState state) {
        return InteractionResult.PASS;
    }

    public void attack(
            Player player,
            float damage,
            DamageSource source,
            AnomalyBlockEntity anomalyBlockEntity,
            SliceAnomalyTaskState state) {
        player.level()
                .playSound(null, anomalyBlockEntity.getBlockPos(), SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.BLOCKS,
                        1, 1);
        var hitpoints = state.hitpoints();
        if (hitpoints < damage) {
            anomalyBlockEntity.closeAndReward(player);
        } else {
            anomalyBlockEntity.updateTask(new SliceAnomalyTaskState(hitpoints - damage));
        }
    }

    @Override
    public AnomalyTaskType<SliceAnomalyTaskState> type() {
        return TYPE;
    }

    @Override
    public SliceAnomalyTaskState createState(RandomSource rng) {
        return new SliceAnomalyTaskState(hitpoints.sample(rng));
    }

    @Override
    public int particleColor() {
        return 0xff_00_ff_ff;
    }

    @Override
    public AnomalyTaskDisplay taskDisplay(SliceAnomalyTaskState task) {
        return null;
    }
}
