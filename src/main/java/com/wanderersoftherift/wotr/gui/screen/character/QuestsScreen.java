package com.wanderersoftherift.wotr.gui.screen.character;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuest;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.guild.quest.Quest;
import com.wanderersoftherift.wotr.gui.menu.character.QuestMenu;
import com.wanderersoftherift.wotr.gui.widget.GoalStateWidget;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.gui.widget.lookup.RewardDisplays;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.FlowContainer;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.LabelEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.SpacerEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.WrappedTextEntry;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class QuestsScreen extends BaseCharacterScreen<QuestMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/character/background.png");

    private static final Component GOAL_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.goals"));
    private static final Component REWARDS_LABEL = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.rewards"));

    private ScrollContainerWidget<ScrollContainerEntry> questInfo;

    public QuestsScreen(QuestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        questInfo = new ScrollContainerWidget<>(300, 30, 300, 140);
        ActiveQuests activeQuests = Minecraft.getInstance().player.getData(WotrAttachments.ACTIVE_QUESTS);
        if (!activeQuests.quests().isEmpty()) {
            ActiveQuest activeQuest = activeQuests.quests().get(0);
            questInfo.children().add(new LabelEntry(font, Quest.title(activeQuest.getQuest()), 4));
            questInfo.children().add(new WrappedTextEntry(font, Quest.description(activeQuest.getQuest())));
            questInfo.children().add(new SpacerEntry(4));
            questInfo.children().add(new LabelEntry(font, GOAL_LABEL, 4));
            for (int i = 0; i < activeQuest.goalCount(); i++) {
                questInfo.children().add(new GoalStateWidget(activeQuest, i));
            }
            questInfo.children().add(new SpacerEntry(2));
            questInfo.children().add(new LabelEntry(font, REWARDS_LABEL, 4));
            List<AbstractWidget> rewards = activeQuest.getQuest()
                    .value()
                    .rewards()
                    .stream()
                    .map(RewardDisplays::createFor)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            questInfo.children().add(new FlowContainer(rewards));
        }
        addRenderableWidget(questInfo);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        questInfo.setHeight(guiGraphics.guiHeight() - 60);
        questInfo.setX((guiGraphics.guiWidth() - questInfo.getWidth() - MENU_BAR_WIDTH) / 2 + MENU_BAR_WIDTH);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, questInfo.getX() - 6, questInfo.getY() - 6, 0, 0, 310,
                150, 310, 150);
    }

}
