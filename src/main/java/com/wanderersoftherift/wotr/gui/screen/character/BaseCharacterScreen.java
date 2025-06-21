package com.wanderersoftherift.wotr.gui.screen.character;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.character.BaseCharacterMenu;
import com.wanderersoftherift.wotr.gui.menu.character.CharacterMenuItem;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.network.SelectCharacterMenuPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCharacterScreen<T extends BaseCharacterMenu> extends AbstractContainerScreen<T> {
    protected static final int MENU_BAR_WIDTH = 100;

    private ScrollContainerWidget<MenuItem> menuSelection;
    private List<ScrollContainerWidget<?>> scrollContainers = new ArrayList<>();

    public BaseCharacterScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        leftPos = 0;
        topPos = 0;
        menuSelection = new ScrollContainerWidget<>(0, 0, MENU_BAR_WIDTH, 320);

        List<CharacterMenuItem> items = BaseCharacterMenu.getSortedMenuItems(minecraft.level.registryAccess());
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            menuSelection.children()
                    .add(new MenuItem(item.name(), i, 0, 0, 94, font, menu.getType() == item.menuType()));
        }
        addRenderableWidget(menuSelection);
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        if (widget instanceof ScrollContainerWidget<?> scrollContainer) {
            scrollContainers.add(scrollContainer);
        }
        return super.addRenderableWidget(widget);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double xOffset, double yOffset) {
        if (!super.mouseScrolled(mouseX, mouseY, xOffset, yOffset)) {
            for (var scrollContainer : scrollContainers) {
                if (scrollContainer.isHovered()) {
                    return scrollContainer.mouseScrolled(mouseX, mouseY, xOffset, yOffset);
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        for (var scrollContainer : scrollContainers) {
            scrollContainer.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!super.keyPressed(keyCode, scanCode, modifiers)) {
            for (var scrollContainer : scrollContainers) {
                if (scrollContainer.isHoveredOrFocused()) {
                    return scrollContainer.keyPressed(keyCode, scanCode, modifiers);
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        menuSelection.setHeight(guiGraphics.guiHeight());
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int width = font.width(getTitle());
        guiGraphics.drawString(font, getTitle(),
                leftPos + (guiGraphics.guiWidth() - MENU_BAR_WIDTH - width) / 2 + MENU_BAR_WIDTH, 10,
                ChatFormatting.WHITE.getColor(), true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), ChatFormatting.BLACK.getColor());
    }

    private static class MenuItem extends AbstractWidget implements ScrollContainerEntry {

        private static final ResourceLocation BACKGROUND = WanderersOfTheRift
                .id("textures/gui/container/character/menu_item.png");
        private static final ResourceLocation SELECTED = WanderersOfTheRift
                .id("textures/gui/container/character/menu_item_selected.png");
        private static final ResourceLocation HOVERED = WanderersOfTheRift
                .id("textures/gui/container/character/menu_item_hovered.png");
        private final Font font;
        private final boolean selected;
        private final int index;

        public MenuItem(Component title, int index, int x, int y, int width, Font font, boolean selected) {
            super(x, y, width, 15, title);
            this.font = font;
            this.selected = selected;
            this.index = index;
        }

        @Override
        public int getHeight(int width) {
            return 15;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            ResourceLocation texture = BACKGROUND;
            if (selected) {
                texture = SELECTED;
            } else if (isHovered()) {
                texture = HOVERED;
            }

            guiGraphics.blit(RenderType::guiTextured, texture, getX(), getY(), 0, 0, 94, 15, 94, 15);
            guiGraphics.drawString(font, getMessage(), getX() + 3, getY() + 4, ChatFormatting.WHITE.getColor(), true);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }

        @Override
        public void onClick(double mouseX, double mouseY, int button) {
            if (!selected) {
                PacketDistributor.sendToServer(new SelectCharacterMenuPayload(index));
            }
        }
    }
}
