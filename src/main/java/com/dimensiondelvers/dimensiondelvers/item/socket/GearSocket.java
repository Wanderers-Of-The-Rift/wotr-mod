package com.dimensiondelvers.dimensiondelvers.item.socket;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemShape;
import com.dimensiondelvers.dimensiondelvers.modifier.Modifier;
import com.dimensiondelvers.dimensiondelvers.modifier.ModifierInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

// Vanilla Equivalent ItemEnchantments
public record GearSocket(
        RunegemShape shape,
        Optional<ModifierInstance> modifier,
        Optional<ItemStack> runegem
) {
    public static Codec<GearSocket> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RunegemShape.CODEC.fieldOf("shape").forGetter(GearSocket::shape),
            ModifierInstance.CODEC.optionalFieldOf("modifier").forGetter(GearSocket::modifier),
            ItemStack.CODEC.optionalFieldOf("runegem").forGetter(GearSocket::runegem)
    ).apply(inst, GearSocket::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GearSocket> STREAM_CODEC = StreamCodec.composite(
            RunegemShape.STREAM_CODEC,
            GearSocket::shape,
            ByteBufCodecs.optional(ModifierInstance.STREAM_CODEC),
            GearSocket::modifier,
            ByteBufCodecs.optional(ItemStack.STREAM_CODEC),
            GearSocket::runegem,
            GearSocket::new
    );

    public boolean isEmpty() {
        return runegem.isEmpty() || runegem.get().isEmpty();
    }

    public boolean canBeApplied(RunegemData runegemData) {
        return isEmpty() && this.shape().equals(runegemData.shape());
    }

    public GearSocket applyRunegem(ItemStack stack, Level level) {
        RunegemData runegemData = stack.get(ModDataComponentType.RUNEGEM_DATA);
        if (runegemData == null) {
            return new GearSocket(this.shape(), Optional.empty(), Optional.empty());
        }
        Optional<Holder<Modifier>> modifierHolder = runegemData.getRandomModifier(level);
        if (modifierHolder.isEmpty()) {
            DimensionDelvers.LOGGER.error("Failed to get random modifier for runegem: " + stack);
            return new GearSocket(this.shape(), Optional.empty(), Optional.empty());
        }
        return new GearSocket(this.shape(), Optional.of(ModifierInstance.of(modifierHolder.get(), level.random)), Optional.of(stack));
    }
}