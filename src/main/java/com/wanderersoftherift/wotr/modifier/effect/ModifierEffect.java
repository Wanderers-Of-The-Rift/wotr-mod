package com.wanderersoftherift.wotr.modifier.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.MODIFIER_EFFECTS;
import static com.wanderersoftherift.wotr.init.WotrRegistries.MODIFIER_TYPES;

public interface ModifierEffect {
    Codec<ModifierEffect> DIRECT_CODEC = MODIFIER_TYPES.byNameCodec()
            .dispatch(ModifierEffect::getCodec, Function.identity());

    Codec<Holder<ModifierEffect>> CODEC = RegistryFixedCodec.create(MODIFIER_EFFECTS);

    StreamCodec<RegistryFriendlyByteBuf, Holder<ModifierEffect>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(MODIFIER_EFFECTS);

    MapCodec<? extends ModifierEffect> getCodec();

    void enableModifier(double roll, Entity entity, ModifierSource source, int effectIndex);

    void disableModifier(double roll, Entity entity, ModifierSource source, int effectIndex);

    List<ImageComponent> getTooltipComponent(ItemStack stack, float roll, Style style);

    List<ImageComponent> getAdvancedTooltipComponent(ItemStack stack, float roll, Style style, int tier);
}
