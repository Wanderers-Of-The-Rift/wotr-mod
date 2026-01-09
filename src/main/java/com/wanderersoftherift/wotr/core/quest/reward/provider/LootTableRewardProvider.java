package com.wanderersoftherift.wotr.core.quest.reward.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import com.wanderersoftherift.wotr.core.quest.reward.ItemReward;
import com.wanderersoftherift.wotr.util.ItemUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    public @NotNull List<Reward> generateReward(LootContext context) {
        LootTable table = context.getLevel().getServer().reloadableRegistries().getLootTable(lootTable);
        List<ItemStack> output = new ArrayList<>();
        table.getRandomItems(context, output::add);
        return ItemUtil.condense(output).stream().<Reward>map(ItemReward::new).toList();
    }
}
