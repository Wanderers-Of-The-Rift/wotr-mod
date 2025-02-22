package com.wanderersoftherift.wotr.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Mixin(WorldDimensions.class)
public class MixinWorldDimensions {
    @WrapOperation(method = "<init>(Lnet/minecraft/core/Registry;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;listElements()Ljava/util/stream/Stream;"))
    private static Stream<Holder.Reference<LevelStem>> removeNull(Registry<LevelStem> instance, Operation<Stream<Holder.Reference<LevelStem>>> original){
        return original.call(instance).filter(x -> x != null && x.value() != null); // this can be null because it's set to null in TempLevelManager
    }

}
