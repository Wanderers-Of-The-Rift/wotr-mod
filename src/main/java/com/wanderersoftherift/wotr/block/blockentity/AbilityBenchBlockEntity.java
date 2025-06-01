package com.wanderersoftherift.wotr.block.blockentity;

import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.gui.menu.AbilityBenchMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.item.handler.ChangeAwareItemHandler;
import com.wanderersoftherift.wotr.item.handler.LargeCountItemHandler;
import com.wanderersoftherift.wotr.network.AbilitySlotsUpdatePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

/**
 * Entity for the ability bench block, to store skill thread
 */
public class AbilityBenchBlockEntity extends BlockEntity {

    public static final int THREAD_STORAGE = 256;
    private final LargeCountItemHandler threadStorage = new LargeCountItemHandler(WotrItems.SKILL_THREAD.toStack(1),
            THREAD_STORAGE);

    public AbilityBenchBlockEntity(BlockPos pos, BlockState blockState) {
        super(WotrBlockEntities.ABILITY_BENCH_BLOCK_ENTITY.get(), pos, blockState);
        threadStorage.registerChangeListener(this::setChanged);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        threadStorage.setCount(tag.getInt("thread"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("thread", threadStorage.getCount());
    }

    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        AbilitySlots slots = playerInventory.player.getData(WotrAttachments.ABILITY_SLOTS);
        IItemHandler replicatedSlots = new ChangeAwareItemHandler(slots) {
            @Override
            public void onSlotChanged(int slot) {
                PacketDistributor.sendToPlayer((ServerPlayer) player,
                        new AbilitySlotsUpdatePayload(slot, slots.getStackInSlot(slot)));
            }
        };
        return new AbilityBenchMenu(containerId, playerInventory, threadStorage,
                ContainerLevelAccess.create(level, getBlockPos()), replicatedSlots);
    }
}
