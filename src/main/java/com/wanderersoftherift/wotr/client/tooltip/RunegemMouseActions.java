package com.wanderersoftherift.wotr.client.tooltip;

import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.List;

/**
 * Responsible for handling paginated tooltips via mouse-scrolling for the Runegem tooltips.
 */
public class RunegemMouseActions implements ItemSlotMouseAction {
    private static RunegemMouseActions instance;
    private final ScrollWheelHandler scrollWheelHandler;
    private int currentIndex = 0;
    private int maxIndex = -1;


    private RunegemMouseActions() {
        this.scrollWheelHandler = new ScrollWheelHandler();
    }

    public static RunegemMouseActions getInstance() {
        if (instance == null) {
            instance = new RunegemMouseActions();
        }
        return instance;
    }

    @Override
    public boolean matches(Slot slot) {
        return slot.getItem().is(WotrItems.RUNEGEM) && slot.getItem().has(WotrDataComponentType.RUNEGEM_DATA);
    }

    @Override
    public boolean onMouseScrolled(double xOffset, double yOffset, int index, @NotNull ItemStack itemStack) {
        List<RunegemData.ModifierGroup> groups = getModifierGroups(itemStack);

        if (groups == null || groups.isEmpty()) {
            return false;
        }

        maxIndex = groups.size();
        Vector2i scroll = this.scrollWheelHandler.onMouseScroll(xOffset, yOffset);
        int direction;
        if (scroll.y == 0) {
            direction = -scroll.x;
        } else {
            direction = scroll.y;
        }

        if (direction == 0) {
            return false;
        }

        int nextIndex = ScrollWheelHandler.getNextScrollWheelSelection(direction, currentIndex, maxIndex);
        if (nextIndex != currentIndex) {
            currentIndex = nextIndex;
            return true;
        }

        return false;
    }

    @Override
    public void onStopHovering(@NotNull Slot slot) {
        currentIndex = 0;
        maxIndex = -1;
    }

    @Override
    public void onSlotClicked(@NotNull Slot slot, @NotNull ClickType clickType) {
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setMaxIndex(int maxIndex) {
        this.maxIndex = maxIndex;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    private static List<RunegemData.ModifierGroup> getModifierGroups(ItemStack stack) {
        if (stack == null || !stack.has(WotrDataComponentType.RUNEGEM_DATA)) {
            return null;
        }

        RunegemData gemData = stack.get(WotrDataComponentType.RUNEGEM_DATA);
        if (gemData != null) {
            return gemData.modifierLists();
        } else {
            return null;
        }
    }
}
