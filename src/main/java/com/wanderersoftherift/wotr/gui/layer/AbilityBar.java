package com.wanderersoftherift.wotr.gui.layer;

import com.mojang.blaze3d.platform.InputConstants;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.config.ClientConfig;
import com.wanderersoftherift.wotr.gui.config.ConfigurableLayer;
import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import com.wanderersoftherift.wotr.gui.config.UIOrientation;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.client.WotrKeyMappings;
import com.wanderersoftherift.wotr.item.ability.Cooldown;
import com.wanderersoftherift.wotr.util.GuiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.List;
import java.util.Optional;

/**
 * Bar displaying a players selected abilities and their state.
 */
public final class AbilityBar implements ConfigurableLayer {

    private static final Component NAME = Component
            .translatable(WanderersOfTheRift.translationId("hud", "ability_bar"));

    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/hud/ability_bar/background.png");
    private static final ResourceLocation COOLDOWN_OVERLAY = WanderersOfTheRift
            .id("textures/gui/hud/ability_bar/cooldown_overlay.png");
    private static final ResourceLocation SELECTED_OVERLAY = WanderersOfTheRift
            .id("textures/gui/hud/ability_bar/select.png");

    private static final int BACKGROUND_WIDTH = 42;
    private static final int BACKGROUND_HEIGHT = 66;

    private static final int SELECTED_WIDTH = 24;
    private static final int SELECTED_HEIGHT = 24;

    private static final int BACKGROUND_SIZE = 24;
    private static final int ICON_OFFSET = 4;
    private static final int SLOT_SIZE = 18;
    private static final int ICON_SIZE = 16;

    private final Orientation vertical = new Vertical();
    private final Orientation horizontal = new Horizontal();

    @Override
    public Component getName() {
        return NAME;
    }

    @Override
    public HudElementConfig getConfig() {
        return ClientConfig.ABILITY_BAR;
    }

    @Override
    public int getConfigWidth() {
        return getOrientation().getWidth(AbilitySlots.ABILITY_BAR_SIZE);
    }

    @Override
    public int getConfigHeight() {
        return getOrientation().getHeight(AbilitySlots.ABILITY_BAR_SIZE);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !getConfig().isVisible() || minecraft.gameMode == null
                || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        Level level = player.level();
        AbilitySlots abilitySlots = player.getData(WotrAttachments.ABILITY_SLOTS);
        if (abilitySlots.getSlots() == 0) {
            return;
        }

        Orientation orientation = getOrientation();
        Vector2i pos = getConfig().getPosition(orientation.getWidth(abilitySlots.getSlots()),
                orientation.getHeight(abilitySlots.getSlots()), graphics.guiWidth(), graphics.guiHeight());
        orientation.renderBackground(graphics, pos, abilitySlots.getSlots());
        Vector2ic slotOffset = orientation.getSlotOffset();

        for (int i = 0; i < abilitySlots.getSlots(); i++) {
            ItemStack abilityItem = abilitySlots.getStackInSlot(i);
            Holder<AbstractAbility> ability = abilityItem.get(WotrDataComponentType.ABILITY);
            if (ability == null) {
                continue;
            }
            Cooldown cooldown = abilityItem.getOrDefault(WotrDataComponentType.COOLDOWN, new Cooldown());
            renderAbility(graphics, pos.x + ICON_OFFSET + i * slotOffset.x(), pos.y + ICON_OFFSET + i * slotOffset.y(),
                    ability, cooldown.remainingFraction(level));
        }
        renderSelected(graphics, pos.x + abilitySlots.getSelectedSlot() * slotOffset.x(),
                pos.y + abilitySlots.getSelectedSlot() * slotOffset.y());
        for (int i = 0; i < abilitySlots.getSlots(); i++) {
            renderKeyBinds(graphics, pos.x + ICON_OFFSET + i * slotOffset.x(), pos.y + ICON_OFFSET + i * slotOffset.y(),
                    i);
        }

        if (Minecraft.getInstance().screen instanceof ChatScreen) {
            Vector2i mouseScreenPos = GuiUtil.getMouseScreenPosition();
            orientation.getSlotAt(pos, abilitySlots.getSlots(), mouseScreenPos.x, mouseScreenPos.y).ifPresent(slot -> {
                ItemStack abilityItem = abilitySlots.getStackInSlot(slot);
                Holder<AbstractAbility> ability = abilityItem.get(WotrDataComponentType.ABILITY);
                if (ability != null) {
                    graphics.renderComponentTooltip(Minecraft.getInstance().font,
                            List.of(AbstractAbility.getDisplayName(ability)), mouseScreenPos.x, mouseScreenPos.y + 8);
                }
            });
        }
    }

    private Orientation getOrientation() {
        if (getConfig().getOrientation() == UIOrientation.HORIZONTAL) {
            return horizontal;
        } else {
            return vertical;
        }
    }

    private void renderAbility(
            GuiGraphics graphics,
            int xOffset,
            int yOffset,
            Holder<AbstractAbility> ability,
            float cooldownFraction) {
        if (ability != null) {
            graphics.blit(RenderType::guiTextured, ability.value().getIcon(), xOffset, yOffset, 0, 0, ICON_SIZE,
                    ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }

        if (cooldownFraction > 0) {
            int overlayHeight = Math.clamp((int) (Math.ceil((float) ICON_SIZE * cooldownFraction)), 1, ICON_SIZE);
            graphics.blit(RenderType::guiTextured, COOLDOWN_OVERLAY, xOffset, yOffset + ICON_SIZE - overlayHeight, 0, 0,
                    ICON_SIZE, overlayHeight, ICON_SIZE, ICON_SIZE);
        }
    }

    private void renderSelected(GuiGraphics graphics, int x, int y) {
        graphics.blit(RenderType::guiTextured, SELECTED_OVERLAY, x, y, 0, 0, SELECTED_WIDTH, SELECTED_HEIGHT,
                SELECTED_WIDTH, SELECTED_HEIGHT);
    }

    private void renderKeyBinds(GuiGraphics graphics, int xOffset, int yOffset, int slot) {
        if (slot >= WotrKeyMappings.ABILITY_SLOT_KEYS.size()) {
            return;
        }
        if (WotrKeyMappings.ABILITY_SLOT_KEYS.get(slot).isUnbound()) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        Component keyText = getShortKeyDescription(WotrKeyMappings.ABILITY_SLOT_KEYS.get(slot));
        int keyTextWidth = font.width(keyText);
        if (keyTextWidth > 31) {
            keyText = Component.literal("...");
            keyTextWidth = font.width(keyText);
        }
        graphics.drawString(font, keyText, xOffset + ICON_SIZE - keyTextWidth - 1,
                yOffset + ICON_SIZE - font.lineHeight, ChatFormatting.WHITE.getColor());
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

    private interface Orientation {
        void renderBackground(GuiGraphics graphics, Vector2i pos, int slots);

        Vector2ic getSlotOffset();

        Optional<Integer> getSlotAt(Vector2i pos, int slots, int mouseX, int mouseY);

        int getWidth(int slots);

        int getHeight(int slots);
    }

    private static class Vertical implements Orientation {
        private static final Vector2i OFFSET = new Vector2i(0, SLOT_SIZE);

        @Override
        public Vector2ic getSlotOffset() {
            return OFFSET;
        }

        public Optional<Integer> getSlotAt(Vector2i pos, int slots, int mouseX, int mouseY) {
            if (mouseX < pos.x + ICON_OFFSET || mouseX > pos.x + ICON_OFFSET + ICON_SIZE || mouseY < pos.y + ICON_OFFSET
                    || mouseY >= pos.y + ICON_OFFSET + SLOT_SIZE * slots) {
                return Optional.empty();
            }
            int slot = (mouseY - pos.y - ICON_OFFSET) / SLOT_SIZE;
            if (mouseY - pos.y - ICON_OFFSET - SLOT_SIZE * slot >= ICON_SIZE) {
                return Optional.empty();
            }
            return Optional.of(slot);
        }

        public void renderBackground(GuiGraphics graphics, Vector2i pos, int slots) {
            int yOffset = pos.y;
            for (int i = 0; i < slots; i++) {
                if (i == 0) {
                    graphics.blit(RenderType::guiTextured, BACKGROUND, pos.x, yOffset, 0, 0, BACKGROUND_SIZE, 20,
                            BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
                    yOffset += 20;
                } else {
                    graphics.blit(RenderType::guiTextured, BACKGROUND, pos.x, yOffset, 0, 20, BACKGROUND_SIZE, 18,
                            BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
                    yOffset += SLOT_SIZE;
                }
            }
            graphics.blit(RenderType::guiTextured, BACKGROUND, pos.x, yOffset, 0, 38, BACKGROUND_SIZE, 4,
                    BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        }

        @Override
        public int getWidth(int slots) {
            return BACKGROUND_SIZE;
        }

        @Override
        public int getHeight(int slots) {
            return ICON_OFFSET + 2 + slots * SLOT_SIZE;
        }
    }

    private static class Horizontal implements Orientation {

        private static final Vector2i OFFSET = new Vector2i(SLOT_SIZE, 0);
        private static final int BACKGROUND_Y_OFFSET = 42;

        @Override
        public Vector2ic getSlotOffset() {
            return OFFSET;
        }

        @Override
        public Optional<Integer> getSlotAt(Vector2i pos, int slots, int mouseX, int mouseY) {
            if (mouseX < pos.x + ICON_OFFSET || mouseX >= pos.x + ICON_OFFSET + SLOT_SIZE * slots
                    || mouseY < pos.y + ICON_OFFSET || mouseY >= pos.y + ICON_OFFSET + ICON_SIZE) {
                return Optional.empty();
            }
            int slot = (mouseX - pos.x - ICON_OFFSET) / SLOT_SIZE;
            if (mouseX - pos.x - ICON_OFFSET - SLOT_SIZE * slot > ICON_SIZE) {
                return Optional.empty();
            }
            return Optional.of(slot);
        }

        @Override
        public void renderBackground(GuiGraphics graphics, Vector2i pos, int slots) {
            int xOffset = pos.x;
            for (int i = 0; i < slots; i++) {
                if (i == 0) {
                    graphics.blit(RenderType::guiTextured, BACKGROUND, xOffset, pos.y, 0, BACKGROUND_Y_OFFSET, 20,
                            BACKGROUND_SIZE, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
                    xOffset += 20;
                } else {
                    graphics.blit(RenderType::guiTextured, BACKGROUND, xOffset, pos.y, 20, BACKGROUND_Y_OFFSET, 18,
                            BACKGROUND_SIZE, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
                    xOffset += SLOT_SIZE;
                }
            }
            graphics.blit(RenderType::guiTextured, BACKGROUND, xOffset, pos.y, 38, BACKGROUND_Y_OFFSET, 4,
                    BACKGROUND_SIZE, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        }

        @Override
        public int getWidth(int slots) {
            return ICON_OFFSET + 2 + slots * SLOT_SIZE;
        }

        @Override
        public int getHeight(int slots) {
            return BACKGROUND_SIZE;
        }
    }
}
