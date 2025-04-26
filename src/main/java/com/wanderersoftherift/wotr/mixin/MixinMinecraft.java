package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.gui.screen.AccessibilityOptionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    // Queueing up our custom Accessibility screen to be shown to the player if they have not completed the
    // accessibility onboarding
    @Inject(method = "addInitialScreens", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER))
    private void injectAccessibilityScreen(List<Function<Runnable, Screen>> output, CallbackInfo ci) {
        output.add(runnable -> new AccessibilityOptionsScreen(new TitleScreen(false)));
    }
}
