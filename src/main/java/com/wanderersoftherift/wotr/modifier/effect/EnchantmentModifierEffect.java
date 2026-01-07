package com.wanderersoftherift.wotr.modifier.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public record EnchantmentModifierEffect(ResourceKey<Enchantment> enchant, int level) implements ModifierEffect {

    public static final MapCodec<EnchantmentModifierEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceKey.codec(Registries.ENCHANTMENT)
                            .fieldOf("enchant")
                            .forGetter(EnchantmentModifierEffect::enchant),
                    Codec.INT.fieldOf("level").forGetter(EnchantmentModifierEffect::level)
            ).apply(instance, EnchantmentModifierEffect::new)
    );

    @Override
    public MapCodec<? extends ModifierEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void enableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {

    }

    @Override
    public void disableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {

    }

    @Override
    public List<ImageComponent> getTooltipComponent(ItemStack stack, float roll, Style style) {
        return List.of(new ImageComponent(
                Component.translatable(enchant().location().toLanguageKey("enchantment")).withColor(0xdddd44), null));
    }

    @Override
    public List<ImageComponent> getAdvancedTooltipComponent(ItemStack stack, float roll, Style style, int tier) {
        return List.of(new ImageComponent(
                Component.translatable(enchant().location().toLanguageKey("enchantment")).withColor(0xdddd44), null));
    }
}
