package com.wanderersoftherift.wotr.gui.screen.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import com.wanderersoftherift.wotr.core.guild.quest.Quest;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import com.wanderersoftherift.wotr.gui.screen.EnhancedContainerScreen;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.gui.widget.lookup.ClassWidgets;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class QuestGiverScreen extends EnhancedContainerScreen<QuestGiverMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift.id("textures/gui/container/quest/giver.png");
    private static final int BACKGROUND_WIDTH = 324;
    private static final int BACKGROUND_HEIGHT = 166;
    private static final Component GOAL_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.goals"));
    private static final Component REWARDS_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.rewards"));

    private ScrollContainerWidget<QuestItem> questsWidget;

    private ScrollContainerWidget<ScrollContainerEntry> questInfo;

    private Holder<Quest> selectedQuest = null;

    private Button accept;

    public QuestGiverScreen(QuestGiverMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = BACKGROUND_WIDTH;
        this.imageHeight = BACKGROUND_HEIGHT;
    }

    private void selectQuest(Holder<Quest> quest) {
        selectedQuest = quest;
        questInfo.children().clear();
        questInfo.children().add(new LabelItem(font, Quest.title(quest), 4));
        questInfo.children().add(new WrappedTextItem(font, Quest.description(quest)));
        questInfo.children().add(new SpacerItem(4));
        questInfo.children().add(new LabelItem(font, GOAL_LABEL, 4));
        for (Goal goal : selectedQuest.value().goals()) {
            ClassWidgets.createFor(goal).map(widget -> {
                if (widget instanceof ScrollContainerEntry scrollable) {
                    return scrollable;
                }
                return null;
            }).ifPresent(widget -> questInfo.children().add(widget));
        }
        questInfo.children().add(new SpacerItem(2));
        questInfo.children().add(new LabelItem(font, REWARDS_LABEL, 4));
        List<AbstractWidget> rewards = selectedQuest.value()
                .rewards()
                .stream()
                .map(ClassWidgets::createFor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        questInfo.children().add(new FlowItem(rewards));
        // Accept button
        accept.visible = true;
    }

    @Override
    protected void init() {
        super.init();
        Registry<Quest> quests = minecraft.level.registryAccess().lookupOrThrow(WotrRegistries.Keys.QUESTS);

        questsWidget = new ScrollContainerWidget<>(leftPos + 5, topPos + 18, 95, 140,
                quests.stream().map(quests::wrapAsHolder).map(QuestItem::new).toList());
        addRenderableWidget(questsWidget);

        questInfo = new ScrollContainerWidget<>(leftPos + 108, topPos + 20, 214, 120);
        addRenderableWidget(questInfo);

        accept = Button.builder(Component.translatable(WanderersOfTheRift.translationId("container", "quest.accept")),
                button -> {
                }).bounds(leftPos + 108, topPos + 142, 40, 16).build();
        addRenderableWidget(accept);
        accept.visible = false;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, ColorUtil.OFF_BLACK, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth,
                this.imageHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

    private class QuestItem extends AbstractButton implements ScrollContainerEntry {

        private final Holder<Quest> quest;

        public QuestItem(Holder<Quest> quest) {
            super(0, 0, 100, 15, Quest.title(quest));
            this.quest = quest;
        }

        @Override
        public int getHeight(int width) {
            return 15;
        }

        @Override
        public void onPress() {
            selectQuest(quest);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }
    }

    private static class SpacerItem extends AbstractWidget implements ScrollContainerEntry {
        private final int space;

        public SpacerItem(int space) {
            super(0, space, 0, space, Component.empty());
            this.space = space;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        }

        @Override
        protected boolean isValidClickButton(int button) {
            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }

        @Override
        public int getHeight(int width) {
            return space;
        }
    }

    private static class LabelItem extends AbstractWidget implements ScrollContainerEntry {

        private final Font font;
        private final int lineSpace;

        public LabelItem(Font font, Component label, int lineSpace) {
            super(0, 0, 100, font.lineHeight + lineSpace, label);
            this.font = font;
            this.lineSpace = lineSpace;
        }

        @Override
        public int getHeight(int width) {
            return font.lineHeight + lineSpace;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.drawString(this.font, getMessage(), getX(), getY(), ColorUtil.OFF_BLACK, false);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }

        @Override
        protected boolean isValidClickButton(int button) {
            return false;
        }
    }

    private static class WrappedTextItem extends AbstractWidget implements ScrollContainerEntry {

        private final Font font;

        public WrappedTextItem(Font font, Component text) {
            super(0, 0, 100, font.lineHeight, text);
            this.font = font;
        }

        @Override
        public int getHeight(int width) {
            return font.wordWrapHeight(getMessage(), width);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.drawWordWrap(this.font, getMessage(), getX(), getY(), getWidth(), ColorUtil.OFF_BLACK, false);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }

        @Override
        protected boolean isValidClickButton(int button) {
            return false;
        }
    }

    private static class FlowItem extends AbstractWidget implements ScrollContainerEntry {

        private final List<AbstractWidget> children;

        public FlowItem(Collection<AbstractWidget> children) {
            super(0, 0, 0, 0, Component.empty());
            this.children = new ArrayList<>(children);
        }

        @Override
        public int getHeight(int width) {
            int result = 0;
            int rowHeight = 0;
            int rowWidth = 0;
            for (AbstractWidget child : children) {
                int childWidth = child.getWidth();
                if (rowWidth + childWidth > width) {
                    result += rowHeight;
                    rowWidth = childWidth;
                    rowHeight = child.getHeight();
                } else {
                    rowWidth += childWidth;
                    rowHeight = Math.max(rowHeight, child.getHeight());
                }
            }
            result += rowHeight;
            return result;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int x = getX();
            int y = getY();
            int maxHeight = 0;
            int xOffset = 0;
            for (AbstractWidget child : children) {
                int childWidth = child.getWidth();
                if (xOffset + childWidth > getWidth()) {
                    xOffset = 0;
                    y += maxHeight;
                    maxHeight = 0;
                }
                child.setX(x + xOffset);
                child.setY(y);
                child.render(guiGraphics, mouseX, mouseY, partialTick);
                maxHeight = Math.max(maxHeight, child.getHeight());
                xOffset += childWidth;
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }
    }
}
