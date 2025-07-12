package com.wanderersoftherift.wotr.core.guild.quest.reward.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Reward;
import com.wanderersoftherift.wotr.core.guild.quest.RewardProvider;
import com.wanderersoftherift.wotr.core.guild.quest.reward.ItemReward;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;

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
    public List<Reward> generateReward(LootParams params) {
        LootTable table = params.getLevel().getServer().reloadableRegistries().getLootTable(lootTable);
        return table.getRandomItems(params).stream().<Reward>map(ItemReward::new).toList();
    }
}
