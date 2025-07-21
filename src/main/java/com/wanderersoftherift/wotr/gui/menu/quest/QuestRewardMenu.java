package com.wanderersoftherift.wotr.gui.menu.quest;

import com.wanderersoftherift.wotr.core.guild.quest.Reward;
import com.wanderersoftherift.wotr.gui.menu.QuickMover;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.util.ItemStackHandlerUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This menu provides rewards to the player after they complete a quest.
 * <p>
 * Item rewards are handle by populating a small inventory with their items - overflow are given straight to the player
 * or dropped at the player's location. Other rewards have their icon displayed and are rewarded when the menu is
 * closed.
 * </p>
 */
public class QuestRewardMenu extends AbstractContainerMenu {

    private static final int NUM_REWARD_SLOTS = 9;
    private static final QuickMover MOVER = QuickMover.create()
            .forPlayerSlots(NUM_REWARD_SLOTS)
            .forSlots(0, NUM_REWARD_SLOTS)
            .tryMoveToPlayer()
            .build();

    private final ContainerLevelAccess access;
    private final ItemStackHandler itemRewards;
    private final List<Reward> nonItemRewards = new ArrayList<>();

    public QuestRewardMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public QuestRewardMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(WotrMenuTypes.QUEST_REWARD_MENU.get(), containerId);

        this.access = access;
        this.itemRewards = new ItemStackHandler(NUM_REWARD_SLOTS);

        for (int i = 0; i < NUM_REWARD_SLOTS; i++) {
            this.addSlot(new SlotItemHandler(itemRewards, i, 8 + 18 * i, 49));
        }
        addStandardInventorySlots(playerInventory, 8, 80);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return MOVER.quickMove(this, player, index);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public List<Reward> getNonItemRewards() {
        return nonItemRewards;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStackHandlerUtil.placeInPlayerInventoryOrDrop(serverPlayer, itemRewards);
            for (Reward reward : nonItemRewards) {
                reward.apply(serverPlayer);
            }
        }
    }

    public void setRewards(Player player, List<Reward> rewards) {
        nonItemRewards.addAll(rewards.stream().filter(x -> !x.isItem()).toList());
        access.execute((level, pos) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            for (Reward reward : rewards) {
                ItemStack rewardItem = reward.generateItem();
                if (!rewardItem.isEmpty()) {
                    ItemStackHandlerUtil.addOrGiveToPlayerOrDrop(rewardItem, itemRewards, serverPlayer);
                }
            }
        });
    }
}
