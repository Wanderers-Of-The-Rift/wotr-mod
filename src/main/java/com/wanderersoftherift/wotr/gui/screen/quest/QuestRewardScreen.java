package com.wanderersoftherift.wotr.gui.screen.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestRewardMenu;
import com.wanderersoftherift.wotr.gui.screen.EnhancedContainerScreen;
import com.wanderersoftherift.wotr.gui.widget.lookup.RewardDisplays;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.FlowContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;

public class QuestRewardScreen extends EnhancedContainerScreen<QuestRewardMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/quest_complete/background.png");
    private static final int BACKGROUND_WIDTH = 176;
    private static final int BACKGROUND_HEIGHT = 162;

    public QuestRewardScreen(QuestRewardMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = BACKGROUND_WIDTH;
        imageHeight = BACKGROUND_HEIGHT;
        inventoryLabelX = 7;
        inventoryLabelY = 69;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (BACKGROUND_WIDTH - font.width(title)) / 2;
        List<AbstractWidget> rewards = menu.getNonItemRewards()
                .stream()
                .map(RewardDisplays::createFor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        FlowContainer rewardContainer = new FlowContainer(rewards, 2);
        rewardContainer.setRectangle(162, 16, 7, 28);

        addRenderableWidget(rewardContainer);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth,
                this.imageHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }
}
