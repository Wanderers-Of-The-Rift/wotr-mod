package com.wanderersoftherift.wotr.gui.screen.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.reward.RewardMenu;
import com.wanderersoftherift.wotr.gui.menu.reward.RewardSlot;
import com.wanderersoftherift.wotr.gui.screen.EnhancedContainerScreen;
import com.wanderersoftherift.wotr.gui.widget.lookup.RewardDisplays;
import com.wanderersoftherift.wotr.gui.widget.reward.RewardWidget;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.FlowContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A screen for displaying rewards from completing a quest
 */
public class RewardScreen extends EnhancedContainerScreen<RewardMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/quest_complete/background.png");
    private static final int BACKGROUND_WIDTH = 176;
    private static final int BACKGROUND_HEIGHT = 162;

    public RewardScreen(RewardMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = BACKGROUND_WIDTH;
        imageHeight = BACKGROUND_HEIGHT;
        inventoryLabelX = 7;
        inventoryLabelY = 69;
    }

    @Override
    protected void init() {
        clearWidgets();
        super.init();
        titleLabelX = (BACKGROUND_WIDTH - font.width(title)) / 2;
        List<RewardWidget> rewardWidgets = new ArrayList<>();
        for (RewardSlot rewardSlot : menu.getNonItemRewards()) {
            Optional<RewardWidget> widget = RewardDisplays.createFor(rewardSlot.reward());
            if (widget.isPresent()) {
                widget.get().setClickListener(reward -> menu.clientClaimReward(rewardSlot));
                rewardWidgets.add(widget.get());
            }
        }
        FlowContainer rewardContainer = new FlowContainer(rewardWidgets, 2);
        rewardContainer.setRectangle(162, 16, leftPos + 7, topPos + 28);

        addRenderableWidget(rewardContainer);
        menu.clearRewardsChangedFlag();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (menu.isRewardsChanged()) {
            init();
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth,
                this.imageHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }
}
