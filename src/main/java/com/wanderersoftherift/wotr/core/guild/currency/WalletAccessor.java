package com.wanderersoftherift.wotr.core.guild.currency;

import net.minecraft.core.Holder;

import java.util.List;

/**
 * Interface for working with the wallet.
 */
public interface WalletAccessor {

    /**
     * @return A list of all non-zero currency in the wallet
     */
    List<Holder<Currency>> availableCurrencies();

    /**
     * @param currency
     * @return The amount of the given currency in the wallet
     */
    int get(Holder<Currency> currency);

    /**
     * @param currency
     * @param amount   The amount of the given currency to set in the wallet
     */
    void set(Holder<Currency> currency, int amount);

    /**
     * @param currency
     * @param amount   The amount of the given currency to add to the wallet
     */
    default void add(Holder<Currency> currency, int amount) {
        set(currency, get(currency) + amount);
    }

    /**
     * @param currency
     * @param amount   The amount of the given currency to remove from the wallet, if available
     * @return Whether the necessary amount of currency was available
     */
    default boolean spend(Holder<Currency> currency, int amount) {
        int available = get(currency);
        if (available >= amount) {
            set(currency, available - amount);
            return true;
        }
        return false;
    }
}
