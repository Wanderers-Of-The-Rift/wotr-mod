package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ModifierInstance(Holder<Modifier> modifier, int tier, float roll) {

    public static final Codec<ModifierInstance> CODEC = RecordCodecBuilder
            .create(inst -> inst
                    .group(Modifier.CODEC.fieldOf("modifier").forGetter(ModifierInstance::modifier),
                            Codec.INT.optionalFieldOf("tier", 0).forGetter(ModifierInstance::tier),
                            Codec.FLOAT.fieldOf("roll").forGetter(ModifierInstance::roll))
                    .apply(inst, ModifierInstance::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifierInstance> STREAM_CODEC = StreamCodec.composite(
            Modifier.STREAM_CODEC, ModifierInstance::modifier, ByteBufCodecs.INT, ModifierInstance::tier,
            ByteBufCodecs.FLOAT, ModifierInstance::roll, ModifierInstance::new);

    public static ModifierInstance of(Holder<Modifier> modifier, int tier, RandomSource random) {
        return new ModifierInstance(modifier, tier, random.nextFloat());
    }

    public List<TooltipComponent> getTooltipComponent(ItemStack stack) {
        return modifier.value().getTooltipComponent(stack, roll, this);
    }

    public List<AbstractModifierEffect> effects() {
        return modifier.value()
                .getModifierTierList()
                .stream()
                .filter(it -> it.getTier() == tier)
                .flatMap(it -> it.getModifierEffects().stream())
                .toList();
    }
}
