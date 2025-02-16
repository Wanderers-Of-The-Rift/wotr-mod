package com.dimensiondelvers.dimensiondelvers.gui.menu;

import com.dimensiondelvers.dimensiondelvers.init.ModBlocks;
import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import com.dimensiondelvers.dimensiondelvers.init.ModItems;
import com.dimensiondelvers.dimensiondelvers.init.ModMenuTypes;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSocket;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSockets;
import com.dimensiondelvers.dimensiondelvers.network.C2SRuneAnvilApplyPacket;
import com.dimensiondelvers.dimensiondelvers.util.ContainerTest;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RuneAnvilMenu extends AbstractContainerMenu {
    public static final List<Vector2i> RUNE_SLOT_POSITIONS = List.of( // CLOCKWISE FROM TOP CENTER
            new Vector2i(80, 26),
            new Vector2i(127, 51),
            new Vector2i(127, 101),
            new Vector2i(80, 126),
            new Vector2i(33, 101),
            new Vector2i(33, 51)
    );
    private static final int GEAR_SLOT = 36;
    private static final Vector2i GEAR_SLOT_POSITION = new Vector2i(80, 76);
    private static final List<Integer> RUNE_SLOTS = List.of(37, 38, 39, 40, 41, 42);
    private final List<RunegemSlot> socketSlots = new ArrayList<>();
    private final Level level;
    private final Inventory playerInventory;
    private final ContainerLevelAccess access;
    public int activeSocketSlots = 0;
    private boolean isClient = false;
    private Container gearSlotContainer;
    private Container socketSlotsContainer;
    private Slot gearSlot;

    // Client
    public RuneAnvilMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);

        this.isClient = true;
    }

    // Server
    public RuneAnvilMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenuTypes.RUNE_ANVIL_MENU.get(), containerId);
        this.playerInventory = playerInventory;
        this.access = access;
        this.level = playerInventory.player.level();

        // please for the love of all that is holy, do not change the order of these lines else it will fuck everything up
        this.createInventorySlots(playerInventory);
        this.createGearSlot();
        this.createSocketSlots();
    }

    private void createInventorySlots(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 166 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 224));
        }
    }

    private void createGearSlot() {
        this.gearSlotContainer = createContainer(1, 1);
        this.gearSlot = this.addSlot(new Slot(this.gearSlotContainer, 0, GEAR_SLOT_POSITION.x, GEAR_SLOT_POSITION.y) {
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.has(ModDataComponentType.GEAR_SOCKETS);
            }
        });
    }

    private void createSocketSlots() {
        this.socketSlotsContainer = createContainer(6, 1);
        for (int i = 0; i < 6; i++) {
            Vector2i position = RUNE_SLOT_POSITIONS.get(i);
            int finalI = i;
            socketSlots.add(
                    // This piece of code needs to be rewritten and all functionality needs to move into the Container.
                    // Pass the menu along and add public functions to access the necessary info.
                    (RunegemSlot) this.addSlot(new RunegemSlot(this.socketSlotsContainer, finalI, position.x, position.y) {
                        public boolean mayPlace(@NotNull ItemStack stack) {
                            if (this.isDisabled() || !stack.is(ModItems.RUNEGEM)) return false;
                            ItemStack item = gearSlotContainer.getItem(0);
                            GearSockets gearSockets = item.get(ModDataComponentType.GEAR_SOCKETS.get());
                            RunegemData runegemData = stack.get(ModDataComponentType.RUNEGEM_DATA.get());
                            if (item.isEmpty() || stack.isEmpty() || gearSockets == null || runegemData == null)
                                return false;
                            List<GearSocket> sockets = gearSockets.sockets();
                            if (sockets.size() <= finalI) return false;
                            GearSocket socket = sockets.get(finalI);
                            return socket.canBeApplied(runegemData);
                        }

                        public boolean isDisabled() {
                            return finalI >= activeSocketSlots;
                        }
                    })
            );
        }
    }

    public void slotsChanged(@NotNull Container inventory) {
        if (inventory == this.gearSlotContainer) {
            gearSlotChanged();
        } else if (inventory == this.socketSlotsContainer) {
            socketSlotChanged();
        }
    }

    private void gearSlotChanged() {
        returnRunegems(null);

        ItemStack gear = this.gearSlot.getItem();
        if (gear.isEmpty()) {
            this.activeSocketSlots = 0;
            for (RunegemSlot socketSlot : this.socketSlots) {
                socketSlot.set(ItemStack.EMPTY);
                socketSlot.setSocket(null);
                socketSlot.setMayTake(false);
            }
        } else {
            GearSockets sockets = gear.get(ModDataComponentType.GEAR_SOCKETS.get());
            if (sockets == null) {
                this.activeSocketSlots = 0;
                return;
            }

            this.activeSocketSlots = sockets.sockets().size();

            List<GearSocket> socketList = sockets.sockets();

            for (int i = 0; i < 6; i++) {
                RunegemSlot slot = this.socketSlots.get(i);
                if (i >= this.activeSocketSlots) {
                    slot.set(ItemStack.EMPTY);
                    slot.setSocket(null);
                    slot.setMayTake(false);
                    continue;
                }

                GearSocket socket = socketList.get(i);
                slot.setSocket(socket);

                Optional<ItemStack> optionalRunegem = socket.runegem();
                if (optionalRunegem.isPresent()) {
                    slot.set(optionalRunegem.get());
                    slot.setMayTake(false);
                } else {
                    slot.set(ItemStack.EMPTY);
                    slot.setMayTake(true);
                }
            }
        }
    }

    private void socketSlotChanged() {

    }

    public void apply() {
        if (this.isClient) {
            PacketDistributor.sendToServer(new C2SRuneAnvilApplyPacket(this.containerId));
            return;
        }

        ItemStack gear = this.gearSlot.getItem();
        if (gear.isEmpty()) {
            return;
        }

        GearSockets sockets = gear.get(ModDataComponentType.GEAR_SOCKETS.get());
        if (sockets == null) {
            return;
        }

        List<GearSocket> newSockets = new ArrayList<>();
        for (int i = 0; i < this.activeSocketSlots; i++) {
            RunegemSlot slot = this.socketSlots.get(i);
            GearSocket currentSocket = slot.getSocket();
            ItemStack runegem = slot.getItem();
            if (slot.getMayTake() && !runegem.isEmpty()) {
                slot.setMayTake(false);
                GearSocket newGearSocket = currentSocket.applyRunegem(runegem, this.level);
                newSockets.add(newGearSocket);
            } else {
                newSockets.add(currentSocket);
            }
        }
        GearSockets newGearSockets = new GearSockets(newSockets);
        gear.set(ModDataComponentType.GEAR_SOCKETS.get(), newGearSockets);
    }

    private Container createContainer(int size, int maxStackSize) {
        return new SimpleContainer(size) {
            public void setChanged() {
                RuneAnvilMenu.this.slotsChanged(this);
                super.setChanged();
            }

            public int getMaxStackSize() {
                return maxStackSize;
            }
        };
    }

    public void returnRunegems(@Nullable Player player) {
        if (player == null) player = this.playerInventory.player;

        for (RunegemSlot socketSlot : this.socketSlots) {
            if (!socketSlot.getMayTake() || !socketSlot.hasItem()) {
                continue;
            }

            ItemStack stack = socketSlot.getItem();

            // this is copied over and cleaned up from AbstractContainerMenu.dropOrPlaceInInventory because its private for somefuckingreason
            boolean flag = player.isRemoved() && player.getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION;
            boolean flag2 = false;
            if (player instanceof ServerPlayer serverplayer) {
                if (serverplayer.hasDisconnected()) {
                    flag2 = true;
                }
            }

            if (!flag && !flag2) {
                if (player instanceof ServerPlayer) {
                    player.getInventory().placeItemBackInInventory(stack);
                }
            } else {
                player.drop(stack, false);
            }

            socketSlot.set(ItemStack.EMPTY);
        }
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.access.execute((world, pos) -> {
            this.clearContainer(player, this.gearSlotContainer);
            returnRunegems(player);
        });
    }

    /**
     * i deeply apologize to anyone reading this code for the monstrosity below
     */
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        if (index >= this.slots.size()) return ItemStack.EMPTY; // just to be sure
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        if (index < GEAR_SLOT) {
            if (stack.has(ModDataComponentType.GEAR_SOCKETS)) {
                if (this.gearSlot.hasItem() || !this.gearSlot.mayPlace(stack)) {
                    return ItemStack.EMPTY;
                }
                int count = stack.getCount();
                this.gearSlot.set(stack.copyWithCount(1));
                slot.set(stack.copyWithCount(count - 1));
                return ItemStack.EMPTY;
            }
            if (stack.has(ModDataComponentType.RUNEGEM_DATA)) {
                for (Slot socketSlot : this.socketSlots) {
                    if (socketSlot.hasItem() || !socketSlot.mayPlace(stack)) {
                        continue;
                    }
                    int count = stack.getCount();
                    socketSlot.set(stack.copyWithCount(1));
                    slot.set(stack.copyWithCount(count - 1));
                    return ItemStack.EMPTY;
                }
            }
        } else {
            Inventory inventory = player.getInventory();
            if (inventory.add(stack)) {
                slot.set(ItemStack.EMPTY);
                return ItemStack.EMPTY;
            }
            return ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return access.evaluate((world, pos) -> isValidBlock(world.getBlockState(pos)) && player.canInteractWithBlock(pos, 4.0F), true);
    }

    protected boolean isValidBlock(BlockState state) {
        return state.is(ModBlocks.RUNE_ANVIL_BLOCK);
    }
}
