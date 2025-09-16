package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.init.WotrItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public record NeedleTask() implements AnomalyTask<NeedleTaskState> {

    public static final AnomalyTaskType<NeedleTaskState> TYPE = new AnomalyTaskType<>(
            MapCodec.unit(new NeedleTask()), MapCodec.unit(new NeedleTaskState()).codec()
    );

    @Override
    public AnomalyTaskType<NeedleTaskState> type() {
        return TYPE;
    }

    @Override
    public NeedleTaskState createState() {
        return new NeedleTaskState();
    }

    public InteractionResult interact(
            Player player,
            InteractionHand hand,
            AnomalyBlockEntity anomalyBlockEntity,
            NeedleTaskState state) {
        var handItem = player.getItemInHand(hand);
        if (!handItem.is(WotrItems.ABILITY_HOLDER /* todo needle */)) {
            return InteractionResult.PASS;
        }
        anomalyBlockEntity.closeAndReward(player);
        return InteractionResult.SUCCESS;
    }

}
