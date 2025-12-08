package com.wanderersoftherift.wotr.gui.screen.character;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.gui.menu.character.QuestMenu;
import com.wanderersoftherift.wotr.gui.widget.ConfirmButton;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.gui.widget.goal.GoalStateWidget;
import com.wanderersoftherift.wotr.gui.widget.lookup.RewardDisplays;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.FlowContainer;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.LabelEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.PanelContainer;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.RowContainer;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.SpacerEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.WidgetEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.WrappedTextEntry;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.network.quest.AbandonQuestPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A character screen displaying quests the player has accepted
 */
public class QuestsScreen extends BaseCharacterScreen<QuestMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift.id("background/panel");

    private static final Component GOAL_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.goals"));
    private static final Component REWARDS_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.rewards"));
    private static final Component ABANDON_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.abandon"));
    private static final Component ARE_YOU_SURE_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.are_you_sure"));
    private static final String HAND_IN_TO = WanderersOfTheRift.translationId("container", "quest.hand_in_to");

    private static final int ABANDON_BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 16;

    private ScrollContainerWidget<ScrollContainerEntry> questInfo;

    private List<QuestState> activeQuests;

    public QuestsScreen(QuestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        questInfo = new ScrollContainerWidget<>(300, 30, 300, 140);
        questInfo.setSpacing(4);
        addRenderableWidget(questInfo);

        activeQuests = Minecraft.getInstance().player.getData(WotrAttachments.ACTIVE_QUESTS).getQuestList();
        for (int i = 0; i < activeQuests.size(); i++) {
            addQuest(questInfo, i, activeQuests.get(i));
        }
    }

    private void addQuest(ScrollContainerWidget<ScrollContainerEntry> questInfo, int index, QuestState questState) {
        List<ScrollContainerEntry> rows = new ArrayList<>();
        rows.add(new LabelEntry(font, Quest.title(questState.getOrigin()), 4));
        rows.add(new WrappedTextEntry(font, Quest.description(questState.getOrigin())));
        rows.add(new SpacerEntry(4));
        rows.add(new LabelEntry(font, GOAL_LABEL, 0));

        questState.getGoalStates().stream().map(GoalStateWidget::new).forEach(rows::add);
        rows.add(new SpacerEntry(2));
        rows.add(new LabelEntry(font,
                Component.translatable(HAND_IN_TO, NpcIdentity.getDisplayName(questState.getHandInTo())), 4));
        rows.add(new LabelEntry(font, REWARDS_LABEL, 4));

        List<? extends AbstractWidget> rewards = questState.getRewards()
                .stream()
                .map(RewardDisplays::createFor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        rows.add(new FlowContainer(rewards, 2));
        rows.add(new SpacerEntry(2));
        Button abandon = new ConfirmButton(0, 0, ABANDON_BUTTON_WIDTH, BUTTON_HEIGHT, ABANDON_LABEL, ARE_YOU_SURE_LABEL,
                (button) -> {
                    PacketDistributor.sendToServer(new AbandonQuestPayload(activeQuests.get(index).getId()));
                    activeQuests.remove(index);
                    questInfo.children().remove(index);
                });
        rows.add(new WidgetEntry(abandon));
        questInfo.addChild(new PanelContainer(new RowContainer(rows), BACKGROUND, 5));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        questInfo.setHeight(guiGraphics.guiHeight() - 40);
        questInfo.setX((guiGraphics.guiWidth() - questInfo.getWidth() - MENU_BAR_WIDTH) / 2 + MENU_BAR_WIDTH);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
