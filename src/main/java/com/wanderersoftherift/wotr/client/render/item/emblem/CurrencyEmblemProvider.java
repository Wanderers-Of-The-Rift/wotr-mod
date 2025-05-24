package com.wanderersoftherift.wotr.client.render.item.emblem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.currency.CurrencyProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CurrencyEmblemProvider implements EmblemProvider {
    public static final MapCodec<CurrencyEmblemProvider> CODEC = Codec.unit(new CurrencyEmblemProvider())
            .fieldOf("currency");

    @Override
    public ResourceLocation getIcon(ItemStack stack) {
        CurrencyProvider currencyProvider = stack.get(WotrDataComponentType.CURRENCY_PROVIDER);
        if (currencyProvider != null) {
            Currency value = currencyProvider.currency().value();
            return value.smallIcon().orElse(value.icon());
        }
        return null;
    }

    @Override
    public MapCodec<? extends EmblemProvider> type() {
        return CODEC;
    }
}
