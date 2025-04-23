package com.wanderersoftherift.wotr.gui.layer;

import com.mojang.blaze3d.platform.InputConstants;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.attachment.PlayerCooldownData;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.init.client.ModKeybinds;
import com.wanderersoftherift.wotr.util.GuiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.List;

import static com.wanderersoftherift.wotr.init.ModAttachments.ABILITY_COOLDOWNS;

/**
 * Bar displaying a players selected abilities and their state.
 */
public final class AbilityBar implements LayeredDraw.Layer {

    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/hud/ability_bar/background.png");
    private static final ResourceLocation COOLDOWN_OVERLAY = WanderersOfTheRift
            .id("textures/gui/hud/ability_bar/cooldown_overlay.png");
    private static final ResourceLocation SELECTED_OVERLAY = WanderersOfTheRift
            .id("textures/gui/hud/ability_bar/select.png");

    private static final int BACKGROUND_WIDTH = 24;
    private static final int BACKGROUND_HEIGHT = 60;

    private static final int BAR_OFFSET_X = -4;
    private static final int BAR_OFFSET_Y = -4;
    private static final int ABILITY_OFFSET_X = 4;
    private static final int ABILITY_START_OFFSET_Y = 4;
    private static final int ABILITY_OFFSET_Y = 2;

    private static final int SLOT_HEIGHT = 18;
    private static final int ICON_SIZE = 16;

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        AbilitySlots abilitySlots = player.getData(ModAttachments.ABILITY_SLOTS);
        if (abilitySlots.getSlots() == 0) {
            return;
        }
        PlayerCooldownData cooldowns = player.getData(ABILITY_COOLDOWNS);

        renderBackground(graphics, abilitySlots);
        renderAbilities(graphics, abilitySlots, cooldowns);
        renderAbilityKeyBinds(graphics);

        if (!minecraft.mouseHandler.isMouseGrabbed()) {
            Vector2i mouseScreenPos = GuiUtil.getMouseScreenPosition();
            renderTooltips(graphics, deltaTracker, abilitySlots, mouseScreenPos.x, mouseScreenPos.y);
        }
    }

    private void renderTooltips(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker,
            AbilitySlots abilitySlots, int x, int y) {
        if (x < BAR_OFFSET_X + ABILITY_OFFSET_X || x >= BAR_OFFSET_X + ABILITY_OFFSET_X + ICON_SIZE) {
            return;
        }
        int yOffset = BAR_OFFSET_Y + ABILITY_START_OFFSET_Y;

        int slot = (y - yOffset) / SLOT_HEIGHT;
        if (slot >= abilitySlots.getSlots()) {
            return;
        }
        if (y - yOffset - slot * SLOT_HEIGHT >= ICON_SIZE) {
            return;
        }
        AbstractAbility ability = abilitySlots.getAbilityInSlot(slot);
        if (ability == null) {
            return;
        }
        graphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(ability.getDisplayName()), x, y + 8);
    }

    private void renderAbilities(GuiGraphics graphics, AbilitySlots abilitySlots, PlayerCooldownData cooldowns) {
        int yOffset = BAR_OFFSET_Y + ABILITY_START_OFFSET_Y;
        for (int slot = 0; slot < abilitySlots.getSlots(); slot++) {
            AbstractAbility ability = abilitySlots.getAbilityInSlot(slot);
            if (ability != null) {
                graphics.blit(RenderType::guiTextured, ability.getIcon(), BAR_OFFSET_X + ABILITY_OFFSET_X,
                        yOffset + slot * SLOT_HEIGHT, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            }

            if (cooldowns.isOnCooldown(slot) && cooldowns.getLastCooldownValue(slot) > 0) {
                int overlayHeight = Math.clamp((int) (Math.ceil((float) ICON_SIZE * cooldowns.getCooldownRemaining(slot)
                        / cooldowns.getLastCooldownValue(slot))), 1, ICON_SIZE);
                graphics.blit(RenderType::guiTextured, COOLDOWN_OVERLAY, BAR_OFFSET_X + ABILITY_OFFSET_X,
                        yOffset + slot * SLOT_HEIGHT + ICON_SIZE - overlayHeight, 0, 0, ICON_SIZE, overlayHeight,
                        ICON_SIZE, ICON_SIZE);
            }
        }
        int selected = abilitySlots.getSelectedSlot();
        graphics.blit(RenderType::guiTextured, SELECTED_OVERLAY, BAR_OFFSET_X + ABILITY_OFFSET_X - 6,
                yOffset + selected * SLOT_HEIGHT - 3, 0, 0, 28, 22, 28, 22);
    }

    private void renderAbilityKeyBinds(GuiGraphics graphics) {
        Font font = Minecraft.getInstance().font;
        int yOffset = BAR_OFFSET_Y + ABILITY_START_OFFSET_Y;
        graphics.pose().pushPose();
        float inverseScale = 1.0f;
        for (int slot = 0; slot < ModKeybinds.ABILITY_SLOT_KEYS.size(); slot++) {
            if (ModKeybinds.ABILITY_SLOT_KEYS.get(slot).isUnbound()) {
                continue;
            }
            Component keyText = getShortKeyDescription(ModKeybinds.ABILITY_SLOT_KEYS.get(slot));
            int keyTextWidth = font.width(keyText);
            if (keyTextWidth > 31) {
                keyText = Component.literal("...");
                keyTextWidth = font.width(keyText);
            }
            graphics.drawString(font, keyText,
                    (int) (inverseScale * (BAR_OFFSET_X + ABILITY_OFFSET_X + ICON_SIZE)) - keyTextWidth,
                    (int) (inverseScale * (yOffset + (slot + 1) * SLOT_HEIGHT - 1)) - font.lineHeight,
                    ChatFormatting.WHITE.getColor());
        }
        graphics.pose().popPose();
    }

    private Component getShortKeyDescription(KeyMapping keyMapping) {
        return switch (keyMapping.getKeyModifier()) {
            case ALT -> Component.translatable(WanderersOfTheRift.translationId("keybinds", "mod_alt"))
                    .append(getUnmodifiedKeyDescription(keyMapping));
            case SHIFT -> Component.translatable(WanderersOfTheRift.translationId("keybinds", "mod_shift"))
                    .append(getUnmodifiedKeyDescription(keyMapping));
            case CONTROL -> Component.translatable(WanderersOfTheRift.translationId("keybinds", "mod_ctrl"))
                    .append(getUnmodifiedKeyDescription(keyMapping));
            case NONE -> getUnmodifiedKeyDescription(keyMapping);
        };
    }

    private Component getUnmodifiedKeyDescription(KeyMapping keyMapping) {
        if (keyMapping.getKey().getType() == InputConstants.Type.MOUSE) {
            return Component.literal("M" + keyMapping.getKey().getValue());
        }
        if (keyMapping.getKey().getType() == InputConstants.Type.KEYSYM) {
            return switch (keyMapping.getKey().getValue()) {
                case InputConstants.KEY_LALT ->
                    Component.translatable(WanderersOfTheRift.translationId("keybinds", "l_alt"));
                case InputConstants.KEY_RALT ->
                    Component.translatable(WanderersOfTheRift.translationId("keybinds", "r_alt"));
                case InputConstants.KEY_LCONTROL ->
                    Component.translatable(WanderersOfTheRift.translationId("keybinds", "l_ctrl"));
                case InputConstants.KEY_RCONTROL ->
                    Component.translatable(WanderersOfTheRift.translationId("keybinds", "r_ctrl"));
                default -> keyMapping.getKey().getDisplayName();
            };
        }
        return keyMapping.getKey().getDisplayName();
    }

    private void renderBackground(GuiGraphics graphics, AbilitySlots abilitySlots) {
        int yOffset = BAR_OFFSET_Y;
        for (int i = 0; i < abilitySlots.getSlots(); i++) {
            if (i == 0) {
                graphics.blit(RenderType::guiTextured, BACKGROUND, BAR_OFFSET_X, yOffset, 0, 0, 24, 20,
                        BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
                yOffset += 20;
            } else {
                graphics.blit(RenderType::guiTextured, BACKGROUND, BAR_OFFSET_X, yOffset, 0, 20, 24, 18,
                        BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
                yOffset += 18;
            }
        }
        graphics.blit(RenderType::guiTextured, BACKGROUND, BAR_OFFSET_X, yOffset, 0, 56, 24, 4, BACKGROUND_WIDTH,
                BACKGROUND_HEIGHT);
    }

}
