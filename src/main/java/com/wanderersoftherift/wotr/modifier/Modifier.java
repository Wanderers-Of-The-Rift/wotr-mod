package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Modifier {
    public static final Codec<Modifier> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ModifierTier.CODEC.listOf().fieldOf("tiers").forGetter(Modifier::getModifierTierList),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(Modifier::getColor)
    ).apply(inst, Modifier::new));
    public static final Codec<Holder<Modifier>> CODEC = RegistryFixedCodec.create(WotrRegistries.Keys.MODIFIERS);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Modifier>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.MODIFIERS);

    private final Map<Integer, ModifierTier> modifierTiers;
    private final int color;

    public Modifier(List<ModifierTier> modifierTiers, int color) {
        this.modifierTiers = modifierTiers.stream().collect(Collectors.toMap(ModifierTier::getTier, tier -> tier));
        this.color = color;
    }

    public List<ModifierTier> getModifierTierList() {
        return modifierTiers.values().stream().toList();
    }

    public int getColor() {
        return color;
    }

    public void enableModifier(float roll, Entity entity, ModifierSource source, int tier) {
        if (!modifierTiers.containsKey(tier)) {
            return;
        }
        modifierTiers.get(tier).enableModifier(roll, entity, source);
    }

    public void disableModifier(float roll, Entity entity, ModifierSource source, int tier) {
        if (!modifierTiers.containsKey(tier)) {
            return;
        }
        modifierTiers.get(tier).disableModifier(roll, entity, source);
    }

    public List<TooltipComponent> getTooltipComponent(
            ItemStack stack,
            float roll,
            ModifierInstance instance,
            int color) {
        if (!modifierTiers.containsKey(instance.tier())) {
            return List.of();
        }
        return modifierTiers.get(instance.tier())
                .getTooltipComponent(
                        stack, roll, instance, color
                );
    }
}