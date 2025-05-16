package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

import static com.wanderersoftherift.wotr.init.ModModifiers.MODIFIER_KEY;

public class Modifier {
    public static final Codec<Modifier> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ModifierTier.CODEC.listOf().fieldOf("tiers").forGetter(Modifier::getModifierTierList)
    ).apply(inst, Modifier::new));
    public static final Codec<Holder<Modifier>> CODEC = RegistryFixedCodec.create(MODIFIER_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Modifier>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(MODIFIER_KEY);

    private final Map<Integer, ModifierTier> modifierTiers;

    public Modifier(List<ModifierTier> modifierTiers) {
        this.modifierTiers = modifierTiers.stream().collect(Collectors.toMap(ModifierTier::getTier, tier -> tier));
    }

    public List<ModifierTier> getModifierTierList() {
        return modifierTiers.values().stream().toList();
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
            ChatFormatting chatFormatting) {
        if (!modifierTiers.containsKey(instance.tier())) {
            return List.of();
        }
        return modifierTiers.get(instance.tier())
                .getTooltipComponent(
                        stack, roll, instance, chatFormatting
                );
    }
}