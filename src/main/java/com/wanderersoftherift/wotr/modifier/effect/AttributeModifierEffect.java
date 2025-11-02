package com.wanderersoftherift.wotr.modifier.effect;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import com.wanderersoftherift.wotr.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

public record AttributeModifierEffect(ResourceLocation id, Holder<Attribute> attribute, double minRoll, double maxRoll,
        AttributeModifier.Operation operation) implements ModifierEffect {

    public static final MapCodec<AttributeModifierEffect> MODIFIER_CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance
                    .group(ResourceLocation.CODEC.fieldOf("id").forGetter(AttributeModifierEffect::id),
                            Attribute.CODEC.fieldOf("attribute").forGetter(AttributeModifierEffect::attribute),
                            Codec.DOUBLE.fieldOf("min_roll").forGetter(AttributeModifierEffect::minRoll),
                            Codec.DOUBLE.fieldOf("max_roll").forGetter(AttributeModifierEffect::maxRoll),
                            AttributeModifier.Operation.CODEC.fieldOf("operation")
                                    .forGetter(AttributeModifierEffect::operation))
                    .apply(instance, AttributeModifierEffect::new));

    @Override
    public MapCodec<? extends ModifierEffect> getCodec() {
        return MODIFIER_CODEC;
    }

    private ResourceLocation idForSlot(StringRepresentable source) {
        return this.id.withSuffix("/" + source.getSerializedName());
    }

    public AttributeModifier getModifier(double roll, StringRepresentable source) {
        return new AttributeModifier(this.idForSlot(source), calculateModifier(roll), this.operation());
    }

    public double calculateModifier(double roll) {
        return (roll * (maxRoll - minRoll)) + minRoll;
    }

    @Override
    public void enableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {
        if (entity instanceof LivingEntity livingentity) {
            livingentity.getAttributes().addTransientAttributeModifiers(this.makeAttributeMap(roll, source));
        }
    }

    @Override
    public void disableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {
        if (entity instanceof LivingEntity livingentity) {
            livingentity.getAttributes().removeAttributeModifiers(this.makeAttributeMap(roll, source));
        }
    }

    private Multimap<Holder<Attribute>, AttributeModifier> makeAttributeMap(double roll, ModifierSource source) {
        return ImmutableMultimap.of(this.attribute, this.getModifier(roll, source));

    }

    @Override
    public List<ImageComponent> getAdvancedTooltipComponent(ItemStack stack, float roll, Style style, int tier) {
        var base = switch (this.operation()) {
            case ADD_VALUE -> getAddTooltipComponent(stack, roll, style);
            case ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL -> getMultiplyTooltipComponent(stack, roll, style);
        };
        base = new ImageComponent(ComponentUtil.mutable(base.base()).append(getTierInfoString(tier)), base.asset());
        return List.of(base);
    }

    @Override
    public List<ImageComponent> getTooltipComponent(ItemStack stack, float roll, Style style) {
        return List.of(switch (this.operation()) {
            case ADD_VALUE -> getAddTooltipComponent(stack, roll, style);
            case ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL -> getMultiplyTooltipComponent(stack, roll, style);
        });
    }

    private ImageComponent getAddTooltipComponent(ItemStack stack, float roll, Style style) {
        double calculatedRoll = calculateModifier(roll);
        float roundedValue = (float) (Math.ceil(calculatedRoll * 100) / 100);
        String sign;
        if (roundedValue > 0) {
            sign = "positive";
        } else {
            sign = "negative";
        }

        MutableComponent cmp = Component.translatable("modifier." + WanderersOfTheRift.MODID + ".attribute.add." + sign,
                roundedValue, Component.translatable(attribute.value().getDescriptionId()));
        if (style != null) {
            cmp = cmp.withStyle(style);
        }
        return new ImageComponent(cmp, WanderersOfTheRift.id("textures/tooltip/attribute/damage_attribute.png"));
    }

    private ImageComponent getMultiplyTooltipComponent(ItemStack stack, float roll, Style style) {
        double calculatedRoll = calculateModifier(roll);
        int roundedValue = (int) Math.ceil(calculatedRoll * 100);
        String sign;
        if (roundedValue > 0) {
            sign = "positive";
        } else {
            sign = "negative";
        }

        MutableComponent cmp = Component.translatable(
                "modifier." + WanderersOfTheRift.MODID + ".attribute.multiply." + sign, roundedValue,
                Component.translatable(attribute.value().getDescriptionId()));
        if (style != null) {
            cmp = cmp.withStyle(style);
        }
        return new ImageComponent(cmp, WanderersOfTheRift.id("textures/tooltip/attribute/damage_attribute.png"));
    }

    private static String formatRoll(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    public String getTierInfoString(int tier) {
        return " (T%d : %s - %s)".formatted(tier, formatRoll(this.minRoll()), formatRoll(this.maxRoll()));
    }

    private Component getTierInfo(int tier) {
        return Component.literal(getTierInfoString(tier)).withStyle(ChatFormatting.DARK_GRAY);
    }

}
