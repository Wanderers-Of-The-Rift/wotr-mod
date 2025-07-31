package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.modifier.ModifierHelper;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Inventory attachment holding equipped ability items. Also tracks the selected ability slot, which is the last used,
 * or else manually selected.
 */
public class AbilitySlots implements IItemHandlerModifiable {

    public static final int ABILITY_BAR_SIZE = 9;

    private static final AttachmentSerializerFromDataCodec<Data, AbilitySlots> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            Data.CODEC, AbilitySlots::new, AbilitySlots::data);

    private final IAttachmentHolder holder;
    private final NonNullList<ItemStack> abilities = NonNullList.withSize(ABILITY_BAR_SIZE, ItemStack.EMPTY);
    private int selected = 0;

    public AbilitySlots(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private AbilitySlots(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        if (data != null) {
            var abilities = data.abilities();
            for (int i = 0; i < abilities.size() && i < this.abilities.size(); i++) {
                this.abilities.set(i, abilities.get(i));
            }
            this.selected = data.selected();
        }
    }

    public static IAttachmentSerializer<Tag, AbilitySlots> getSerializer() {
        return SERIALIZER;
    }

    private Data data() {
        return new Data(abilities, selected);
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
        if (!stack.isEmpty() && stack.has(WotrDataComponentType.ABILITY)) {
            return stack.get(WotrDataComponentType.ABILITY).value();
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
        if (!isItemValid(slot, stack)) {
            return stack;
        }
        var oldStack = abilities.get(slot);
        if (oldStack.isEmpty()) {
            if (!simulate) {
                var newStack = stack.copy().split(1);
                abilities.set(slot, newStack);
                onSlotChanged(slot, oldStack, newStack);
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
        if (amount == 0 || abilities.get(slot).isEmpty()) {
            return ItemStack.EMPTY;
        }
        var original = abilities.get(slot).copy();
        var extracted = Integer.min(amount, original.getMaxStackSize());
        if (!simulate) {
            var newCount = original.getCount() - extracted;
            if (newCount > 0) {
                var newStack = abilities.get(slot);
                newStack.setCount(newCount);
                onSlotChanged(slot, original, newStack);
            } else {
                abilities.set(slot, ItemStack.EMPTY);
                onSlotChanged(slot, original, ItemStack.EMPTY);
            }
        }
        original.setCount(extracted);
        return original;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1; // Item.ABSOLUTE_MAX_STACK_SIZE if we wanted consumable abilities
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.is(WotrTags.Items.ABILITY_SLOT_ACCEPTED);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        var original = abilities.get(slot);
        onSlotChanged(slot, original, stack);
        abilities.set(slot, stack);
    }

    private void onSlotChanged(int slot, ItemStack original, ItemStack newStack) {
        if (holder instanceof LivingEntity livingEntity) {
            ModifierHelper.disableModifier(original, livingEntity, new AbilityEquipmentSlot(slot));
            ModifierHelper.enableModifier(newStack, livingEntity, new AbilityEquipmentSlot(slot));
        }
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

    private record Data(NonNullList<ItemStack> abilities, int selected) {
        public static final Codec<AbilitySlots.Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                NonNullList.codecOf(ItemStack.OPTIONAL_CODEC).fieldOf("abilities").forGetter(x -> x.abilities),
                Codec.INT.fieldOf("selected").forGetter(x -> x.selected)
        ).apply(instance, AbilitySlots.Data::new));
    }

}
