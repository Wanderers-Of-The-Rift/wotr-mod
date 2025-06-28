package com.wanderersoftherift.wotr.core.guild.trading;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * A trade listing describes trade that can be offered. All trade listings have a price (in terms of one or more
 * currencies) and an output item that is supplied by the trade
 */
public class TradeListing {

    public static final Codec<TradeListing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Currency.CODEC, Codec.INT).fieldOf("price").forGetter(TradeListing::getPrice),
            ItemStack.CODEC.fieldOf("outputItem").forGetter(TradeListing::getOutputItem)
    ).apply(instance, TradeListing::new));

    // TODO: Guild the entry is for

    // Requirements
    // TODO: tier, unlock, etc

    // Cost
    private final Object2IntMap<Holder<Currency>> price;

    // Output
    private final ItemStack outputItem;

    public TradeListing(Map<Holder<Currency>, Integer> price, ItemStack outputItem) {
        this.price = new Object2IntArrayMap<>(price);
        this.outputItem = outputItem.copy();
    }

    public Object2IntMap<Holder<Currency>> getPrice() {
        return price;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    public static Builder create(ItemStack outputItem) {
        return new Builder(outputItem);
    }

    public static class Builder {
        private final Object2IntMap<Holder<Currency>> price = new Object2IntArrayMap<>();
        private final ItemStack outputItem;

        private Builder(ItemStack outputItem) {
            this.outputItem = outputItem;
        }

        public Builder addToPrice(Holder<Currency> currency, int amount) {
            price.put(currency, amount);
            return this;
        }

        public TradeListing build() {
            return new TradeListing(price, outputItem);
        }
    }

}
