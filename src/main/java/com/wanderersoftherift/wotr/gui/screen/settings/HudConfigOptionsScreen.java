package com.wanderersoftherift.wotr.gui.screen.settings;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.config.ClientConfig;
import com.wanderersoftherift.wotr.gui.config.preset.ElementPreset;
import com.wanderersoftherift.wotr.gui.config.preset.HudPreset;
import com.wanderersoftherift.wotr.gui.config.preset.HudPresetManager;
import com.wanderersoftherift.wotr.init.client.ModConfigurableLayers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * A screen to enable configuration of HUD element positioning
 */
public class HudConfigOptionsScreen extends Screen {

    private int screenWidth = 0;
    private int screenHeight = 0;

    private final Button customizeButton;
    private final Button resetButton;
    private final Button closeButton;
    private final CycleButton<HudPreset> presetButton;

    public HudConfigOptionsScreen() {
        super(Component.translatable(WanderersOfTheRift.translationId("screen", "configure_hud")));

        ResourceLocation presetName = ResourceLocation.tryParse(ClientConfig.HUD_PRESET.get());
        HudPreset currentPreset = HudPresetManager.getInstance().getPreset(presetName);
        if (currentPreset == null) {
            currentPreset = HudPresetManager.getInstance().getPreset(WanderersOfTheRift.id("default"));
        }

        presetButton = CycleButton.<HudPreset>builder(x -> Component.translatable(
                "hud_preset." + x.id().getNamespace() + "." + x.id().getPath()))
                .withValues(HudPresetManager.getInstance().getPresets())
                .withInitialValue(currentPreset)
                .create(0, 0, 150, 20,
                        Component.translatable(WanderersOfTheRift.translationId("button", "hud_presets")),
                        (cycleButton, preset) -> {
                            ClientConfig.HUD_PRESET.set(preset.id().toString());
                            ModConfigurableLayers.CONFIGURABLE_LAYER_REGISTRY.stream().forEach(layer -> {
                                layer.getConfig().reset();
                                ElementPreset elementPreset = preset.elementMap()
                                        .get(ModConfigurableLayers.CONFIGURABLE_LAYER_REGISTRY.getKey(layer));
                                if (elementPreset != null) {
                                    elementPreset.applyTo(layer.getConfig());
                                }
                            });
                        });

        customizeButton = Button
                .builder(Component.translatable(WanderersOfTheRift.translationId("button", "customize")), button -> {
                    minecraft.setScreen(new HudConfigCustomizeScreen());
                })
                .size(150, 20)
                .build();

        resetButton = Button
                .builder(Component.translatable(WanderersOfTheRift.translationId("button", "reset")), button -> {
                    HudPreset preset = presetButton.getValue();
                    ModConfigurableLayers.CONFIGURABLE_LAYER_REGISTRY.stream().forEach(layer -> {
                        layer.getConfig().reset();
                        ElementPreset elementPreset = preset.elementMap()
                                .get(ModConfigurableLayers.CONFIGURABLE_LAYER_REGISTRY.getKey(layer));
                        if (elementPreset != null) {
                            elementPreset.applyTo(layer.getConfig());
                        }
                    }
                    );
                })
                .size(40, 20)
                .build();
        closeButton = Button
                .builder(Component.translatable(WanderersOfTheRift.translationId("button", "close")),
                        button -> onClose())
                .size(40, 20)
                .build();
    }

    @Override
    protected void init() {
        addRenderableWidget(resetButton);
        addRenderableWidget(closeButton);
        addRenderableWidget(presetButton);
        addRenderableWidget(customizeButton);
    }

    @Override
    public void onClose() {
        ClientConfig.SPEC.save();
        super.onClose();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (screenWidth != guiGraphics.guiWidth() || screenHeight != guiGraphics.guiHeight()) {
            updateButtonPositions(guiGraphics);
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void updateButtonPositions(GuiGraphics guiGraphics) {
        screenWidth = guiGraphics.guiWidth();
        screenHeight = guiGraphics.guiHeight();

        int buttonWidth = presetButton.getWidth() + resetButton.getWidth() + 3;

        presetButton.setPosition(guiGraphics.guiWidth() / 2 - buttonWidth / 2,
                guiGraphics.guiHeight() / 2 - presetButton.getHeight() - 2);
        customizeButton.setPosition(guiGraphics.guiWidth() / 2 - buttonWidth / 2, guiGraphics.guiHeight() / 2 + 1);
        resetButton.setPosition(presetButton.getX() + presetButton.getWidth() + 3,
                guiGraphics.guiHeight() / 2 - resetButton.getHeight() - 2);
        closeButton.setPosition(customizeButton.getX() + customizeButton.getWidth() + 3,
                guiGraphics.guiHeight() / 2 + 1);
    }

    @Override
    protected void renderBlurredBackground() {
    }
}
