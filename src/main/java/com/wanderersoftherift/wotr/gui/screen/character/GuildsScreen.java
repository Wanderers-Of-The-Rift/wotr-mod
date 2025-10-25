package com.wanderersoftherift.wotr.gui.screen.character;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.core.guild.GuildStatus;
import com.wanderersoftherift.wotr.core.guild.UnclaimedGuildRewards;
import com.wanderersoftherift.wotr.gui.menu.character.GuildMenu;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.network.guild.ClaimGuildRewardPayload;
import net.minecraft.ChatFormatting;
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

import java.util.List;

/**
 * A character screen displays information on guilds the player is affiliated with
 */
public class GuildsScreen extends BaseCharacterScreen<GuildMenu> {

    private ScrollContainerWidget<GuildDisplay> guildsDisplay;
    private GuildStatus guildStatus;

    public GuildsScreen(GuildMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        guildStatus = minecraft.player.getData(WotrAttachments.GUILD_STATUS);
        List<Holder<Guild>> guilds = guildStatus.getGuildsWithStanding();
        UnclaimedGuildRewards unclaimedGuildRewards = minecraft.player.getData(WotrAttachments.UNCLAIMED_GUILD_REWARDS);
        guildsDisplay = new ScrollContainerWidget<>(300, 30, 200, 140, guilds.stream()
                .map(guild -> new GuildDisplay(font, guild, guildStatus, unclaimedGuildRewards.hasRewards(guild)))
                .toList());
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
        private final Holder<Guild> guild;
        private final GuildStatus status;
        private final Button claimRewardsButton;
        private final boolean hasRewards;

        public GuildDisplay(Font font, Holder<Guild> guild, GuildStatus status, boolean hasRewards) {
            super(0, 0, 300, DISPLAY_HEIGHT, Guild.getDisplayName(guild));
            this.font = font;
            this.guild = guild;
            this.status = status;
            this.hasRewards = hasRewards;
            this.claimRewardsButton = new Button.Builder(CLAIM_MESSAGE, button -> {
                if (hasRewards) {
                    PacketDistributor.sendToServer(new ClaimGuildRewardPayload(guild));
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
            guiGraphics.blit(RenderType::guiTextured, guild.value().icon32(), getX() + 4, getY() + 4, 0, 0, EMBLEM_SIZE,
                    EMBLEM_SIZE, EMBLEM_SIZE, EMBLEM_SIZE);
            guiGraphics.drawString(font, getMessage(), getX() + EMBLEM_SIZE + 8, getY() + 8,
                    ChatFormatting.WHITE.getColor(), true);
            int rank = status.getRank(guild);
            guiGraphics.drawString(font,
                    Component.translatable(WanderersOfTheRift.translationId("container", "guild.rank"),
                            Guild.getRankTitle(guild, rank)),
                    getX() + EMBLEM_SIZE + 8, getY() + 8 + 2 * font.lineHeight, ChatFormatting.WHITE.getColor(), false);

            Component reputationLabel;
            if (rank < guild.value().ranks().size()) {
                reputationLabel = Component.translatable(
                        WanderersOfTheRift.translationId("container", "guild.reputation"), status.getReputation(guild),
                        guild.value().getRank(rank + 1).reputationRequirement());

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
