package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.client.MainAttackHandler;
import com.wanderersoftherift.wotr.gui.screen.settings.AccessibilityOptionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow
    @Nullable public LocalPlayer player;

    // Queueing up our custom Accessibility screen to be shown to the player if they have not completed the
    // accessibility onboarding
    @Inject(method = "addInitialScreens", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER))
    private void injectAccessibilityScreen(List<Function<Runnable, Screen>> output, CallbackInfo ci) {
        output.add(runnable -> new AccessibilityOptionsScreen(new TitleScreen(false)));
    }

    // custom combat
    @Inject(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"), cancellable = true)
    private void wotrContinueAttack(boolean leftClick, CallbackInfo ci) {
        if (MainAttackHandler.doAttack(player)) {
            ci.cancel();
        }
    }
}
