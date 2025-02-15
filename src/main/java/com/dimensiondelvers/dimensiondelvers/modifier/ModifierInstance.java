package com.dimensiondelvers.dimensiondelvers.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;


public class ModifierInstance {

    public static Codec<ModifierInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Modifier.CODEC.fieldOf("modifier").forGetter(ModifierInstance::getModifier),
            Codec.FLOAT.fieldOf("roll").forGetter(ModifierInstance::getRoll)
    ).apply(inst, ModifierInstance::new));

    public final Holder<Modifier> modifier;

    public final float roll;

    public ModifierInstance(Holder<Modifier> modifier, float roll) {
        this.modifier = modifier;
        this.roll = roll;
    }

    public Holder<Modifier> getModifier() {
        return modifier;
    }

    public float getRoll() {
        return roll;
    }
}
