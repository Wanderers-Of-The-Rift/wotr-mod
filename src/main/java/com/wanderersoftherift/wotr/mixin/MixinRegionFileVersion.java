package com.wanderersoftherift.wotr.mixin;

import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

@Mixin(RegionFileVersion.class)
public class MixinRegionFileVersion {
    @SuppressWarnings("StaticVariableName")
    @Shadow
    @Final
    public static RegionFileVersion VERSION_LZ4;

    @Shadow
    private static volatile RegionFileVersion selected;

    @Redirect(method = { "lambda$static$5", "lambda$static$3",
            "lambda$static$1" }, at = @At(value = "NEW", target = "(Ljava/io/OutputStream;)Ljava/io/BufferedOutputStream;"))
    private static BufferedOutputStream biggerBufferedOutputStream(OutputStream out) {
        return new BufferedOutputStream(out, 8192);
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void lz4ByDefault(CallbackInfo ci) {
        selected = RegionFileVersion.VERSION_LZ4;
    }
}
