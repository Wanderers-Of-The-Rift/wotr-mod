package com.wanderersoftherift.wotr.gui.menu;

import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RuneAnvilContainer extends Container {
    int SIZE = 7;

    @Override
    default int getContainerSize() {
        return SIZE;
    }

    @Override
    default int getMaxStackSize() {
        return 1;
    }

    @Override
    default boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if (slot == 0) {
            return stack.has(ModDataComponentType.GEAR_SOCKETS);
        } else {
            int runegemSlotIndex = slot - 1;

            if (!stack.is(ModItems.RUNEGEM)) return false;

            ItemStack gear = this.getItem(0);
            GearSockets gearSockets = gear.get(ModDataComponentType.GEAR_SOCKETS.get());
            RunegemData runegemData = stack.get(ModDataComponentType.RUNEGEM_DATA.get());
            if (gear.isEmpty() || stack.isEmpty() || gearSockets == null || runegemData == null) return false;

            List<GearSocket> sockets = gearSockets.sockets();
            if (sockets.size() <= runegemSlotIndex) return false;

            GearSocket socket = sockets.get(runegemSlotIndex);
            return socket.canBeApplied(runegemData);
        }
    }

    @Override
    default boolean canTakeItem(@NotNull Container target, int slot, @NotNull ItemStack stack) {
        if (slot == 0) {
            return true;
        } else {
            int runegemSlotIndex = slot - 1;

            ItemStack gear = this.getItem(0);
            GearSockets gearSockets = gear.get(ModDataComponentType.GEAR_SOCKETS.get());
            RunegemData runegemData = stack.get(ModDataComponentType.RUNEGEM_DATA.get());
            if (gear.isEmpty() || stack.isEmpty() || gearSockets == null || runegemData == null) return true; // i'm not
                                                                                                              // sure
                                                                                                              // this is
                                                                                                              // right

            List<GearSocket> sockets = gearSockets.sockets();
            if (sockets.size() <= runegemSlotIndex) return false;

            GearSocket socket = sockets.get(runegemSlotIndex);
            RunegemData appliedRunegem = socket.runegem().orElse(null);
            if (appliedRunegem == null) return true;
            return !appliedRunegem.equals(runegemData);
        }
    }
}
