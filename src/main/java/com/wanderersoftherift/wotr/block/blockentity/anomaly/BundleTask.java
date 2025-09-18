package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;

import java.util.ArrayList;
import java.util.Map;

public record BundleTask(Map<Holder<Item>, IntProvider> rolls) implements AnomalyTask<BundleTaskState> {
    public static final AnomalyTaskType<BundleTaskState> TYPE = new AnomalyTaskType<>(
            Codec.unboundedMap(BuiltInRegistries.ITEM.holderByNameCodec(), IntProvider.CODEC)
                    .xmap(BundleTask::new, BundleTask::rolls)
                    .fieldOf("items"),
            BundleTaskState.CODEC);

    @Override
    public AnomalyTaskType<BundleTaskState> type() {
        return TYPE;
    }

    @Override
    public int particleColor() {
        return 0x00_ff_00;
    }

    @Override
    public BundleTaskState createState(RandomSource rng) {
        var task = new Object2IntOpenHashMap<Item>();
        rolls.forEach((item, count) -> task.put(item.value(), count.sample(rng)));
        return new BundleTaskState(task);
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

        var newContent = new ArrayList<ItemStack>();

        for (var bundleContentEntry : content.items()) {
            var item = bundleContentEntry.getItem();
            if (requirements.isEmpty() || !requirements.containsKey(item)) {
                if (!bundleContentEntry.isEmpty()) {
                    newContent.add(bundleContentEntry);
                }
                continue;
            }
            var remaining = requirements.getInt(item);
            var consumed = Integer.min(remaining, bundleContentEntry.getCount());
            bundleContentEntry.shrink(consumed);
            if (!bundleContentEntry.isEmpty()) {
                newContent.add(bundleContentEntry);
            }
            if (remaining == consumed) {
                requirements.removeInt(item);
                if (requirements.isEmpty()) {
                    entity.closeAndReward(player);
                    resultIfIncomplete = InteractionResult.SUCCESS;
                }
            } else if (consumed > 0) {
                requirements.put(item, remaining - consumed);
                resultIfIncomplete = InteractionResult.SUCCESS;
            }
        }
        if (newContent.size() < content.size()) {
            handItem.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(newContent));
        }
        if (resultIfIncomplete != InteractionResult.PASS) {
            entity.updateTask(new BundleTaskState(requirements));
        }

        return resultIfIncomplete;
    }

}
