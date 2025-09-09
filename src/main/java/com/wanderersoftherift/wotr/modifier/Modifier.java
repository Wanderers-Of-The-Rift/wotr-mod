package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class Modifier {
    public static final Codec<Modifier> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ModifierEffect.DIRECT_CODEC.listOf().listOf().fieldOf("tiers").forGetter(Modifier::getModifierTierList),
            Style.Serializer.CODEC.optionalFieldOf("style", Style.EMPTY.withColor(ChatFormatting.GRAY))
                    .forGetter(Modifier::getStyle))
            .apply(inst, Modifier::new)
    );
    public static final Codec<Holder<Modifier>> CODEC = RegistryFixedCodec.create(WotrRegistries.Keys.MODIFIERS);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Modifier>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.MODIFIERS);

    private final List<List<ModifierEffect>> modifierTiers;
    private final Style style;

    public Modifier(List<List<ModifierEffect>> modifierTiers, Style style) {
        this.modifierTiers = modifierTiers;
        this.style = style;
    }

    public List<List<ModifierEffect>> getModifierTierList() {
        return modifierTiers;
    }

    public Style getStyle() {
        return style;
    }

    public void enableModifier(float roll, Entity entity, ModifierSource source, int tier) {
        if (tier <= 0 || tier > modifierTiers.size()) {
            return;
        }
        List<ModifierEffect> tierEffects = modifierTiers.get(tier - 1);
        for (int i = 0; i < tierEffects.size(); i++) {
            ModifierEffect it = tierEffects.get(i);
            it.enableModifier(roll, entity, source, i);
        }
    }

    public void disableModifier(float roll, Entity entity, ModifierSource source, int tier) {
        if (tier <= 0 || tier > modifierTiers.size()) {
            return;
        }
        List<ModifierEffect> tierEffects = modifierTiers.get(tier - 1);
        for (int i = 0; i < tierEffects.size(); i++) {
            ModifierEffect it = tierEffects.get(i);
            it.disableModifier(roll, entity, source, i);
        }
    }

    public List<ImageComponent> getTooltipComponent(ItemStack stack, float roll, ModifierInstance instance) {
        if (instance.tier() <= 0 || instance.tier() > modifierTiers.size()) {
            return Collections.emptyList();
        }
        return modifierTiers.get(instance.tier() - 1)
                .stream()
                .flatMap(it -> it.getTooltipComponent(
                        stack, roll, instance.modifier().value().getStyle()
                ).stream())
                .toList();
    }

    public List<ImageComponent> getAdvancedTooltipComponent(ItemStack stack, float roll, ModifierInstance instance) {
        if (instance.tier() <= 0 || instance.tier() > modifierTiers.size()) {
            return Collections.emptyList();
        }
        return modifierTiers.get(instance.tier() - 1)
                .stream()
                .flatMap(it -> it.getAdvancedTooltipComponent(
                        stack, roll, instance.modifier().value().getStyle(), instance.tier()
                ).stream())
                .toList();
    }

    public List<ModifierEffect> getModifierTier(int tier) {
        if (tier <= 0 || tier > modifierTiers.size()) {
            return null;
        }
        return modifierTiers.get(tier - 1); // why is modifierTiers not just a list??
    }
}