package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public record AbilityEquipmentSlot(int slot) implements WotrEquipmentSlot {

    public static final MapCodec<AbilityEquipmentSlot> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("slot").forGetter(AbilityEquipmentSlot::slot)
    ).apply(instance, AbilityEquipmentSlot::new));

    public static final List<AbilityEquipmentSlot> SLOTS = IntStream.range(0, AbilitySlots.ABILITY_BAR_SIZE)
            .mapToObj(AbilityEquipmentSlot::new)
            .toList();

    @Override
    public MapCodec<? extends WotrEquipmentSlot> codec() {
        return CODEC;
    }

    @Override
    public boolean canAccept(ItemStack stack) {
        return stack.is(WotrItems.ABILITY_HOLDER);
    }

    @Override
    public ItemStack getContent(IAttachmentHolder entity) {
        return entity.getData(WotrAttachments.ABILITY_SLOTS.get()).getStackInSlot(slot);
    }

    @Override
    public @NotNull String getSerializedName() {
        return "ability_slot_" + slot;
    }
}
