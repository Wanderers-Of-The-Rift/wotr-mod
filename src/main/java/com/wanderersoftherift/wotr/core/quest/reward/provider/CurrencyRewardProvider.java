package com.wanderersoftherift.wotr.core.quest.reward.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.currency.Currency;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import com.wanderersoftherift.wotr.core.quest.reward.CurrencyReward;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Generates a potentially randomised currency reward
 * 
 * @param currency The currency to reward
 * @param amount   A provider for the amount to reward
 */
public record CurrencyRewardProvider(Holder<Currency> currency, NumberProvider amount) implements RewardProvider {

    public static final MapCodec<CurrencyRewardProvider> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Currency.CODEC.fieldOf("currency").forGetter(CurrencyRewardProvider::currency),
                    NumberProviders.CODEC.fieldOf("amount").forGetter(CurrencyRewardProvider::amount)
            ).apply(instance, CurrencyRewardProvider::new));

    @Override
    public MapCodec<? extends RewardProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Reward> generateReward(LootContext context) {
        return List.of(
                new CurrencyReward(currency, amount.getInt(context)));
    }
}
