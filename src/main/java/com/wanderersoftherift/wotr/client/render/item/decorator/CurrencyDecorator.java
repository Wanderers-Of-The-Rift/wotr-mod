package com.wanderersoftherift.wotr.client.render.item.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.currency.CurrencyProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CurrencyDecorator implements DecoratorSource {
    public static final MapCodec<CurrencyDecorator> CODEC = Codec.unit(new CurrencyDecorator()).fieldOf("currency");

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
    public MapCodec<? extends DecoratorSource> type() {
        return CODEC;
    }
}
