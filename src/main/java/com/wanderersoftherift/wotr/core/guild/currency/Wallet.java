package com.wanderersoftherift.wotr.core.guild.currency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.network.guild.WalletReplicationPayload;
import com.wanderersoftherift.wotr.network.guild.WalletUpdatePayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Player attachment that stores currencies
 */
public class Wallet {

    private final IAttachmentHolder holder;

    @NotNull private final Wallet.Data data;

    public Wallet(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private Wallet(@NotNull IAttachmentHolder holder, @Nullable Wallet.Data data) {
        this.holder = holder;
        this.data = Objects.requireNonNullElseGet(data, Data::new);
    }

    public static IAttachmentSerializer<Tag, Wallet> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(Data.CODEC, Wallet::new, x -> x.data);
    }

    /**
     * @return A list of all non-zero currency in the wallet
     */
    public List<Holder<Currency>> availableCurrencies() {
        return data.currencies.object2IntEntrySet()
                .stream()
                .filter(x -> x.getIntValue() > 0)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * @param currency
     * @return The amount of the given currency in the wallet
     */
    public int get(Holder<Currency> currency) {
        return data.currencies.getOrDefault(currency, 0);
    }

    /**
     * @param currency
     * @param amount   The amount of the given currency to set in the wallet
     */
    public void set(Holder<Currency> currency, int amount) {
        data.currencies.put(currency, amount);
        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new WalletUpdatePayload(Map.of(currency, amount)));
        }
    }

    public Map<Holder<Currency>, Integer> getAll() {
        return Collections.unmodifiableMap(data.currencies);
    }

    public void replaceAll(Map<Holder<Currency>, Integer> content) {
        data.currencies.clear();
        data.currencies.putAll(content);
        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new WalletReplicationPayload(data.currencies));
        }
    }

    /**
     * @param currency
     * @param amount   The amount of the given currency to add to the wallet
     */
    public void add(Holder<Currency> currency, int amount) {
        set(currency, get(currency) + amount);
    }

    /**
     * @param currency
     * @param amount   The amount of the given currency to remove from the wallet, if available
     * @return Whether the necessary amount of currency was available
     */
    public boolean spend(Holder<Currency> currency, int amount) {
        int available = get(currency);
        if (available >= amount) {
            set(currency, available - amount);
            return true;
        }
        return false;
    }

    private record Data(Object2IntMap<Holder<Currency>> currencies) {
        public static final Codec<Wallet.Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Currency.CODEC, Codec.INT)
                        .<Object2IntMap<Holder<Currency>>>xmap(Object2IntOpenHashMap::new, x -> x)
                        .fieldOf("currencies")
                        .forGetter(Wallet.Data::currencies)
        ).apply(instance, Wallet.Data::new));

        public Data() {
            this(new Object2IntOpenHashMap<>());
        }
    }
}
