package com.wanderersoftherift.wotr.core.quest.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

/**
 * This reward provides an amount of currency when the quest is completed
 *
 * @param currency The currency to award upon completion
 */
public record CurrencyReward(Holder<Currency> currency, int amount) implements Reward {

    public static final MapCodec<CurrencyReward> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Currency.CODEC.fieldOf("currency").forGetter(CurrencyReward::currency),
                    Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(CurrencyReward::amount)
            ).apply(instance, CurrencyReward::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CurrencyReward> STREAM_CODEC = StreamCodec.composite(
            Currency.STREAM_CODEC, CurrencyReward::currency, ByteBufCodecs.INT, CurrencyReward::amount,
            CurrencyReward::new
    );

    public static final DualCodec<CurrencyReward> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<CurrencyReward> getType() {
        return TYPE;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public void apply(Player player) {
        player.getData(WotrAttachments.WALLET).add(currency, amount);
    }

}
