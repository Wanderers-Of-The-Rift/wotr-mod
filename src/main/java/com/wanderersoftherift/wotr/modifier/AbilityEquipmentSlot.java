package com.wanderersoftherift.wotr.modifier;

import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public record AbilityEquipmentSlot(int slot) implements WotrEquipmentSlot {
    @Override
    public boolean canAccept(ItemStack stack) {
        return stack.is(WotrItems.ABILITY_HOLDER);
    }

    @Override
    public ItemStack getContent(IAttachmentHolder entity) {
        return entity.getData(WotrAttachments.ABILITY_SLOTS.get()).getStackInSlot(slot);
    }

    @Override
    public String getSerializedName() {
        return "ability_slot_" + slot;
    }
}
