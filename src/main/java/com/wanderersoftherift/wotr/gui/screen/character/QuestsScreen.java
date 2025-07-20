package com.wanderersoftherift.wotr.gui.screen.character;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.guild.quest.Quest;
import com.wanderersoftherift.wotr.core.guild.quest.QuestState;
import com.wanderersoftherift.wotr.gui.menu.character.QuestMenu;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.gui.widget.lookup.RewardDisplays;
import com.wanderersoftherift.wotr.gui.widget.quest.GoalStateWidget;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.FlowContainer;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.LabelEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.SpacerEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.WrappedTextEntry;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.network.quest.AbandonQuestPayload;
import net.minecraft.client.Minecraft;
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
 * A character screen displaying quests the player has accepted //TODO: Currently it only shows one quest
 */
public class QuestsScreen extends BaseCharacterScreen<QuestMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/character/background.png");

    private static final Component GOAL_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.goals"));
    private static final Component REWARDS_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.rewards"));
    private static final Component ABANDON_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.abandon"));
    private static final Component ARE_YOU_SURE_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.are_you_sure"));

    private static final int ABANDON_BUTTON_WIDTH = 100;

    private ScrollContainerWidget<ScrollContainerEntry> questInfo;

    private boolean confirmAbandon = false;
    private Button abandonQuest;

    public QuestsScreen(QuestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        questInfo = new ScrollContainerWidget<>(300, 30, 300, 140);
        addRenderableWidget(questInfo);

        ActiveQuests activeQuests = Minecraft.getInstance().player.getData(WotrAttachments.ACTIVE_QUESTS);
        abandonQuest = Button.builder(ABANDON_LABEL, button -> {
            if (confirmAbandon) {
                button.setMessage(ABANDON_LABEL);
                confirmAbandon = false;
                PacketDistributor.sendToServer(new AbandonQuestPayload(activeQuests.getQuestState(0).getId()));
                questInfo.children().clear();
                button.visible = false;
            } else {
                button.setMessage(ARE_YOU_SURE_LABEL);
                confirmAbandon = true;
            }
        }).bounds(0, 0, ABANDON_BUTTON_WIDTH, 16).build();
        addRenderableWidget(abandonQuest);

        if (!activeQuests.isEmpty()) {
            QuestState questState = activeQuests.getQuestState(0);
            questInfo.children().add(new LabelEntry(font, Quest.title(questState.getOrigin()), 4));
            questInfo.children().add(new WrappedTextEntry(font, Quest.description(questState.getOrigin())));
            questInfo.children().add(new SpacerEntry(4));
            questInfo.children().add(new LabelEntry(font, GOAL_LABEL, 0));
            for (int i = 0; i < questState.goalCount(); i++) {
                questInfo.children().add(new GoalStateWidget(questState, i));
            }
            questInfo.children().add(new SpacerEntry(2));
            questInfo.children().add(new LabelEntry(font, REWARDS_LABEL, 4));
            List<AbstractWidget> rewards = questState.getRewards()
                    .stream()
                    .map(RewardDisplays::createFor)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            questInfo.children().add(new FlowContainer(rewards, 2));
        } else {
            abandonQuest.visible = false;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        questInfo.setHeight(guiGraphics.guiHeight() - 60);
        questInfo.setX((guiGraphics.guiWidth() - questInfo.getWidth() - MENU_BAR_WIDTH) / 2 + MENU_BAR_WIDTH);
        abandonQuest.setY(questInfo.getY() + questInfo.getHeight());
        abandonQuest.setX(questInfo.getX() + questInfo.getWidth() - abandonQuest.getWidth());
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        if (!questInfo.children().isEmpty()) {
            guiGraphics.blit(RenderType::guiTextured, BACKGROUND, questInfo.getX() - 6, questInfo.getY() - 6, 0, 0, 310,
                    150, 310, 150);
        }
    }

}
