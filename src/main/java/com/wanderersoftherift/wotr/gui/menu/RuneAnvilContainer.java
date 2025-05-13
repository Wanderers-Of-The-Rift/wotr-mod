package com.wanderersoftherift.wotr.gui.menu;

import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
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
            return stack.has(WotrDataComponentType.GEAR_SOCKETS);
        } else {
            int runegemSlotIndex = slot - 1;

            if (!stack.is(WotrItems.RUNEGEM)) {
                return false;
            }

            ItemStack gear = this.getItem(0);
            GearSockets gearSockets = gear.get(WotrDataComponentType.GEAR_SOCKETS.get());
            RunegemData runegemData = stack.get(WotrDataComponentType.RUNEGEM_DATA.get());
            if (gear.isEmpty() || stack.isEmpty() || gearSockets == null || runegemData == null) {
                return false;
            }

            List<GearSocket> sockets = gearSockets.sockets();
            if (sockets.size() <= runegemSlotIndex) {
                return false;
            }

            GearSocket socket = sockets.get(runegemSlotIndex);
//            return socket.canBeApplied(runegemData);
            return socket.shape().equals(runegemData.shape());
        }
    }
}
