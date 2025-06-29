package com.wanderersoftherift.wotr.mixin;

import net.minecraft.core.IdMap;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PalettedContainer.class)
public interface AccessorPalettedContainer {
    @Accessor
    IdMap getRegistry();
}
