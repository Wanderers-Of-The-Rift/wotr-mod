package com.wanderersoftherift.wotr.gui.menu;

import com.wanderersoftherift.wotr.gui.menu.slot.RunegemSlot;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import com.wanderersoftherift.wotr.mixin.InvokerAbstractContainerMenu;
import com.wanderersoftherift.wotr.network.C2SRuneAnvilApplyPacket;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class RuneAnvilMenu extends AbstractContainerMenu {
    public static final List<Vector2i> RUNE_SLOT_POSITIONS = List.of( // CLOCKWISE FROM TOP CENTER
            new Vector2i(80, 26), new Vector2i(127, 51), new Vector2i(127, 101), new Vector2i(80, 126),
            new Vector2i(33, 101), new Vector2i(33, 51));
    private static final Vector2i GEAR_SLOT_POSITION = new Vector2i(80, 76);
    private final List<Slot> playerInventorySlots = new ArrayList<>();
    private final List<RunegemSlot> socketSlots = new ArrayList<>();
    private final Inventory playerInventory;
    private final ContainerLevelAccess access;
    private final boolean isServer;
    private final Container container;
    private Slot gearSlot;
    private int activeSocketSlots = 0;
    private final QuickMover mover;

    // Client
    public RuneAnvilMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL, false, new RuneAnvilSimpleClientContainer());
    }

    // Server
    public RuneAnvilMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, boolean isServer,
            Container container) {
        super(WotrMenuTypes.RUNE_ANVIL_MENU.get(), containerId);
        this.playerInventory = playerInventory;
        this.access = access;
        this.isServer = isServer;
        this.container = container;

        this.createSlots();

        mover = QuickMover.create(this)
                .forPlayerSlots(0)
                .tryMoveTo(QuickMover.PLAYER_SLOTS, 7)
                .forSlots(QuickMover.PLAYER_SLOTS, 7)
                .tryMoveToPlayer()
                .build();
    }

    private void createSlots() {
        this.createInventorySlots(this.playerInventory);
        this.createGearSlot();
        this.createSocketSlots();

        GearSockets gearSockets = this.gearSlot.getItem().get(WotrDataComponentType.GEAR_SOCKETS.get());
        if (gearSockets != null) {
            this.activeSocketSlots = gearSockets.sockets().size();
        }
    }

    private void createInventorySlots(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.playerInventorySlots
                        .add(this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 166 + i * 18)));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.playerInventorySlots.add(this.addSlot(new Slot(inventory, k, 8 + k * 18, 224)));
        }
    }

    private void createGearSlot() {
        this.gearSlot = this.addSlot(new Slot(this.container, 0, GEAR_SLOT_POSITION.x, GEAR_SLOT_POSITION.y) {
            @Override
            public void setChanged() {
                super.setChanged();
                RuneAnvilMenu.this.gearSlotChanged();
            }

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return container.canPlaceItem(0, stack);
            }

            @Override
            public boolean mayPickup(@NotNull Player player) {
                return container.canTakeItem(RuneAnvilMenu.this.playerInventory, 0, this.getItem());
            }
        });
    }

    private void createSocketSlots() {
        for (int i = 0; i < 6; i++) {
            Vector2i position = RUNE_SLOT_POSITIONS.get(i);
            int finalI = i;
            socketSlots.add(
                    (RunegemSlot) this.addSlot(new RunegemSlot(this.container, finalI + 1, position.x, position.y) {
                        @Override
                        public boolean mayPlace(@NotNull ItemStack stack) {
                            return container.canPlaceItem(finalI + 1, stack);
                        }

                        @Override
                        public boolean mayPickup(@NotNull Player player) {
                            return container.canTakeItem(RuneAnvilMenu.this.playerInventory, finalI + 1,
                                    this.getItem());
                        }
                    }));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, WotrBlocks.RUNE_ANVIL_ENTITY_BLOCK.get());
    }

    public void apply() {
        if (!this.isServer) {
            PacketDistributor.sendToServer(new C2SRuneAnvilApplyPacket(this.containerId));
        }

        ItemStack gear = this.gearSlot.getItem();
        if (gear.isEmpty()) {
            return;
        }

        GearSockets currentSockets = gear.get(WotrDataComponentType.GEAR_SOCKETS.get());
        if (currentSockets == null) {
            return;
        }

        List<GearSocket> newSockets = new ArrayList<>(currentSockets.sockets());
        Level level = this.playerInventory.player.level();
        for (int i = 0; i < this.activeSocketSlots; i++) {
            RunegemSlot slot = this.socketSlots.get(i);
            GearSocket currentSocket = currentSockets.sockets().get(i);
            ItemStack runegem = slot.getItem();
            RunegemData runegemData = runegem.get(WotrDataComponentType.RUNEGEM_DATA);
            List<GearSocket> existingSockets = new ArrayList<>(newSockets);
            existingSockets.remove(i);
            if (!runegem.isEmpty() && runegemData != null
                    && currentSocket.canBeApplied(gear, level, runegemData, existingSockets)) {
                GearSocket newSocket = currentSocket.applyRunegem(gear, runegem, level, existingSockets);
                newSockets.set(i, newSocket);
                slot.set(ItemStack.EMPTY);
                slot.setLockedSocket(newSocket);
                slot.setSocket(null);
            }
        }
        GearSockets newGearSockets = new GearSockets(newSockets);
        gear.set(WotrDataComponentType.GEAR_SOCKETS.get(), newGearSockets);
    }

    private void gearSlotChanged() {
        this.returnRunegems(this.playerInventory.player);

        ItemStack gear = this.gearSlot.getItem();
        if (gear.isEmpty()) {
            this.activeSocketSlots = 0;
            for (RunegemSlot socketSlot : this.socketSlots) {
                socketSlot.set(ItemStack.EMPTY);
                socketSlot.setSocket(null);
                socketSlot.setLockedSocket(null);
                socketSlot.setShape(null);
            }
        } else {
            GearSockets sockets = gear.get(WotrDataComponentType.GEAR_SOCKETS.get());
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
                    continue;
                }

                GearSocket socket = socketList.get(i);
                slot.set(ItemStack.EMPTY);
                slot.setSocket(null);
                slot.setLockedSocket(socket.isEmpty() ? null : socket);
                slot.setShape(socket.shape());
            }
        }
    }

    public void returnRunegems(@Nullable Player player) {
        if (player == null) {
            player = this.playerInventory.player;
        }

        for (RunegemSlot socketSlot : this.socketSlots) {
            ItemStack stack = socketSlot.getItem();

            if (stack.isEmpty() || !socketSlot.mayPickup(player)) {
                continue;
            }

            InvokerAbstractContainerMenu.dropOrPlaceInInventory(player, stack);

            socketSlot.set(ItemStack.EMPTY);
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return mover.quickMove(player, index);
    }

    public @NotNull ItemStack getGearSlotItem() {
        return this.gearSlot.getItem().copy();
    }
}
