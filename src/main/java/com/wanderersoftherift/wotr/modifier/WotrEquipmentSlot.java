package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.function.Function;

public interface WotrEquipmentSlot extends StringRepresentable {
    Codec<WotrEquipmentSlot> DIRECT_CODEC = WotrRegistries.EQUIPMENT_SLOTS.byNameCodec()
            .dispatch(WotrEquipmentSlot::codec, Function.identity());

    MapCodec<? extends WotrEquipmentSlot> codec();

    boolean canAccept(ItemStack stack);

    ItemStack getContent(IAttachmentHolder entity);
}
