package com.wanderersoftherift.wotr.core.inventory.slot;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public interface WotrEquipmentSlot extends StringRepresentable {
    Codec<WotrEquipmentSlot> DIRECT_CODEC = WotrRegistries.EQUIPMENT_SLOTS.byNameCodec()
            .dispatch(WotrEquipmentSlot::type, DualCodec::codec);
    StreamCodec<RegistryFriendlyByteBuf, WotrEquipmentSlot> STREAM_CODEC = ByteBufCodecs
            .registry(WotrRegistries.Keys.EQUIPMENT_SLOTS)
            .dispatch(WotrEquipmentSlot::type, DualCodec::streamCodec);

    DualCodec<? extends WotrEquipmentSlot> type();

    boolean canAccept(ItemStack stack);

    ItemStack getContent(IAttachmentHolder entity);
}
