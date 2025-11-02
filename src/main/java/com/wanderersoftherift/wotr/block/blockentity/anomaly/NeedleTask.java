package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public record NeedleTask(HolderSet<Item> needleItem, IntProvider stitchCount) implements AnomalyTask<NeedleTaskState> {

    public static final AnomalyTaskType<NeedleTaskState> TYPE = new AnomalyTaskType<>(
            RecordCodecBuilder.<NeedleTask>mapCodec(
                    instance -> instance.group(
                            RegistryCodecs.homogeneousList(Registries.ITEM)
                                    .fieldOf("needle_item")
                                    .forGetter(NeedleTask::needleItem),
                            IntProvider.CODEC.fieldOf("stitch_count").forGetter(NeedleTask::stitchCount)
                    ).apply(instance, NeedleTask::new)
            ), NeedleTaskState.CODEC
    );

    @Override
    public AnomalyTaskType<NeedleTaskState> type() {
        return TYPE;
    }

    @Override
    public int particleColor() {
        return 0x00_00_ff;
    }

    @Override
    public AnomalyTaskDisplay taskDisplay(NeedleTaskState task) {
        return null;
    }

    @Override
    public NeedleTaskState createState(RandomSource randomSource) {
        return new NeedleTaskState(stitchCount.sample(randomSource));
    }

    public InteractionResult interact(
            Player player,
            InteractionHand hand,
            AnomalyBlockEntity anomalyBlockEntity,
            NeedleTaskState state) {
        var handItem = player.getItemInHand(hand);
        if (!handItem.is(needleItem)) {
            return InteractionResult.PASS;
        }
        var stitchCount = state.remainingStitches();
        if (stitchCount == 1) {
            anomalyBlockEntity.closeAndReward(player);
        } else {
            anomalyBlockEntity.updateTask(new NeedleTaskState(stitchCount - 1));
        }
        return InteractionResult.SUCCESS;
    }

}
