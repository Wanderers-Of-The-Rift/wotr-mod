package com.wanderersoftherift.wotr.gui.screen.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import com.wanderersoftherift.wotr.gui.screen.EnhancedContainerScreen;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.gui.widget.lookup.RewardDisplays;
import com.wanderersoftherift.wotr.gui.widget.quest.GoalStateWidget;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.ButtonEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.FlowContainer;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.LabelEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.SpacerEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.WrappedTextEntry;
import com.wanderersoftherift.wotr.network.quest.AcceptQuestPayload;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * QuestGiverScreen displays a list of available quests and allows the player to accept one
 */
public class QuestGiverScreen extends EnhancedContainerScreen<QuestGiverMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift.id("textures/gui/container/quest/giver.png");
    private static final int BACKGROUND_WIDTH = 324;
    private static final int BACKGROUND_HEIGHT = 166;
    private static final int QUEST_ITEM_HEIGHT = 15;
    private static final Component GOAL_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.goals"));
    private static final Component REWARDS_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.rewards"));

    private ScrollContainerWidget<ScrollContainerEntry> questsWidget;

    private ScrollContainerWidget<ScrollContainerEntry> questInfo;

    private Integer selectedQuest;

    private Button accept;

    public QuestGiverScreen(QuestGiverMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = BACKGROUND_WIDTH;
        this.imageHeight = BACKGROUND_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        questsWidget = new ScrollContainerWidget<>(leftPos + 5, topPos + 18, 95, 140);
        updateQuestList();
        addRenderableWidget(questsWidget);

        questInfo = new ScrollContainerWidget<>(leftPos + 108, topPos + 20, 210, 120);
        addRenderableWidget(questInfo);

        accept = Button.builder(Component.translatable(WanderersOfTheRift.translationId("container", "quest.accept")),
                button -> {
                    if (selectedQuest != null) {
                        PacketDistributor.sendToServer(new AcceptQuestPayload(selectedQuest));
                    }
                }).bounds(leftPos + 108, topPos + 142, 40, 16).build();
        addRenderableWidget(accept);
        accept.visible = false;

        if (selectedQuest != null) {
            selectQuest(selectedQuest);
        }
    }

    private void updateQuestList() {
        if (menu.isDirty()) {
            questsWidget.children().clear();
            for (int i = 0; i < menu.getAvailableQuests().size(); i++) {
                final int index = i;
                QuestState quest = menu.getAvailableQuests().get(i);
                questsWidget.addChild(
                        new ButtonEntry(Quest.title(quest.getOrigin()), QUEST_ITEM_HEIGHT, () -> selectQuest(index)));
            }
            menu.clearDirty();
        }
    }

    private void selectQuest(int index) {
        selectedQuest = index;
        QuestState quest = menu.getAvailableQuests().get(index);

        questInfo.children().clear();
        questInfo.addChild(new LabelEntry(font, Quest.title(quest.getOrigin()), 4))
                .addChild(new WrappedTextEntry(font, Quest.description(quest.getOrigin())))
                .addChild(new SpacerEntry(4))
                .addChild(new LabelEntry(font, GOAL_LABEL, 0));
        for (int i = 0; i < quest.goalCount(); i++) {
            questInfo.addChild(new GoalStateWidget(quest, i));
        }
        questInfo.addChild(new SpacerEntry(2)).addChild(new LabelEntry(font, REWARDS_LABEL, 4));
        List<AbstractWidget> rewards = quest.getRewards()
                .stream()
                .map(RewardDisplays::createFor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        questInfo.addChild(new FlowContainer(rewards, 2)).addChild(new SpacerEntry(2));
        questInfo.setScrollAmount(0);
        // Accept button
        accept.visible = true;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateQuestList();
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

}
