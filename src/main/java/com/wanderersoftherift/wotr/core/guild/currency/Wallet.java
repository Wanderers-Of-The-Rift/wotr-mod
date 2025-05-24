package com.wanderersoftherift.wotr.core.guild.currency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Map;

public class Wallet {
    public static final Codec<Wallet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Currency.CODEC, Codec.INT).fieldOf("currencies").forGetter(x -> x.currencies)
    ).apply(instance, Wallet::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Wallet> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(capacity -> new Object2IntAVLTreeMap<>(), Currency.STREAM_CODEC, ByteBufCodecs.INT),
            x -> x.currencies, Wallet::new
    );

    private final Object2IntMap<Holder<Currency>> currencies;

    public Wallet() {
        currencies = new Object2IntAVLTreeMap<>();
    }

    private Wallet(Map<Holder<Currency>, Integer> values) {
        currencies = new Object2IntAVLTreeMap<>(values);
    }

    public int get(Holder<Currency> currency) {
        return currencies.getOrDefault(currency, 0);
    }

    public void set(Holder<Currency> currency, int amount) {
        currencies.put(currency, amount);
    }

    public void add(Holder<Currency> currency, int amount) {
        currencies.mergeInt(currency, amount, Integer::sum);
    }

    public boolean spend(Holder<Currency> currency, int amount) {
        int available = get(currency);
        if (available >= amount) {
            set(currency, available - amount);
            return true;
        }
        return false;
    }

}
