package com.wanderersoftherift.wotr.core.quest.reward.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import com.wanderersoftherift.wotr.core.quest.reward.ReputationReward;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Generates a potentially randomised reputation reward
 * 
 * @param guilds A list of guilds, of which one will be selected for the reward
 * @param amount A provider for the amount to reward
 */
public record ReputationRewardProvider(List<Holder<Guild>> guilds, NumberProvider amount) implements RewardProvider {

    public static final MapCodec<ReputationRewardProvider> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.withAlternative(Guild.CODEC.listOf(), Guild.CODEC, List::of)
                            .fieldOf("guilds")
                            .forGetter(ReputationRewardProvider::guilds),
                    NumberProviders.CODEC.fieldOf("amount").forGetter(ReputationRewardProvider::amount)
            ).apply(instance, ReputationRewardProvider::new));

    @Override
    public MapCodec<? extends RewardProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Reward> generateReward(LootParams params) {
        LootContext lootContext = new LootContext.Builder(params).create(Optional.empty());
        Holder<Guild> guild;
        if (guilds.size() > 1) {
            guild = guilds.get(lootContext.getRandom().nextInt(guilds.size()));
        } else {
            guild = guilds.getFirst();
        }
        return List.of(new ReputationReward(guild, amount.getInt(lootContext)));
    }
}
