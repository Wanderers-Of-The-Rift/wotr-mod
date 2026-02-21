package com.wanderersoftherift.wotr.gui.menu.reward;

import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.gui.menu.QuickMover;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.util.ItemStackHandlerUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This menu provides rewards to the player after they complete a quest (or otherwise earn a reward).
 * <p>
 * Item rewards are handle by populating a small inventory with their items - overflow are given straight to the player
 * or dropped at the player's location. Other rewards have their icon displayed and are rewarded when the menu is
 * closed.
 * </p>
 */
public class RewardMenu extends AbstractRewardMenu {

    public static final StreamCodec<RegistryFriendlyByteBuf, List<RewardSlot>> REWARD_MENU_CODEC = RewardSlot.STREAM_CODEC
            .apply(ByteBufCodecs.list());

    private static final int NUM_REWARD_SLOTS = 9;
    private static final QuickMover MOVER = QuickMover.create()
            .forPlayerSlots(NUM_REWARD_SLOTS)
            .forSlots(0, NUM_REWARD_SLOTS)
            .tryMoveToPlayer()
            .build();

    private final ItemStackHandler itemRewards;
    private final ContainerLevelAccess access;

    public RewardMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, new ItemStackHandler(NUM_REWARD_SLOTS), ContainerLevelAccess.NULL,
                REWARD_MENU_CODEC.decode(data));
    }

    public RewardMenu(int containerId, Inventory playerInventory, ItemStackHandler itemRewards,
            ContainerLevelAccess access, List<RewardSlot> nonItemRewards) {
        super(WotrMenuTypes.REWARD_MENU.get(), containerId, nonItemRewards, access);

        this.access = access;
        this.itemRewards = itemRewards;

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
        ItemStackHandler itemRewards = new ItemStackHandler(NUM_REWARD_SLOTS);
        List<RewardSlot> nonItemRewards = new ArrayList<>();

        for (Reward reward : rewards) {
            if (reward.isItem()) {
                ItemStack item = reward.generateItem();
                ItemStackHandlerUtil.addOrGiveToPlayerOrDrop(item, itemRewards, player);
            } else {
                nonItemRewards.add(new RewardSlot(nonItemRewards.size(), reward));
            }
        }
        player.openMenu(
                new SimpleMenuProvider((containerId, playerInventory, p) -> new RewardMenu(containerId, playerInventory,
                        itemRewards, ContainerLevelAccess.create(p.level(), p.getOnPos()), nonItemRewards), title),
                registryFriendlyByteBuf -> REWARD_MENU_CODEC.encode(registryFriendlyByteBuf, nonItemRewards));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return MOVER.quickMove(this, player, index);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.access.execute((world, pos) -> {
            ItemStackHandlerUtil.placeInPlayerInventoryOrDrop(player, itemRewards);
        });
    }
}
