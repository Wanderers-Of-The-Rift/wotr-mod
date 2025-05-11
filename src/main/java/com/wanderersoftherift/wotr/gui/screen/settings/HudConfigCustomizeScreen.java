package com.wanderersoftherift.wotr.gui.screen.settings;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.config.ConfigurableLayer;
import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import com.wanderersoftherift.wotr.init.client.ModConfigurableLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.Optional;

/**
 * A screen to customize of HUD element positioning
 */
public class HudConfigCustomizeScreen extends Screen {

    private static final int BACKGROUND_COLOR = 0x669999FF;
    private static final int SELECTED_BACKGROUND_COLOR = 0xAAFFFFFF;
    private static final int HIGHLIGHT_COLOR = 0xFF8888FF;
    private static final int SELECTED_HIGHLIGHT_COLOR = 0xFFFFFFFF;

    private static final Component HIDE_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("button", "hide"));
    private static final Component SHOW_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("button", "show"));
    private static final int SNAP_DIST = 10;

    private ConfigurableLayer focusedLayer = null;

    private int screenWidth = 0;
    private int screenHeight = 0;

    private double residualDragX;
    private double residualDragY;

    private final Button reorientateButton;
    private final CycleButton<Boolean> visibleButton;

    public HudConfigCustomizeScreen() {
        super(Component.translatable(WanderersOfTheRift.translationId("screen", "configure_hud")));
        reorientateButton = Button
                .builder(Component.translatable(WanderersOfTheRift.translationId("button", "rotate")), button -> {
                    if (focusedLayer != null) {
                        // Also adjust position to keep it centered after the move
                        Vector2i pos = focusedLayer.getConfig()
                                .getPosition(focusedLayer.getConfigWidth(), focusedLayer.getConfigHeight(), screenWidth,
                                        screenHeight)
                                .add(focusedLayer.getConfigWidth() / 2, focusedLayer.getConfigHeight() / 2);
                        focusedLayer.getConfig().reorientate();
                        Vector2i newPos = focusedLayer.getConfig()
                                .getPosition(focusedLayer.getConfigWidth(), focusedLayer.getConfigHeight(), screenWidth,
                                        screenHeight)
                                .add(focusedLayer.getConfigWidth() / 2, focusedLayer.getConfigHeight() / 2);
                        focusedLayer.getConfig().setX(focusedLayer.getConfig().getX() + pos.x - newPos.x);
                        focusedLayer.getConfig().setY(focusedLayer.getConfig().getY() + pos.y - newPos.y);
                    }
                })
                .size(35, 12)
                .build();
        visibleButton = CycleButton.<Boolean>booleanBuilder(HIDE_LABEL, SHOW_LABEL)
                .displayOnlyValue()
                .create(0, 0, 35, 12, Component.empty(), ((cycleButton, value) -> {
                    if (focusedLayer != null) {
                        focusedLayer.getConfig().setVisible(value);
                    }
                }));
    }

    @Override
    protected void init() {
        addRenderableWidget(visibleButton);
        addRenderableWidget(reorientateButton);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(new HudConfigOptionsScreen());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        screenWidth = guiGraphics.guiWidth();
        screenHeight = guiGraphics.guiHeight();

        updateVisibleButton(guiGraphics);
        updateRotateButton(guiGraphics);

        renderConfigurableElements(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void updateVisibleButton(@NotNull GuiGraphics guiGraphics) {
        if (focusedLayer != null && !isDragging()) {
            visibleButton.setValue(focusedLayer.getConfig().isVisible());
            Vector2i pos = focusedLayer.getConfig()
                    .getPosition(focusedLayer.getConfigWidth(), focusedLayer.getConfigHeight(), guiGraphics.guiWidth(),
                            guiGraphics.guiHeight())
                    .add(focusedLayer.getConfigWidth() / 2, focusedLayer.getConfigHeight() / 2)
                    .sub(visibleButton.getWidth() / 2, visibleButton.getHeight() / 2);
            if (focusedLayer.getConfig().hasOrientation()) {
                pos.sub(0, visibleButton.getHeight() / 2 + 1);
            }
            if (pos.x + focusedLayer.getConfigWidth() / 2 + visibleButton.getWidth() < screenWidth) {
                pos.x += focusedLayer.getConfigWidth() / 2 + visibleButton.getWidth() / 2 + 1;
            } else {
                pos.x -= focusedLayer.getConfigWidth() / 2 + visibleButton.getWidth() / 2 + 2;
            }
            visibleButton.setPosition(pos.x, pos.y);

            visibleButton.visible = true;
        } else {
            visibleButton.visible = false;
        }
    }

    private void updateRotateButton(@NotNull GuiGraphics guiGraphics) {
        if (focusedLayer != null && focusedLayer.getConfig().hasOrientation() && !isDragging()) {
            Vector2i pos = focusedLayer.getConfig()
                    .getPosition(focusedLayer.getConfigWidth(), focusedLayer.getConfigHeight(), guiGraphics.guiWidth(),
                            guiGraphics.guiHeight())
                    .add(focusedLayer.getConfigWidth() / 2, focusedLayer.getConfigHeight() / 2 + 1)
                    .sub(reorientateButton.getWidth() / 2, 0);

            if (pos.x + focusedLayer.getConfigWidth() / 2 + reorientateButton.getWidth() < screenWidth) {
                pos.x += focusedLayer.getConfigWidth() / 2 + reorientateButton.getWidth() / 2 + 1;
            } else {
                pos.x -= focusedLayer.getConfigWidth() / 2 + reorientateButton.getWidth() / 2 + 2;
            }

            reorientateButton.setPosition(pos.x, pos.y);
            reorientateButton.visible = true;
        } else {
            reorientateButton.visible = false;
        }
    }

    private void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isDragging()) {
            return;
        }
        findMouseOver(mouseX, mouseY, screenWidth, screenHeight)
                .ifPresent(layer -> guiGraphics.renderTooltip(minecraft.font, layer.getName(), mouseX, mouseY + 8));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button) || button != GLFW.GLFW_MOUSE_BUTTON_1) {
            return true;
        }
        Optional<ConfigurableLayer> overLayer = findMouseOver((int) mouseX, (int) mouseY, screenWidth, screenHeight);
        if (overLayer.isPresent()) {
            focusedLayer = overLayer.get();
            setDragging(true);
            residualDragX = 0;
            residualDragY = 0;
            return true;
        }
        focusedLayer = null;
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (focusedLayer != null) {
            HudElementConfig config = focusedLayer.getConfig();
            dragX += residualDragX;
            dragY += residualDragY;
            Vector2i newRelPos = new Vector2i(config.getX() + (int) dragX, config.getY() + (int) dragY);
            snap(newRelPos);

            boolean changed = false;
            if (newRelPos.x != config.getX()) {
                residualDragX = dragX - (newRelPos.x - config.getX());
                config.setX(newRelPos.x);
                changed = true;
            } else {
                residualDragX = dragX;
            }
            if (newRelPos.y != config.getY()) {
                residualDragY = dragY - (newRelPos.y - config.getY());
                config.setY(newRelPos.y);
                changed = true;
            } else {
                residualDragY = dragY;
            }

            if (changed) {
                config.reanchor(focusedLayer.getConfigWidth(), focusedLayer.getConfigHeight(), screenWidth,
                        screenHeight);
            }
            return true;
        }
        return false;
    }

    private void snap(Vector2i newRelPos) {
        if (Math.abs(newRelPos.x) < SNAP_DIST) {
            newRelPos.x = 0;
        }
        if (Math.abs(newRelPos.y) < SNAP_DIST) {
            newRelPos.y = 0;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedLayer != null) {
            boolean unused = false;
            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT -> focusedLayer.getConfig().setX(focusedLayer.getConfig().getX() - 1);
                case GLFW.GLFW_KEY_RIGHT -> focusedLayer.getConfig().setX(focusedLayer.getConfig().getX() + 1);
                case GLFW.GLFW_KEY_UP -> focusedLayer.getConfig().setY(focusedLayer.getConfig().getY() - 1);
                case GLFW.GLFW_KEY_DOWN -> focusedLayer.getConfig().setY(focusedLayer.getConfig().getY() + 1);
                default -> unused = true;
            }
            if (!unused) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderMenuBackground(@NotNull GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderBlurredBackground() {
    }

    private Optional<ConfigurableLayer> findMouseOver(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        return ModConfigurableLayers.CONFIGURABLE_LAYER_REGISTRY.stream().filter(layer -> {
            int width = layer.getConfigWidth();
            int height = layer.getConfigHeight();
            Vector2i pos = layer.getConfig().getPosition(width, height, screenWidth, screenHeight);
            return mouseX >= pos.x && mouseY >= pos.y && mouseX <= pos.x + width && mouseY <= pos.y + height;
        }).min(Comparator.comparing(x -> x.getConfigHeight() * x.getConfigWidth()));
    }

    private void renderConfigurableElements(GuiGraphics guiGraphics) {
        ModConfigurableLayers.CONFIGURABLE_LAYER_REGISTRY.stream().forEach(layer -> {
            int width = layer.getConfigWidth();
            int height = layer.getConfigHeight();
            Vector2i pos = layer.getConfig()
                    .getPosition(width, height, guiGraphics.guiWidth(), guiGraphics.guiHeight());

            boolean focused = focusedLayer == layer;
            if (layer.getConfig().isVisible()) {
                guiGraphics.fill(RenderType.gui(), pos.x + 1, pos.y + 1, pos.x + width - 1, pos.y + height - 1,
                        focused ? SELECTED_BACKGROUND_COLOR : BACKGROUND_COLOR);
            }
            guiGraphics.renderOutline(pos.x, pos.y, width, height,
                    focused ? SELECTED_HIGHLIGHT_COLOR : HIGHLIGHT_COLOR);

        });
    }
}
