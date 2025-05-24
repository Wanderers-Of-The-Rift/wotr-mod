package com.wanderersoftherift.wotr.core.guild.currency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Wallet implements WalletAccessor {
    public static final Codec<Wallet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Currency.CODEC, Codec.INT).fieldOf("currencies").forGetter(x -> x.currencies)
    ).apply(instance, Wallet::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Wallet> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(LinkedHashMap::new, Currency.STREAM_CODEC, ByteBufCodecs.INT), x -> x.currencies,
            Wallet::new
    );

    private final Object2IntMap<Holder<Currency>> currencies;

    public Wallet() {
        currencies = new Object2IntOpenHashMap<>();
    }

    private Wallet(Map<Holder<Currency>, Integer> values) {
        currencies = new Object2IntOpenHashMap<>(values);
    }

    public List<Holder<Currency>> availableCurrencies() {
        return currencies.object2IntEntrySet()
                .stream()
                .filter(x -> x.getIntValue() > 0)
                .map(Map.Entry::getKey)
                .toList();
    }

    public int get(Holder<Currency> currency) {
        return currencies.getOrDefault(currency, 0);
    }

    @Override
    public void set(Holder<Currency> currency, int amount) {
        currencies.put(currency, amount);
    }
}
