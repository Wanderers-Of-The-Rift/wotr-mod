package com.wanderersoftherift.wotr.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AccessibilityOptionsScreen.class)
public class MixinAccessibilityOptionsScreen extends Screen {
    protected MixinAccessibilityOptionsScreen(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void addAccessibilityButton(CallbackInfo ci) {
        AccessibilityOptionsScreen screen = ((AccessibilityOptionsScreen) (Object) this);
        Button accessibilityButton = Button
                .builder(Component.translatable("accessibility.wotr.menubutton"), (button) -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(
                                new com.wanderersoftherift.wotr.gui.screen.AccessibilityOptionsScreen(screen));
                    }
                })
                .pos(5, 5)
                .size(125, 20)
                .build();
        this.addRenderableWidget(accessibilityButton); // using 'this' here feels dangerous but it works so im not
                                                       // complaining
    }
}
