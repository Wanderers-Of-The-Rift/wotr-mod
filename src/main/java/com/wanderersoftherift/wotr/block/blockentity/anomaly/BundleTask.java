package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

import java.util.Map;

public record BundleTask() implements AnomalyTask<BundleTaskState> {
    public static final AnomalyTaskType<BundleTaskState> TYPE = new AnomalyTaskType<>(
            MapCodec.unit(new BundleTask()), BundleTaskState.CODEC
    );

    @Override
    public AnomalyTaskType<BundleTaskState> type() {
        return TYPE;
    }

    @Override
    public BundleTaskState createState() {
        return new BundleTaskState(new Object2IntOpenHashMap(Map.of(Items.WHITE_WOOL, 10, Items.GRAY_WOOL, 5)));
    }

    public InteractionResult interact(
            Player player,
            InteractionHand hand,
            AnomalyBlockEntity entity,
            BundleTaskState state) {
        var handItem = player.getItemInHand(hand);
        if (!handItem.is(Items.BUNDLE)) {
            return InteractionResult.PASS;
        }
        var content = handItem.get(DataComponents.BUNDLE_CONTENTS);
        if (content == null) {
            return InteractionResult.PASS;
        }
        var requirements = new Object2IntOpenHashMap<>(state.requirements());

        if (requirements.isEmpty()) {
            entity.closeAndReward(player);
            return InteractionResult.SUCCESS;
        }
        InteractionResult resultIfIncomplete = InteractionResult.PASS;

        for (var bundleContentEntry : content.items()) {
            var item = bundleContentEntry.getItem();
            if (!requirements.containsKey(item)) {
                continue;
            }
            var remaining = requirements.getInt(item);
            var consumed = Integer.min(remaining, bundleContentEntry.getCount());
            bundleContentEntry.shrink(consumed);
            if (remaining == consumed) {
                requirements.removeInt(item);
                if (requirements.isEmpty()) {
                    entity.closeAndReward(player);
                    return InteractionResult.SUCCESS;
                }
            } else if (consumed > 0) {
                requirements.put(item, remaining - consumed);
                resultIfIncomplete = InteractionResult.SUCCESS;
            }

        }

        return resultIfIncomplete;
    }

}
