package com.wanderersoftherift.wotr.item.implicit;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModDatapackRegistries;
import com.wanderersoftherift.wotr.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;

public record ImplicitConfig(HolderSet<Modifier> implicitModifiers) {
    public static final ImplicitConfig DEFAULT = new ImplicitConfig(HolderSet.empty());
    public static Codec<ImplicitConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryCodecs.homogeneousList(ModDatapackRegistries.MODIFIER_KEY).fieldOf("modifiers").forGetter(ImplicitConfig::implicitModifiers)
    ).apply(inst, ImplicitConfig::new));
}
