package com.wanderersoftherift.wotr.mixin;

import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.DelegatingOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DelegatingOps.class)
public interface AccessorDelegatingOps {
    @Accessor
    DynamicOps getDelegate();
}
