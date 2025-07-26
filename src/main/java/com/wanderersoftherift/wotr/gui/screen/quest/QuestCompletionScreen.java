package com.wanderersoftherift.wotr.gui.screen.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import com.wanderersoftherift.wotr.gui.screen.EnhancedContainerScreen;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.gui.widget.lookup.RewardDisplays;
import com.wanderersoftherift.wotr.gui.widget.quest.GoalStateWidget;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.FlowContainer;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.LabelEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.SpacerEntry;
import com.wanderersoftherift.wotr.network.quest.CompleteQuestPayload;
import com.wanderersoftherift.wotr.network.quest.HandInQuestItemPayload;
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
 * The QuestCompletionScreen shows a player's progress on a quest and allows handing in items and completing the quest
 */
public class QuestCompletionScreen extends EnhancedContainerScreen<QuestCompletionMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/quest/completion.png");
    private static final int BACKGROUND_WIDTH = 326;
    private static final int BACKGROUND_HEIGHT = 200;

    private static final Component REWARDS_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.rewards"));

    private ScrollContainerWidget<ScrollContainerEntry> questInfo;

    private Button handInItems;
    private Button complete;
    private QuestState currentQuest;

    public QuestCompletionScreen(QuestCompletionMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = BACKGROUND_WIDTH;
        this.imageHeight = BACKGROUND_HEIGHT;
        this.inventoryLabelX = 158;
        this.inventoryLabelY = 106;
    }

    @Override
    protected void init() {
        super.init();
        currentQuest = menu.getQuestState().orElse(null);
        updateQuestDisplay();

        handInItems = Button
                .builder(Component.translatable(WanderersOfTheRift.translationId("container", "quest.handin")),
                        button -> {
                            PacketDistributor.sendToServer(new HandInQuestItemPayload());
                        })
                .bounds(leftPos + 157, topPos + 57, 44, 16)
                .build();
        addRenderableWidget(handInItems);

        complete = Button
                .builder(Component.translatable(WanderersOfTheRift.translationId("container", "quest.complete")),
                        button -> {
                            PacketDistributor
                                    .sendToServer(new CompleteQuestPayload(menu.getQuestState().get().getId()));
                        })
                .bounds(leftPos + 157, topPos + 78, 60, 16)
                .build();
        addRenderableWidget(complete);
    }

    private void updateButtons() {
        complete.active = currentQuest != null && currentQuest.isComplete();
        handInItems.active = menu.hasItemToHandIn();
    }

    private void updateQuestDisplay() {
        QuestState newState = menu.getQuestState().orElse(null);
        if (currentQuest == newState) {
            return;
        }
        if (questInfo != null) {
            removeWidget(questInfo);
        }
        currentQuest = newState;
        if (currentQuest == null) {
            return;
        }
        questInfo = new ScrollContainerWidget<>(leftPos + 5, topPos + 32, 149, 160);
        for (int i = 0; i < currentQuest.goalCount(); i++) {
            questInfo.children().add(new GoalStateWidget(currentQuest, i));
        }
        questInfo.children().add(new SpacerEntry(6));
        questInfo.children().add(new LabelEntry(font, REWARDS_LABEL, 4));
        List<AbstractWidget> rewards = currentQuest.getRewards()
                .stream()
                .map(RewardDisplays::createFor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        questInfo.children().add(new FlowContainer(rewards, 2));
        addRenderableWidget(questInfo);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateQuestDisplay();
        updateButtons();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        if (currentQuest != null) {
            guiGraphics.drawString(this.font, Quest.title(currentQuest.getOrigin()), this.titleLabelX,
                    this.titleLabelY + 12, ColorUtil.OFF_BLACK, false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth,
                this.imageHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

}
