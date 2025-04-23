package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Inventory attachment holding equipped ability items. Also tracks the selected ability slot, which is the last used,
 * or else manually selected.
 */
public class AbilitySlots implements IItemHandlerModifiable {

    public static final int ABILITY_BAR_SIZE = 9;
    public static final Codec<AbilitySlots> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(NonNullList.codecOf(ItemStack.OPTIONAL_CODEC).fieldOf("abilities").forGetter(x -> x.abilities),
                    Codec.INT.fieldOf("selected").forGetter(x -> x.selected))
            .apply(instance, AbilitySlots::new));

    private final NonNullList<ItemStack> abilities = NonNullList.withSize(ABILITY_BAR_SIZE, ItemStack.EMPTY);
    private int selected = 0;

    public AbilitySlots() {

    }

    public AbilitySlots(NonNullList<ItemStack> abilities, int selected) {
        for (int i = 0; i < abilities.size() && i < this.abilities.size(); i++) {
            this.abilities.set(i, abilities.get(i));
        }
        this.selected = selected;
    }

    /**
     * @return The currently selected slot
     */
    public int getSelectedSlot() {
        return selected;
    }

    /**
     * @param slot The slot to be selected
     */
    public void setSelectedSlot(int slot) {
        if (slot >= 0 && slot < ABILITY_BAR_SIZE) {
            selected = slot;
        }
    }

    /**
     * Decrements the currently selected slot, wrapping around
     */
    public void decrementSelected() {
        selected = (selected + abilities.size() - 1) % abilities.size();
    }

    /**
     * Increments the currently selected slot, wrapping around
     */
    public void incrementSelected() {
        selected = (selected + 1) % abilities.size();
    }

    /**
     * @return The list of the contents of the ability slots. Empty slots will contain Item.EMPTY.
     */
    public List<ItemStack> getAbilitySlots() {
        return Collections.unmodifiableList(abilities);
    }

    /**
     * @param slot
     * @return The ability of the item in the given slot, or null.
     */
    public AbstractAbility getAbilityInSlot(int slot) {
        ItemStack stack = getStackInSlot(slot);
        if (!stack.isEmpty() && stack.has(ModDataComponentType.ABILITY)) {
            return stack.get(ModDataComponentType.ABILITY).value();
        }
        return null;
    }

    @Override
    public int getSlots() {
        return ABILITY_BAR_SIZE;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return abilities.get(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (abilities.get(slot).isEmpty()) {
            if (!simulate) {
                abilities.set(slot, stack.split(1));
                return stack;
            } else {
                ItemStack residual = stack.copy();
                residual.shrink(1);
                return residual;
            }
        }
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!abilities.get(slot).isEmpty()) {
            ItemStack result = abilities.get(slot);
            if (!simulate) {
                abilities.set(slot, ItemStack.EMPTY);
            }
            return result.copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.has(ModDataComponentType.ABILITY);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        abilities.set(slot, stack);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbilitySlots other) {
            return Objects.equals(abilities, other.abilities) && selected == other.selected;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(abilities, selected);
    }

}
