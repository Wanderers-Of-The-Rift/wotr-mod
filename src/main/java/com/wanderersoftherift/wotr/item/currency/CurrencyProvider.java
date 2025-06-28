package com.wanderersoftherift.wotr.item.currency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record CurrencyProvider(Holder<Currency> currency, int amount) implements ConsumableListener, TooltipProvider {
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
        if (entity instanceof ServerPlayer player) {
            Component currencyName = Component.translatable(
                    WanderersOfTheRift.translationId("currency", ResourceLocation.parse(currency.getRegisteredName())));
            player.displayClientMessage(Component.translatable(
                    WanderersOfTheRift.translationId("message", "currency_obtained"), amount, currencyName), false);
        }
    }

    @Override
    public void addToTooltip(
            Item.@NotNull TooltipContext context,
            Consumer<Component> tooltipAdder,
            @NotNull TooltipFlag tooltipFlag) {
        tooltipAdder.accept(
                Component
                        .translatable(WanderersOfTheRift.translationId("tooltip", "currency_bag"), amount,
                                Component.translatable(WanderersOfTheRift.translationId("currency",
                                        ResourceLocation.parse(currency.getRegisteredName()))))
                        .withStyle(ChatFormatting.GRAY)
        );
    }
}
