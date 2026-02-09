package com.wanderersoftherift.wotr.gui.screen.character;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTrack;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTracker;
import com.wanderersoftherift.wotr.gui.menu.character.MainCharacterMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.guild.ClaimTrackRewardPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

/**
 * A character screen displays information on guilds the player is affiliated with
 */
public class MainCharacterScreen extends BaseCharacterScreen<MainCharacterMenu> {

    private static final int WIDTH = 200;
    private static final ResourceKey<ProgressionTrack> LEVEL_TRACK = ResourceKey
            .create(WotrRegistries.Keys.PROGRESSION_TRACKS, WanderersOfTheRift.id("level"));
    private static final Component CLAIM_MESSAGE = Component
            .translatable(WanderersOfTheRift.translationId("container", "character.claim_reward"));

    private Holder<ProgressionTrack> levelTrack;
    private ProgressionTracker progressionTracker;
    private Button claimRewardsButton;

    public MainCharacterScreen(MainCharacterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        progressionTracker = minecraft.player.getData(WotrAttachments.PROGRESSION_TRACKER);
        levelTrack = minecraft.player.registryAccess().holderOrThrow(LEVEL_TRACK);
        this.claimRewardsButton = new Button.Builder(CLAIM_MESSAGE, button -> {
            if (progressionTracker.hasUnclaimedRewards(levelTrack)) {
                PacketDistributor.sendToServer(new ClaimTrackRewardPayload(levelTrack));
            }
        }).pos(0, 0).size(font.width(CLAIM_MESSAGE) + 8, font.lineHeight + 8).build();
        addRenderableWidget(claimRewardsButton);
        claimRewardsButton.visible = false;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int yOffset = topPos + 30;
        int xOffset = (guiGraphics.guiWidth() - WIDTH - MENU_BAR_WIDTH) / 2 + MENU_BAR_WIDTH;

        int level = progressionTracker.getRank(levelTrack);
        guiGraphics.drawString(font,
                Component.translatable(WanderersOfTheRift.translationId("container", "character.level"), level),
                xOffset, yOffset, ChatFormatting.WHITE.getColor(), false);
        yOffset += font.lineHeight;
        int xp = progressionTracker.getPoints(levelTrack);
        guiGraphics.drawString(font,
                Component.translatable(WanderersOfTheRift.translationId("container", "character.xp"), xp), xOffset,
                yOffset, ChatFormatting.WHITE.getColor(), false);
        yOffset += font.lineHeight;

        if (level < levelTrack.value().rankCount() - 1) {
            guiGraphics.drawString(font,
                    Component.translatable(WanderersOfTheRift.translationId("container", "character.xp.next_level"),
                            levelTrack.value().getRank(level + 1).requirement()),
                    xOffset, yOffset, ChatFormatting.WHITE.getColor(), false);
            yOffset += font.lineHeight;
        }
        yOffset += font.lineHeight;
        boolean hasRewards = progressionTracker.hasUnclaimedRewards(levelTrack);
        claimRewardsButton.visible = hasRewards;
        if (hasRewards) {
            claimRewardsButton.setX(xOffset + 4);
            claimRewardsButton.setY(yOffset);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }
}
