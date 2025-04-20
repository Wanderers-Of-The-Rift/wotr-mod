package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.config.ClientConfig;
import com.wanderersoftherift.wotr.gui.widget.RiftMap3DWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RiftMapScreen extends Screen {
    private RiftMap3DWidget mapWidget;
    private float renderDistance = 10;

    public RiftMapScreen(Component title, float renderDistance) {
        super(title);

        this.renderDistance = renderDistance;
    }

    @Override
    protected void init() {
        super.init();

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        this.mapWidget = new RiftMap3DWidget(15, 15, screenWidth - 30, screenHeight - 30, renderDistance);

        Button button = Button.builder(Component.literal("X"), (btn) -> onClose())
                .createNarration((messageSupplier) -> Component
                        .literal("Custom Narration: " + messageSupplier.get().getString()))
                .bounds(screenWidth - 27, 2, 12, 12)
                .build();
        Button resetButton = Button.builder(Component.literal("Reset"), (btn) -> mapWidget.resetCam())
                .createNarration((messageSupplier) -> Component
                        .literal("Custom Narration: " + messageSupplier.get().getString()))
                .bounds(screenWidth - 50, screenHeight - 14, 35, 12)
                .build();

        Button mouseToggleButton = Button.builder(Component.literal("Toggle Mouse"), (btn) -> {
            ClientConfig.MOUSE_MODE.set(!ClientConfig.MOUSE_MODE.get());
            btn.setMessage(
                    Component.literal(ClientConfig.MOUSE_MODE.get() ? "Toggle Mouse (True)" : "Toggle Mouse (False)"));
            ClientConfig.MOUSE_MODE.save();
        })
                .createNarration((messageSupplier) -> Component
                        .literal("Custom Narration: " + messageSupplier.get().getString()))
                .bounds(screenWidth - 180, screenHeight - 14, 110, 12)
                .build();

        Button lerpButton = Button.builder(Component.literal("Lerp Speed"), (btn) -> {
            double lerpSpeed = Math.round(ClientConfig.LERP_SPEED.get() * 10.0) / 10.0;
            lerpSpeed += 0.1;
            if (lerpSpeed > 2.0) {
                lerpSpeed = 0.0;
            }
            ClientConfig.LERP_SPEED.set(lerpSpeed);
            btn.setMessage(Component.literal("Lerp Speed: " + String.valueOf(lerpSpeed).substring(0, 3)));
            if (lerpSpeed <= 0.0) {
                btn.setMessage(Component.literal("Lerp Speed: OFF"));
            }
            ClientConfig.LERP_SPEED.save();
        })
                .createNarration((messageSupplier) -> Component
                        .literal("Custom Narration: " + messageSupplier.get().getString()))
                .bounds(screenWidth - 50 - 130 - 130, screenHeight - 14, 110, 12)
                .build();

        this.addRenderableWidget(button);
        this.addRenderableWidget(resetButton);
        this.addRenderableWidget(mouseToggleButton);
        this.addRenderableWidget(lerpButton);

        // register buttons first to be clickable and not occluded by RiftMap
        GuiEventListener e = this.addRenderableWidget(mapWidget);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // needs to be overriden to allow right click drag
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Optional<GuiEventListener> optional = this.getChildAt(mouseX, mouseY);
        if (optional.isEmpty()) {
            return false;
        } else {
            GuiEventListener guieventlistener = optional.get();
            if (guieventlistener.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(guieventlistener);
                if (button == 0 || button == 1) { // changed here to allow rightclick drag
                    this.setDragging(true);
                }
            }

            return true;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.getFocused() != null && this.isDragging() && (button == 0 || button == 1)
                && this.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public void tick() {
        mapWidget.tick();
    }

}
