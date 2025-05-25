package com.wanderersoftherift.wotr.core.guild.currency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.network.guild.WalletUpdatePayload;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
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

    public void set(Holder<Currency> currency, int amount, @Nullable Player player) {
        currencies.put(currency, amount);
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new WalletUpdatePayload(Map.of(currency, amount)));
        }
    }

    public void add(Holder<Currency> currency, int amount, @Nullable Player player) {
        set(currency, get(currency) + amount, player);
    }

    public boolean spend(Holder<Currency> currency, int amount, @Nullable Player player) {
        int available = get(currency);
        if (available >= amount) {
            set(currency, available - amount, player);
            return true;
        }
        return false;
    }

}
