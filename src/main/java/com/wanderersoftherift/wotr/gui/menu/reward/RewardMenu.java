package com.wanderersoftherift.wotr.gui.menu.reward;

import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.gui.menu.QuickMover;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.network.reward.ClaimRewardPayload;
import com.wanderersoftherift.wotr.network.reward.RewardsPayload;
import com.wanderersoftherift.wotr.util.ItemStackHandlerUtil;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * This menu provides rewards to the player after they complete a quest (or otherwise earn a reward).
 * <p>
 * Item rewards are handle by populating a small inventory with their items - overflow are given straight to the player
 * or dropped at the player's location. Other rewards have their icon displayed and are rewarded when the menu is
 * closed.
 * </p>
 */
public class RewardMenu extends AbstractContainerMenu {

    private static final int NUM_REWARD_SLOTS = 9;
    private static final QuickMover MOVER = QuickMover.create()
            .forPlayerSlots(NUM_REWARD_SLOTS)
            .forSlots(0, NUM_REWARD_SLOTS)
            .tryMoveToPlayer()
            .build();

    private final ContainerLevelAccess access;
    private final ItemStackHandler itemRewards;
    private final IntObjectMap<RewardSlot> nonItemRewards = new IntObjectHashMap<>();
    private int nextId = 0;

    private boolean dirty = true;

    public RewardMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public RewardMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(WotrMenuTypes.REWARD_MENU.get(), containerId);

        this.access = access;
        this.itemRewards = new ItemStackHandler(NUM_REWARD_SLOTS);

        for (int i = 0; i < NUM_REWARD_SLOTS; i++) {
            this.addSlot(new SlotItemHandler(itemRewards, i, 8 + 18 * i, 49));
        }
        addStandardInventorySlots(playerInventory, 8, 80);
    }

    /**
     * Opens a reward menu, or if a reward menu is already open adds rewards to it.
     * 
     * @param player  The player to open the menu
     * @param rewards A list of rewards to provide
     * @param title   The title of the menu (if there is an existing open menu the title doesn't change)
     */
    public static void openRewardMenu(Player player, List<Reward> rewards, Component title) {
        if (player.containerMenu instanceof RewardMenu existingMenu) {
            existingMenu.addRewards(player, rewards);
        } else {
            player.openMenu(new SimpleMenuProvider((containerId, playerInventory, p) -> {
                var menu = new RewardMenu(containerId, playerInventory,
                        ContainerLevelAccess.create(p.level(), p.getOnPos()));
                menu.addRewards(p, rewards);
                return menu;
            }, title));
        }
        if (player instanceof ServerPlayer serverPlayer && player.containerMenu instanceof RewardMenu menu) {
            PacketDistributor.sendToPlayer(serverPlayer, new RewardsPayload(List.copyOf(menu.nonItemRewards.values())));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return MOVER.quickMove(this, player, index);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    /**
     * @return A list of unclaimed non-item rewards
     */
    public Collection<RewardSlot> getNonItemRewards() {
        return nonItemRewards.values();
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStackHandlerUtil.placeInPlayerInventoryOrDrop(serverPlayer, itemRewards);
            for (RewardSlot rewardSlot : nonItemRewards.values()) {
                rewardSlot.reward().apply(serverPlayer);
            }
        }
    }

    /**
     * Adds rewards to the reward menu. If they don't fit in the menu they are directly given to the player or dropped
     * at their location.
     * 
     * @param player
     * @param rewards
     */
    public void addRewards(Player player, List<Reward> rewards) {
        access.execute((level, pos) -> {
            rewards.stream()
                    .filter(x -> !x.isItem())
                    .map(x -> new RewardSlot(nextId++, x))
                    .forEach(x -> nonItemRewards.put(x.id(), x));
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

    public void setClientRewards(List<RewardSlot> rewards) {
        nonItemRewards.clear();
        rewards.forEach(reward -> nonItemRewards.put(reward.id(), reward));
        dirty = true;
    }

    /**
     * Client-side request to claim a non-item reward
     * 
     * @param reward
     */
    public void clientClaimReward(RewardSlot reward) {
        RewardSlot removed = nonItemRewards.remove(reward.id());
        if (removed != null) {
            PacketDistributor.sendToServer(new ClaimRewardPayload(removed.id()));
            dirty = true;
        }
    }

    /**
     * Server-side handling of claiming a non-item reward
     * 
     * @param player
     * @param rewardId
     */
    public void claimReward(Player player, int rewardId) {
        access.execute((level, pos) -> {
            RewardSlot rewardSlot = nonItemRewards.remove(rewardId);
            if (rewardSlot != null) {
                rewardSlot.reward().apply(player);
            }
        });
    }

    /**
     * @return Whether the non-item rewards list has changed
     */
    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }
}
