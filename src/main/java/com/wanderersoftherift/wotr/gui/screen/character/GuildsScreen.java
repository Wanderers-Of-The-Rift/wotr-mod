package com.wanderersoftherift.wotr.gui.screen.character;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTrack;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTracker;
import com.wanderersoftherift.wotr.gui.menu.character.GuildMenu;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.network.guild.ClaimTrackRewardPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A character screen displays information on guilds the player is affiliated with
 */
public class GuildsScreen extends BaseCharacterScreen<GuildMenu> {

    private ScrollContainerWidget<GuildDisplay> guildsDisplay;
    private ProgressionTracker progressionTracker;

    public GuildsScreen(GuildMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        progressionTracker = minecraft.player.getData(WotrAttachments.PROGRESSION_TRACKER);
        List<GuildDisplay> displays = new ArrayList<>();
        Minecraft.getInstance().player.registryAccess()
                .lookupOrThrow(WotrRegistries.Keys.PROGRESSION_TRACKS)
                .getTagOrEmpty(WotrTags.ProgressionTracks.GUILDS)
                .forEach(guild -> {
                    if (progressionTracker.getPoints(guild) > 0 || progressionTracker.getRank(guild) > 0) {
                        displays.add(new GuildDisplay(font, guild, progressionTracker,
                                progressionTracker.hasUnclaimedRewards(guild)));
                    }
                }
                );
        guildsDisplay = new ScrollContainerWidget<>(300, 30, 200, 140, displays);
        addRenderableWidget(guildsDisplay);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guildsDisplay.setHeight(guiGraphics.guiHeight() - 60);
        guildsDisplay.setX((guiGraphics.guiWidth() - guildsDisplay.getWidth() - MENU_BAR_WIDTH) / 2 + MENU_BAR_WIDTH);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    private static class GuildDisplay extends AbstractContainerWidget implements ScrollContainerEntry {

        private static final int EMBLEM_SIZE = 32;
        private static final int DISPLAY_HEIGHT = 47;
        private static final Component CLAIM_MESSAGE = Component
                .translatable(WanderersOfTheRift.translationId("container", "guilds.claim_reward"));

        private final Font font;
        private final Holder<ProgressionTrack> track;
        private final ProgressionTracker status;
        private final Button claimRewardsButton;
        private final boolean hasRewards;

        public GuildDisplay(Font font, Holder<ProgressionTrack> track, ProgressionTracker status, boolean hasRewards) {
            super(0, 0, 300, DISPLAY_HEIGHT, ProgressionTrack.getDisplayName(track));
            this.font = font;
            this.track = track;
            this.status = status;
            this.hasRewards = hasRewards;
            this.claimRewardsButton = new Button.Builder(CLAIM_MESSAGE, button -> {
                if (hasRewards) {
                    PacketDistributor.sendToServer(new ClaimTrackRewardPayload(track));
                }
            }).pos(0, 0).size(font.width(CLAIM_MESSAGE) + 8, font.lineHeight + 8).build();
        }

        @Override
        public int getHeight(int width) {
            int height = DISPLAY_HEIGHT;
            if (hasRewards) {
                height += claimRewardsButton.getHeight();
            }
            return height;
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(claimRewardsButton);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            track.value().displayIcon().ifPresent(icon -> {
                guiGraphics.blit(RenderType::guiTextured, icon, getX() + 4, getY() + 4, 0, 0, EMBLEM_SIZE, EMBLEM_SIZE,
                        EMBLEM_SIZE, EMBLEM_SIZE);
            });
            guiGraphics.drawString(font, getMessage(), getX() + EMBLEM_SIZE + 8, getY() + 8,
                    ChatFormatting.WHITE.getColor(), true);
            int rank = status.getRank(track);
            guiGraphics.drawString(font,
                    Component.translatable(WanderersOfTheRift.translationId("container", "guild.rank"),
                            ProgressionTrack.getRankTitle(track, rank)),
                    getX() + EMBLEM_SIZE + 8, getY() + 8 + 2 * font.lineHeight, ChatFormatting.WHITE.getColor(), false);

            Component reputationLabel;
            if (rank + 1 <= track.value().rankCount()) {
                reputationLabel = Component.translatable(
                        WanderersOfTheRift.translationId("container", "guild.reputation"), status.getPoints(track),
                        track.value().getRank(rank + 1).requirement());

            } else {
                reputationLabel = Component
                        .translatable(WanderersOfTheRift.translationId("container", "guild.reputation.max"));
            }

            guiGraphics.drawString(font, reputationLabel, getX() + EMBLEM_SIZE + 8, getY() + 8 + 3 * font.lineHeight,
                    ChatFormatting.WHITE.getColor(), false);

            if (hasRewards) {
                claimRewardsButton.setX(getX() + 4);
                claimRewardsButton.setY(getY() + font.lineHeight * 5);
                claimRewardsButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
            // TODO
        }

        @Override
        protected int contentHeight() {
            return getHeight(getWidth());
        }

        @Override
        protected double scrollRate() {
            return 0;
        }
    }
}
