package com.wanderersoftherift.wotr.item.currency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record CurrencyProvider(Holder<Currency> currency, int amount) implements ConsumableListener {
    public static final Codec<CurrencyProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Currency.CODEC.fieldOf("currency").forGetter(CurrencyProvider::currency),
            Codec.INT.fieldOf("amount").forGetter(CurrencyProvider::amount)
    ).apply(instance, CurrencyProvider::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CurrencyProvider> STREAM_CODEC = StreamCodec.composite(
            Currency.STREAM_CODEC, CurrencyProvider::currency, ByteBufCodecs.INT, CurrencyProvider::amount,
            CurrencyProvider::new
    );

    @Override
    public void onConsume(
            Level level,
            @NotNull LivingEntity entity,
            @NotNull ItemStack stack,
            @NotNull Consumable consumable) {
        if (level.isClientSide()) {
            return;
        }
        entity.getData(WotrAttachments.WALLET).add(currency, amount);
    }
}
