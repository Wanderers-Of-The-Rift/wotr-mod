package com.wanderersoftherift.wotr.item.socket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record GearSocket(RunegemShape shape, Optional<ModifierInstance> modifier, Optional<RunegemData> runegem) {

    public static Codec<GearSocket> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(RunegemShape.CODEC.fieldOf("shape").forGetter(GearSocket::shape),
                    ModifierInstance.CODEC.optionalFieldOf("modifier").forGetter(GearSocket::modifier),
                    RunegemData.CODEC.optionalFieldOf("runegem").forGetter(GearSocket::runegem))
            .apply(inst, GearSocket::new));

    public static GearSocket getRandomSocket(RandomSource random) {
        RunegemShape shape = RunegemShape.getRandomShape(random);
        return new GearSocket(shape, Optional.empty(), Optional.empty());
    }

    public boolean isEmpty() {
        return runegem.isEmpty() || modifier.isEmpty();
    }

    public boolean canBeApplied(RunegemData runegemData) {
        return isEmpty() && this.shape().equals(runegemData.shape());
    }

    public GearSocket applyRunegem(ItemStack stack, ItemStack runegem, Level level) {
        RunegemData runegemData = runegem.get(ModDataComponentType.RUNEGEM_DATA);
        if (runegemData == null) {
            return new GearSocket(this.shape(), Optional.empty(), Optional.empty());
        }
        Optional<Holder<Modifier>> modifierHolder = runegemData.getRandomModifierForItem(stack, level);
        if (modifierHolder.isEmpty()) {
            WanderersOfTheRift.LOGGER.error("Failed to get random modifier for runegem: " + stack);
            return new GearSocket(this.shape(), Optional.empty(), Optional.empty());
        }
        return new GearSocket(this.shape(), Optional.of(ModifierInstance.of(modifierHolder.get(), level.random)),
                Optional.of(runegemData));
    }
}