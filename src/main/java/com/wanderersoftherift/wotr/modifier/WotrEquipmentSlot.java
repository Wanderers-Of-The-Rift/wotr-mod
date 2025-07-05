package com.wanderersoftherift.wotr.modifier;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public interface WotrEquipmentSlot extends StringRepresentable {
    boolean canAccept(ItemStack stack);

    ItemStack getContent(IAttachmentHolder entity);
}
