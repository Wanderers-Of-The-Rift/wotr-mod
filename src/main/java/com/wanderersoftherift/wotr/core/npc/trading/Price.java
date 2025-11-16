package com.wanderersoftherift.wotr.core.npc.trading;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.core.guild.currency.Wallet;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A data component for storing the price of an item. A price is composed of the quantity of one or more currencies.
 * 
 * @param amounts
 */
public record Price(Object2IntMap<Holder<Currency>> amounts) {
    public static final Codec<Price> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Currency.CODEC, Codec.intRange(1, Integer.MAX_VALUE))
                    .xmap(x -> Object2IntMaps.unmodifiable(new Object2IntArrayMap<>(x)), x -> x)
                    .fieldOf("amounts")
                    .forGetter(Price::amounts)
    ).apply(instance, Price::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Price> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2IntArrayMap::new, ByteBufCodecs.holderRegistry(WotrRegistries.Keys.CURRENCIES),
                    ByteBufCodecs.INT),
            Price::amounts, Price::new
    );

    public boolean canPay(LivingEntity entity) {
        Wallet wallet = entity.getData(WotrAttachments.WALLET);
        for (var entry : amounts.object2IntEntrySet()) {
            if (wallet.get(entry.getKey()) < entry.getIntValue()) {
                return false;
            }
        }
        return true;
    }

    public Collection<Either<FormattedText, TooltipComponent>> getTooltips() {
        List<Either<FormattedText, TooltipComponent>> result = new ArrayList<>();
        amounts.forEach((currency, amount) -> result.add(Either.right(new ImageComponent(
                Component.literal(amount.toString()).append(" ").append(Currency.getDisplayName(currency)),
                currency.value().icon()))));
        return result;
    }

}
