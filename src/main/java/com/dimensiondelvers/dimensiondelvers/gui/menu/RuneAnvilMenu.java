package com.dimensiondelvers.dimensiondelvers.gui.menu;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.init.ModBlocks;
import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import com.dimensiondelvers.dimensiondelvers.init.ModItems;
import com.dimensiondelvers.dimensiondelvers.init.ModMenuTypes;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSocket;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSockets;
import com.dimensiondelvers.dimensiondelvers.network.C2SRuneAnvilApplyPacket;
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
    private static final Vector2i GEAR_SLOT_POSITION = new Vector2i(80, 76);
    private final List<Slot> playerInventorySlots = new ArrayList<>();
    private final List<RunegemSlot> socketSlots = new ArrayList<>();
    private final Inventory playerInventory;
    private final ContainerLevelAccess access;
    private final boolean isServer;
    private final Container container;
    private Slot gearSlot;
    private int activeSocketSlots = 0;

    // Client
    public RuneAnvilMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL, false, new SimpleContainer(7));
    }

    // Server
    public RuneAnvilMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, boolean isServer, Container container) {
        super(ModMenuTypes.RUNE_ANVIL_MENU.get(), containerId);
        this.playerInventory = playerInventory;
        this.access = access;
        this.isServer = isServer;
        this.container = container;

        this.createSlots();
    }

    private void createSlots() {
        this.createInventorySlots(this.playerInventory);
        this.createGearSlot();
        this.createSocketSlots();
    }

    private void createInventorySlots(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.playerInventorySlots.add(this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 166 + i * 18)));
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
                return stack.has(ModDataComponentType.GEAR_SOCKETS);
            }
        });
    }

    private void createSocketSlots() {
        for (int i = 0; i < 6; i++) {
            Vector2i position = RUNE_SLOT_POSITIONS.get(i);
            int finalI = i;
            socketSlots.add((RunegemSlot) this.addSlot(new RunegemSlot(this.container, finalI + 1, position.x, position.y) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    if (!stack.is(ModItems.RUNEGEM)) return false;

                    ItemStack gear = RuneAnvilMenu.this.gearSlot.getItem();
                    GearSockets gearSockets = gear.get(ModDataComponentType.GEAR_SOCKETS.get());
                    RunegemData runegemData = stack.get(ModDataComponentType.RUNEGEM_DATA.get());
                    if (gear.isEmpty() || stack.isEmpty() || gearSockets == null || runegemData == null) return false;

                    List<GearSocket> sockets = gearSockets.sockets();
                    if (sockets.size() <= finalI) return false;

                    GearSocket socket = sockets.get(finalI);
                    return socket.canBeApplied(runegemData);
                }

                @Override
                public boolean mayPickup(@NotNull Player player) {
                    ItemStack gear = RuneAnvilMenu.this.gearSlot.getItem();
                    ItemStack stack = this.getItem();
                    GearSockets gearSockets = gear.get(ModDataComponentType.GEAR_SOCKETS.get());
                    RunegemData runegemData = stack.get(ModDataComponentType.RUNEGEM_DATA.get());
                    if (gear.isEmpty() || stack.isEmpty() || gearSockets == null || runegemData == null)
                        return true; // i'm not sure this is right

                    List<GearSocket> sockets = gearSockets.sockets();
                    if (sockets.size() <= finalI) return false;

                    GearSocket socket = sockets.get(finalI);
                    RunegemData appliedRunegem = socket.runegem().orElse(null);
                    if (appliedRunegem == null) return true;
                    return !appliedRunegem.equals(runegemData);
                }
            }));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, ModBlocks.RUNE_ANVIL_ENTITY_BLOCK.get());
    }

    public void apply() {
        if (!this.isServer) {
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
            if (slot.mayPickup(this.playerInventory.player) && !runegem.isEmpty()) {
                GearSocket newGearSocket = currentSocket.applyRunegem(runegem, this.playerInventory.player.level());
                newSockets.add(newGearSocket);
            } else {
                newSockets.add(currentSocket);
            }
        }
        GearSockets newGearSockets = new GearSockets(newSockets);
        gear.set(ModDataComponentType.GEAR_SOCKETS.get(), newGearSockets);
    }

    private void gearSlotChanged() {
        returnRunegems(null);

        ItemStack gear = this.gearSlot.getItem();
        if (gear.isEmpty()) {
            this.activeSocketSlots = 0;
            for (RunegemSlot socketSlot : this.socketSlots) {
                socketSlot.set(ItemStack.EMPTY);
                socketSlot.setSocket(null);
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
                    continue;
                }

                GearSocket socket = socketList.get(i);
                slot.setSocket(socket);

                Optional<RunegemData> optionalRunegem = socket.runegem();
                if (optionalRunegem.isPresent()) {
                    ItemStack tempStack = ModItems.RUNEGEM.toStack();
                    tempStack.set(ModDataComponentType.RUNEGEM_DATA.get(), optionalRunegem.get());
                    slot.set(tempStack);
                } else {
                    slot.set(ItemStack.EMPTY);
                }
            }
        }
    }

    private void socketSlotChanged(int i) {

    }

    public void returnRunegems(@Nullable Player player) {
        if (player == null) player = this.playerInventory.player;

        for (RunegemSlot socketSlot : this.socketSlots) {
            if (!socketSlot.mayPickup(player) || !socketSlot.hasItem()) {
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
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        if (index >= this.slots.size()) {
            // this should never happen
            DimensionDelvers.LOGGER.error("RuneAnvilMenu: quickMoveStack: index out of bounds");
            return ItemStack.EMPTY;
        }

        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            // trying to move an empty slot?
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();

        if (this.playerInventorySlots.contains(slot)) {
            // Inventory -> Container
            if (stack.has(ModDataComponentType.GEAR_SOCKETS)) {
                // Gear
                if (this.gearSlot.hasItem() || !this.gearSlot.mayPlace(stack)) {
                    return ItemStack.EMPTY;
                }

                int count = stack.getCount();
                this.gearSlot.set(stack.copyWithCount(1));
                slot.set(stack.copyWithCount(count - 1));
                return ItemStack.EMPTY;
            }
            if (stack.has(ModDataComponentType.RUNEGEM_DATA)) {
                // Runegems
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
            // Container -> Inventory
            Inventory inventory = player.getInventory();
            if (inventory.add(stack)) {
                slot.set(ItemStack.EMPTY);
                return ItemStack.EMPTY;
            }
            return ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }
}
