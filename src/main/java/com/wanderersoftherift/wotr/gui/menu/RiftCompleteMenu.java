package com.wanderersoftherift.wotr.gui.menu;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.util.ItemStackHandlerUtil;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays statistics and rewards at the end of a rift
 */
public class RiftCompleteMenu extends AbstractContainerMenu {

    public static final List<ResourceLocation> STATS_SLOTS = ImmutableList.of(
            Stats.PLAY_TIME, Stats.DAMAGE_TAKEN, Stats.DAMAGE_DEALT, Stats.MOB_KILLS
    );

    public static final int FLAG_SUCCESS = 1;
    public static final int FLAG_SURVIVED = 2;
    public static final int FLAG_FAILED = 3;

    private static final int NUM_REWARD_SLOTS = 5;
    private static final int PLAYER_INVENTORY_SLOTS = 3 * 9;
    private static final int PLAYER_SLOTS = PLAYER_INVENTORY_SLOTS + 9;

    private static final QuickMover MOVER = QuickMover.create()
            .forPlayerSlots(NUM_REWARD_SLOTS)
            .forSlots(0, NUM_REWARD_SLOTS)
            .tryMoveToPlayer()
            .build();

    private final ContainerLevelAccess access;
    private final ItemStackHandler rewards;
    private final DataSlot resultStatus;
    private final List<DataSlot> stats = new ArrayList<>();

    public RiftCompleteMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL, 0, new Object2IntArrayMap<>());
    }

    public RiftCompleteMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, int successFlag,
            Object2IntMap<ResourceLocation> riftStats) {
        super(WotrMenuTypes.RIFT_COMPLETE_MENU.get(), containerId);
        this.access = access;
        this.rewards = new ItemStackHandler(NUM_REWARD_SLOTS);

        resultStatus = DataSlot.standalone();
        resultStatus.set(successFlag);
        addDataSlot(resultStatus);

        for (int i = 0; i < rewards.getSlots(); i++) {
            this.addSlot(new SlotItemHandler(rewards, i, 14 + 18 * i, 123));
        }

        addStandardInventorySlots(playerInventory, 68, 154);

        for (ResourceLocation stat : STATS_SLOTS) {
            DataSlot statSlot = DataSlot.standalone();
            statSlot.set(riftStats.getInt(stat));
            addDataSlot(statSlot);
            stats.add(statSlot);
        }
    }

    public int getStat(int index) {
        return stats.get(index).get();
    }

    public int getResult() {
        return resultStatus.get();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return MOVER.quickMove(this, player, index);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        this.access.execute((world, pos) -> {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            ItemStackHandlerUtil.placeInPlayerInventoryOrDrop(serverPlayer, rewards);
        });
    }

    public void addReward(ItemStack item, ServerPlayer player) {
        access.execute((level, pos) -> {
            ItemStackHandlerUtil.addOrGiveToPlayerOrDrop(item, rewards, player);
        });
    }
}
