package com.wanderersoftherift.wotr.item.socket;

import com.wanderersoftherift.wotr.item.runegem.RuneGemShape;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

// Vanilla Equivalent ItemEnchantments
public record GearSocket(RuneGemShape runeGemShape, ModifierInstance modifier, ItemStack runegem) {
    // Further define runeGemShape
    // Needs to have a modifier and a Runegem
    // should eventually also have the roll of the modifier (and a tier?)
    public static Codec<GearSocket> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RuneGemShape.CODEC.fieldOf("shape").forGetter(GearSocket::runeGemShape),
            ModifierInstance.CODEC.optionalFieldOf("modifier", null).forGetter(GearSocket::modifier),
            ItemStack.CODEC.optionalFieldOf("runegem", null).forGetter(GearSocket::runegem)
    ).apply(inst, GearSocket::new));

    public boolean isEmpty() {
        return runegem == null;
    }

    public boolean canBeApplied(RunegemData runegemData) {
        return isEmpty() && runeGemShape().equals(runegemData.shape());
    }
}