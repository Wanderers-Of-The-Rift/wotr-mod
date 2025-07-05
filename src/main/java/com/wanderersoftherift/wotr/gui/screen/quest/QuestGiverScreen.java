package com.wanderersoftherift.wotr.gui.screen.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuest;
import com.wanderersoftherift.wotr.core.guild.quest.Quest;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import com.wanderersoftherift.wotr.gui.screen.EnhancedContainerScreen;
import com.wanderersoftherift.wotr.gui.widget.GoalStateWidget;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.gui.widget.lookup.RewardDisplays;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.FlowContainer;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.LabelEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.SpacerEntry;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.WrappedTextEntry;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.guild.AcceptQuestPayload;
import com.wanderersoftherift.wotr.util.ColorUtil;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

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
                    if (selectedQuest != null) {
                        PacketDistributor.sendToServer(new AcceptQuestPayload(selectedQuest));
                    }
                }).bounds(leftPos + 108, topPos + 142, 40, 16).build();
        addRenderableWidget(accept);
        accept.visible = false;
    }

    private void selectQuest(Holder<Quest> quest) {
        selectedQuest = quest;
        ActiveQuest fakeQuest = new ActiveQuest(quest);
        questInfo.children().clear();
        questInfo.children().add(new LabelEntry(font, Quest.title(quest), 4));
        questInfo.children().add(new WrappedTextEntry(font, Quest.description(quest)));
        questInfo.children().add(new SpacerEntry(4));
        questInfo.children().add(new LabelEntry(font, GOAL_LABEL, 4));
        for (int i = 0; i < selectedQuest.value().goals().size(); i++) {
            questInfo.children().add(new GoalStateWidget(fakeQuest, i));
        }
        questInfo.children().add(new SpacerEntry(2));
        questInfo.children().add(new LabelEntry(font, REWARDS_LABEL, 4));
        List<AbstractWidget> rewards = selectedQuest.value()
                .rewards()
                .stream()
                .map(RewardDisplays::createFor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        questInfo.children().add(new FlowContainer(rewards));
        // Accept button
        accept.visible = true;
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

}
