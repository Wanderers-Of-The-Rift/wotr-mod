package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ModifierInstance(Holder<Modifier> modifier, float roll) {

    public static Codec<ModifierInstance> CODEC = RecordCodecBuilder
            .create(inst -> inst
                    .group(Modifier.CODEC.fieldOf("modifier").forGetter(ModifierInstance::modifier),
                            Codec.FLOAT.fieldOf("roll").forGetter(ModifierInstance::roll))
                    .apply(inst, ModifierInstance::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifierInstance> STREAM_CODEC = StreamCodec.composite(
            Modifier.STREAM_CODEC, ModifierInstance::modifier, ByteBufCodecs.FLOAT, ModifierInstance::roll,
            ModifierInstance::new);

    public static ModifierInstance of(Holder<Modifier> modifier, RandomSource random) {
        return new ModifierInstance(modifier, random.nextFloat());
    }

    public List<TooltipComponent> getTooltipComponent(ItemStack stack, ChatFormatting chatFormatting) {
        return modifier.value().getTooltipComponent(stack, roll, this, chatFormatting);
    }

}
