package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgrade;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.gui.menu.AbilityBenchMenu;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.network.LevelUpAbilityPayload;
import com.wanderersoftherift.wotr.network.SelectAbilityUpgradePayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * A UI for viewing ability details and manage their upgrades
 */
public class AbilityBenchScreen extends AbstractContainerScreen<AbilityBenchMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/ability_bench/background.png");

    private static final int BACKGROUND_WIDTH = 320;
    private static final int BACKGROUND_HEIGHT = 236;
    private static final int UPGRADE_LABEL_X = 202;
    private static final int UPGRADE_LABEL_Y = 20;
    private static final int SELECTION_AREA_X = 202;
    private static final int SELECTION_AREA_Y = 30;
    private static final int SELECTION_AREA_WIDTH = 106;
    private static final int SELECTION_AREA_HEIGHT = 196;
    private static final int UPGRADE_WIDTH = 20;
    private static final int UPGRADE_SPACING = 8;
    private static final int BORDER_X = 24;
    public static final int ICON_SIZE = 16;

    private final Component upgradeLabel = Component
            .translatable(WanderersOfTheRift.translationId("container", "ability_bench.upgrade"));
    private ScrollContainerWidget<ScrollContainerEntry> upgradeChoices;

    public AbilityBenchScreen(AbilityBenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 320;
        this.imageHeight = 236;
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX += BORDER_X;
        this.titleLabelX += BORDER_X;
    }

    @Override
    protected void init() {
        super.init();
        upgradeChoices = new ScrollContainerWidget<>(leftPos + SELECTION_AREA_X, topPos + SELECTION_AREA_Y,
                SELECTION_AREA_WIDTH, SELECTION_AREA_HEIGHT);
        addRenderableWidget(upgradeChoices);
        upgradeChoices.setFocused(true);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double xOffset, double yOffset) {
        if (!super.mouseScrolled(mouseX, mouseY, xOffset, yOffset)) {
            if (upgradeChoices.isHovered()) {
                return upgradeChoices.mouseScrolled(mouseX, mouseY, xOffset, yOffset);
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        upgradeChoices.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!super.keyPressed(keyCode, scanCode, modifiers)) {
            if (upgradeChoices.isHoveredOrFocused()) {
                return upgradeChoices.keyPressed(keyCode, scanCode, modifiers);
            }
            return false;
        }
        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateChoices();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderSelection(guiGraphics, partialTick, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void updateChoices() {
        AbilityUpgradePool pool = menu.getUpgradePool();
        if (pool != null) {
            int count = pool.getChoiceCount();
            boolean needsUnlock = menu.canLevelUp();
            boolean hasUnlock = !upgradeChoices.children().isEmpty()
                    && upgradeChoices.children().getLast() instanceof LockedBar;

            int currentSkills = upgradeChoices.children().size() - (hasUnlock ? 1 : 0);
            while (currentSkills > count) {
                upgradeChoices.children().remove(--currentSkills);
            }
            while (currentSkills < count) {
                upgradeChoices.children()
                        .add(currentSkills,
                                new ChoiceBar(0, 0, SELECTION_AREA_WIDTH - ScrollContainerWidget.SCROLLBAR_SPACE,
                                        currentSkills, menu::getUpgradePool));
                currentSkills++;
            }

            if (needsUnlock && !hasUnlock) {
                upgradeChoices.children()
                        .add(new LockedBar(0, 0, SELECTION_AREA_WIDTH - ScrollContainerWidget.SCROLLBAR_SPACE, font,
                                menu));
            } else if (hasUnlock && !needsUnlock) {
                upgradeChoices.children().removeLast();
            }
        } else {
            upgradeChoices.children().clear();
        }
    }

    private void renderSelection(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        if (menu.isAbilityItemPresent()) {
            ItemStack skillItem = menu.getAbilityItem();
            guiGraphics.drawString(font, skillItem.getDisplayName(), this.leftPos + 52, this.topPos + 21,
                    ChatFormatting.BLACK.getColor(), true);
            guiGraphics.drawString(this.font, this.upgradeLabel, leftPos + UPGRADE_LABEL_X, topPos + UPGRADE_LABEL_Y,
                    ChatFormatting.DARK_GRAY.getColor(), false);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth,
                this.imageHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

    /**
     * Locked bar that can be unlocked
     */
    private static class LockedBar extends AbstractContainerWidget implements ScrollContainerEntry {
        private static final ResourceLocation BAR_TEXTURE = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/choice_bar.png");
        private static final int BORDER_Y = 4;

        private final UnlockButton button;

        LockedBar(int x, int y, int width, Font font, AbilityBenchMenu menu) {
            super(x, y, width, 28, Component.empty());
            int offset = (width - UPGRADE_WIDTH) / 2;
            button = new UnlockButton(x + offset, y + BORDER_Y, font, menu);
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.blit(RenderType::guiTextured, BAR_TEXTURE, this.getX(), this.getY(), 0, 0, this.width,
                    this.height, this.width, this.height);
            button.setX(getX());
            button.setY(getY() + 4);
            button.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }

        @Override
        protected int contentHeight() {
            return height;
        }

        @Override
        protected double scrollRate() {
            return 0;
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return Collections.singletonList(button);
        }

        @Override
        public int getHeight(int width) {
            return height;
        }
    }

    /**
     * A bar of N choices
     */
    private static class ChoiceBar extends AbstractContainerWidget implements ScrollContainerEntry {
        private static final ResourceLocation BAR_TEXTURE = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/choice_bar.png");
        private static final int BORDER_Y = 4;

        private final Supplier<AbilityUpgradePool> upgradePool;
        private final int choice;
        private final List<ChoiceButton> options = new ArrayList<>();

        ChoiceBar(int x, int y, int width, int choice, Supplier<AbilityUpgradePool> upgradePool) {
            super(x, y, width, 28, Component.empty());
            this.choice = choice;
            this.upgradePool = upgradePool;

            AbilityUpgradePool pool = upgradePool.get();
            int choices = pool.getChoiceOptions(choice).size();
            int offset = (width - choices * UPGRADE_WIDTH - (choices - 1) * UPGRADE_SPACING) / 2;

            for (int i = 0; i < choices; i++) {
                ChoiceButton button = new ChoiceButton(x + offset + i * (UPGRADE_WIDTH + UPGRADE_SPACING), y + BORDER_Y,
                        choice, i, upgradePool);
                options.add(button);
            }
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            AbilityUpgradePool pool = upgradePool.get();
            while (pool.getChoiceOptions(choice).size() > options.size()) {
                options.add(new ChoiceButton(0, 0, choice, options.size(), upgradePool));
            }
            while (pool.getChoiceOptions(choice).size() < options.size()) {
                options.removeLast();
            }
            guiGraphics.blit(RenderType::guiTextured, BAR_TEXTURE, this.getX(), this.getY(), 0, 0, this.width,
                    this.height, this.width, this.height);
            int offset = (width - options.size() * UPGRADE_WIDTH - (options.size() - 1) * UPGRADE_SPACING) / 2;
            for (int i = 0; i < options.size(); i++) {
                ChoiceButton option = options.get(i);
                option.setX(getX() + offset + i * (UPGRADE_WIDTH + UPGRADE_SPACING));
                option.setY(getY() + 4);
                option.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }

        @Override
        protected int contentHeight() {
            return height;
        }

        @Override
        protected double scrollRate() {
            return 0;
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return options;
        }

        @Override
        public int getHeight(int width) {
            return height;
        }
    }

    /**
     * Button for unlocking an ability level
     */
    private static class UnlockButton extends AbstractButton {
        private static final ResourceLocation LOCK = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/lock.png");
        private static final ResourceLocation LOCKED_BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/locked_button.png");
        private static final ResourceLocation CHOICE_BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/choice_button.png");
        private static final ResourceLocation HOVERED_CHOICE_BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/hovered_choice_button.png");

        private final Font font;
        private final AbilityBenchMenu menu;

        UnlockButton(int x, int y, Font font, AbilityBenchMenu menu) {
            super(x, y, 20, 20, Component.empty());
            this.font = font;
            this.menu = menu;
            this.setTooltip(Tooltip.create(
                    Component.translatable(WanderersOfTheRift.translationId("container", "ability_bench.unlock"))));
        }

        @Override
        public void onPress() {
            if (menu.costForNextLevel() <= menu.availableUpgradeCurrency()) {
                PacketDistributor.sendToServer(new LevelUpAbilityPayload(menu.getUnlockLevel() + 1));
            }
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int cost = menu.costForNextLevel();
            int available = menu.availableUpgradeCurrency();

            ResourceLocation buttonTexture;
            if (cost > available) {
                buttonTexture = LOCKED_BUTTON;
                active = false;
            } else if (this.isHovered()) {
                buttonTexture = HOVERED_CHOICE_BUTTON;
                active = true;
            } else {
                buttonTexture = CHOICE_BUTTON;
                active = true;
            }

            graphics.blit(RenderType::guiTextured, buttonTexture, this.getX(), this.getY(), 0, 0, this.width,
                    this.height, this.width, this.height);
            graphics.blit(RenderType::guiTextured, LOCK, this.getX() + 2, this.getY() + 2, 0, 0, ICON_SIZE, ICON_SIZE,
                    ICON_SIZE, ICON_SIZE);

            Component costString = Component.literal(Integer.toString(cost));
            int costX = (this.width - font.width(costString)) / 2;
            int costY = this.height - font.lineHeight - 2;
            graphics.drawString(font, costString, this.getX() + costX, this.getY() + costY,
                    ChatFormatting.WHITE.getColor(), true);
        }

        @Override
        public void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }
    }

    /**
     * Button for selecting a choice option
     */
    private static class ChoiceButton extends AbstractButton {
        private static final ResourceLocation CHOICE_BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/choice_button.png");
        private static final ResourceLocation SELECTED_CHOICE_BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/selected_choice_button.png");
        private static final ResourceLocation HOVERED_CHOICE_BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/hovered_choice_button.png");

        private final int choice;
        private final int selection;
        private final Supplier<AbilityUpgradePool> upgradePool;

        ChoiceButton(int x, int y, int choice, int selection, Supplier<AbilityUpgradePool> upgradePool) {
            super(x, y, 20, 20, Component.empty());
            this.choice = choice;
            this.selection = selection;
            this.upgradePool = upgradePool;
        }

        @Override
        public void onPress() {
            AbilityUpgradePool pool = upgradePool.get();
            if (pool != null && pool.getSelectedIndex(choice) != selection) {
                PacketDistributor.sendToServer(new SelectAbilityUpgradePayload(choice, selection));
            }
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            ResourceLocation resourcelocation;
            if (isSelected()) {
                resourcelocation = SELECTED_CHOICE_BUTTON;
                active = false;
            } else if (this.isHovered()) {
                resourcelocation = HOVERED_CHOICE_BUTTON;
                active = true;
            } else {
                resourcelocation = CHOICE_BUTTON;
                active = true;
            }

            graphics.blit(RenderType::guiTextured, resourcelocation, this.getX(), this.getY(), 0, 0, this.width,
                    this.height, this.width, this.height);
            renderIcon(graphics);
            updateTooltip();
        }

        private void updateTooltip() {
            AbilityUpgradePool pool = upgradePool.get();
            AbilityUpgrade upgrade = pool.getChoiceOptions(choice).get(selection).value();
            MutableComponent tooltipComp = Component.empty()
                    .append(upgrade.name())
                    .append("\n")
                    .append(upgrade.description());
            this.setTooltip(Tooltip.create(tooltipComp));
        }

        private void renderIcon(GuiGraphics guiGraphics) {
            AbilityUpgradePool pool = upgradePool.get();
            AbilityUpgrade upgrade = pool.getChoiceOptions(choice).get(selection).value();
            guiGraphics.blit(RenderType::guiTextured, upgrade.icon(), this.getX() + (width - ICON_SIZE) / 2,
                    this.getY() + (height - ICON_SIZE) / 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }

        public boolean isSelected() {
            AbilityUpgradePool pool = upgradePool.get();
            return pool.getSelectedIndex(choice) == selection;
        }

        @Override
        public void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }
    }
}
