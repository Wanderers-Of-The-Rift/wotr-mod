package com.wanderersoftherift.wotr.modifier;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.EnumMap;
import java.util.Map;

public record WotrEquipmentSlotFromMC(EquipmentSlot slot) implements WotrEquipmentSlot {

    private static final EnumMap<EquipmentSlot, TagKey<Item>> SLOTS = new EnumMap<>(Map.ofEntries(
            Map.entry(EquipmentSlot.MAINHAND,
                    TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_main_hand_slot"))),
            Map.entry(EquipmentSlot.OFFHAND,
                    TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_off_hand_slot"))),
            Map.entry(EquipmentSlot.FEET,
                    TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_boots_slot"))),
            Map.entry(EquipmentSlot.LEGS,
                    TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_leggings_slot"))),
            Map.entry(EquipmentSlot.BODY,
                    TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_chestplate_slot"))),
            Map.entry(EquipmentSlot.CHEST,
                    TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_chestplate_slot"))),
            Map.entry(EquipmentSlot.HEAD,
                    TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_helmet_slot")))
    ));

    @Override
    public boolean canAccept(ItemStack stack) {
        var equippable = stack.getComponents().get(DataComponents.EQUIPPABLE);
        if (equippable != null) {
            return equippable.slot() == slot;
        }
        var tag = SLOTS.get(slot);
        return stack.is(tag);
    }

    @Override
    public ItemStack getContent(IAttachmentHolder entity) {
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity.getItemBySlot(slot);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public String getSerializedName() {
        return slot.getSerializedName();
    }
}
