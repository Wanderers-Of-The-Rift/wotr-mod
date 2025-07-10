package com.wanderersoftherift.wotr.gui.screen.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.QuestState;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import com.wanderersoftherift.wotr.gui.screen.EnhancedContainerScreen;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.gui.widget.lookup.RewardDisplays;
import com.wanderersoftherift.wotr.gui.widget.quest.GoalStateWidget;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.FlowContainer;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.LabelEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.SpacerEntry;
import com.wanderersoftherift.wotr.network.guild.CompleteQuestPayload;
import com.wanderersoftherift.wotr.network.guild.HandInQuestItemPayload;
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

public class QuestCompletionScreen extends EnhancedContainerScreen<QuestCompletionMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/quest/completion.png");
    private static final int BACKGROUND_WIDTH = 326;
    private static final int BACKGROUND_HEIGHT = 200;

    private static final Component GOAL_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.goals"));
    private static final Component REWARDS_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.rewards"));

    private ScrollContainerWidget<ScrollContainerEntry> questInfo;

    private Button handInItems;
    private Button complete;

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
        QuestState questState = menu.getQuestState();
        questInfo = new ScrollContainerWidget<>(leftPos + 5, topPos + 20, 149, 172);
        for (int i = 0; i < questState.goalCount(); i++) {
            questInfo.children().add(new GoalStateWidget(questState, i));
        }
        questInfo.children().add(new SpacerEntry(6));
        questInfo.children().add(new LabelEntry(font, REWARDS_LABEL, 4));
        List<AbstractWidget> rewards = questState.getRewards()
                .stream()
                .map(RewardDisplays::createFor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        questInfo.children().add(new FlowContainer(rewards));
        addRenderableWidget(questInfo);

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
                            PacketDistributor.sendToServer(new CompleteQuestPayload(menu.getQuestState().getId()));
                        })
                .bounds(leftPos + 157, topPos + 78, 60, 16)
                .build();
        addRenderableWidget(complete);
    }

    private void updateButtons() {
        complete.active = menu.getQuestState().isComplete();
        handInItems.active = menu.hasItemToHandIn();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (menu.getQuestState() == null) {
            return;
        }
        updateButtons();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth,
                this.imageHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

}
