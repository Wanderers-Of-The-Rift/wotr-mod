package com.wanderersoftherift.wotr.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.currency.Currency;
import com.wanderersoftherift.wotr.core.npc.trading.Price;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.loot.WotrLootItemFunctionTypes;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SetPriceFunction extends LootItemConditionalFunction {

    public static final MapCodec<SetPriceFunction> CODEC = RecordCodecBuilder
            .mapCodec(instance -> commonFields(instance).and(
                    Codec.unboundedMap(Currency.CODEC, NumberProviders.CODEC)
                            .fieldOf("amounts")
                            .forGetter(SetPriceFunction::getAmounts)
            ).apply(instance, SetPriceFunction::new)
            );

    private final Map<Holder<Currency>, NumberProvider> amounts;

    public SetPriceFunction(List<LootItemCondition> predicates, Map<Holder<Currency>, NumberProvider> amounts) {
        super(predicates);
        this.amounts = amounts;
    }

    public Map<Holder<Currency>, NumberProvider> getAmounts() {
        return Collections.unmodifiableMap(amounts);
    }

    @Override
    public @NotNull LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return WotrLootItemFunctionTypes.SET_PRICE_FUNCTION.get();
    }

    @Override
    protected @NotNull ItemStack run(ItemStack stack, @NotNull LootContext context) {
        Object2IntMap<Holder<Currency>> priceParts = new Object2IntArrayMap<>();
        amounts.forEach((currency, valueProvider) -> {
            int value = valueProvider.getInt(context);
            if (value > 0) {
                priceParts.put(currency, value);
            }
        });
        stack.set(WotrDataComponentType.PRICE, new Price(priceParts));
        return stack;
    }
}
