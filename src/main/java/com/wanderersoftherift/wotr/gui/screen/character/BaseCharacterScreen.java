package com.wanderersoftherift.wotr.gui.screen.character;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.character.BaseCharacterMenu;
import com.wanderersoftherift.wotr.gui.menu.character.CharacterMenuItem;
import com.wanderersoftherift.wotr.gui.screen.EnhancedContainerScreen;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.network.charactermenu.SelectCharacterMenuPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The base character screen, which all character subscreens implement. This provides the common appearance of all
 * character screens - primarily the list of submenus that can be opened.
 * 
 * @param <T>
 */
public abstract class BaseCharacterScreen<T extends BaseCharacterMenu> extends EnhancedContainerScreen<T> {
    protected static final int MENU_BAR_WIDTH = 100;

    private ScrollContainerWidget<MenuItem> menuSelection;

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
                    .add(new MenuItem(item.name(), i, 0, 0, MENU_BAR_WIDTH - 6, font,
                            menu.getType() == item.menuType()));
        }
        addRenderableWidget(menuSelection);
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
        private static final int MENU_ITEM_HEIGHT = 15;

        private final Font font;
        private final boolean selected;
        private final int index;

        public MenuItem(Component title, int index, int x, int y, int width, Font font, boolean selected) {
            super(x, y, width, MENU_ITEM_HEIGHT, title);
            this.font = font;
            this.selected = selected;
            this.index = index;
        }

        @Override
        public int getHeight(int width) {
            return MENU_ITEM_HEIGHT;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            ResourceLocation texture = BACKGROUND;
            if (selected) {
                texture = SELECTED;
            } else if (isHovered()) {
                texture = HOVERED;
            }

            guiGraphics.blit(RenderType::guiTextured, texture, getX(), getY(), 0, 0, 94, MENU_ITEM_HEIGHT, 94,
                    MENU_ITEM_HEIGHT);
            guiGraphics.drawString(font, getMessage(), getX() + 3, getY() + 4, ChatFormatting.WHITE.getColor(), true);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
            // TODO
        }

        @Override
        public void onClick(double mouseX, double mouseY, int button) {
            if (!selected) {
                PacketDistributor.sendToServer(new SelectCharacterMenuPayload(index));
            }
        }
    }
}
