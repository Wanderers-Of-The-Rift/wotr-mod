package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;

public record BundleTask(Map<HolderSet<Item>, IntProvider> rolls) implements AnomalyTask<BundleTaskState> {
    public static final AnomalyTaskType<BundleTaskState> TYPE = new AnomalyTaskType<>(
            Codec.unboundedMap(Codec.withAlternative(HolderSetCodec.create(Registries.ITEM, Item.CODEC, true),
                    BuiltInRegistries.ITEM.holderByNameCodec(), HolderSet::direct), IntProvider.CODEC)
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
        rolls.forEach((item, count) -> task.put(item.getRandomElement(rng).get().value(), count.sample(rng)));
        return new BundleTaskState(task);
    }

    @Override
    public AnomalyTaskDisplay taskDisplay(BundleTaskState task) {
        return new AnomalyTaskDisplay() {
            @Override
            public int getCount() {
                return task.requirements().size();
            }

            @Override
            public void forEachIndexed(BiConsumer<Integer, ItemStack> func) {
                var i = 0;
                for (var req : task.requirements().object2IntEntrySet()) {
                    func.accept(i, new ItemStack(req.getKey(), req.getIntValue()));
                    i++;
                }
            }
        };
    }

    public InteractionResult interact(
            Player player,
            InteractionHand hand,
            AnomalyBlockEntity entity,
            BundleTaskState state) {
        var handItem = player.getItemInHand(hand);
        if (!handItem.is(ItemTags.BUNDLES)) {
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
            if (consumed > 0) {
                resultIfIncomplete = InteractionResult.SUCCESS;
            }
            if (remaining == consumed) {
                requirements.removeInt(item);
            } else if (consumed > 0) {
                requirements.put(item, remaining - consumed);
            }
        }
        if (resultIfIncomplete != InteractionResult.PASS) {
            handItem.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(newContent));
            if (requirements.isEmpty()) {
                entity.closeAndReward(player);
            } else {
                entity.updateTask(new BundleTaskState(requirements));
            }
        }
        return resultIfIncomplete;
    }
}
