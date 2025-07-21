package com.wanderersoftherift.wotr.core.quest.reward.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import com.wanderersoftherift.wotr.core.quest.reward.ItemReward;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This reward provider generates {@link ItemReward}s from a loot table. Where possible the generated items are combined
 * 
 * @param lootTable
 */
public record LootTableRewardProvider(ResourceKey<LootTable> lootTable) implements RewardProvider {
    public static final MapCodec<LootTableRewardProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    ResourceKey.codec(Registries.LOOT_TABLE)
                            .fieldOf("loot_table")
                            .forGetter(LootTableRewardProvider::lootTable)
            ).apply(instance, LootTableRewardProvider::new));

    @Override
    public MapCodec<? extends RewardProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Reward> generateReward(LootParams params) {
        LootTable table = params.getLevel().getServer().reloadableRegistries().getLootTable(lootTable);
        return condense(table.getRandomItems(params)).stream().<Reward>map(ItemReward::new).toList();
    }

    private List<ItemStack> condense(Collection<ItemStack> randomItems) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack item : randomItems) {
            for (ItemStack existing : result) {
                if (existing.getCount() < existing.getMaxStackSize()
                        && ItemStack.isSameItemSameComponents(existing, item)) {
                    int amount = Math.min(existing.getMaxStackSize() - existing.getCount(), item.getCount());
                    existing.setCount(existing.getCount() + amount);
                    item.setCount(item.getCount() - amount);
                    if (item.isEmpty()) {
                        break;
                    }
                }
            }
            if (!item.isEmpty()) {
                result.add(item);
            }
        }
        return result;
    }
}
