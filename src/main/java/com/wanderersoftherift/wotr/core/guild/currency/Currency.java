package com.wanderersoftherift.wotr.core.guild.currency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Defines and describes a currency
 * 
 * @param icon
 * @param smallIcon
 */
public record Currency(ResourceLocation icon, Optional<ResourceLocation> smallIcon) {
    public static final Codec<Currency> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("icon").forGetter(Currency::icon),
            ResourceLocation.CODEC.optionalFieldOf("small_icon").forGetter(Currency::smallIcon)
    ).apply(instance, Currency::new));

    public static final Codec<Holder<Currency>> CODEC = RegistryFixedCodec.create(WotrRegistries.Keys.CURRENCIES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Currency>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.CURRENCIES);

    public static Component getDisplayName(Holder<Currency> currency) {
        ResourceLocation loc = ResourceLocation.parse(currency.getRegisteredName());
        return Component.translatable(loc.toLanguageKey("currency"));
    }

}
