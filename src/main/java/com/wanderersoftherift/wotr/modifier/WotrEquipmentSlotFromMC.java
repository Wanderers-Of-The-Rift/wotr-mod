package com.wanderersoftherift.wotr.modifier;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public record WotrEquipmentSlotFromMC(EquipmentSlot minecraftSlot, TagKey<Item> tag) implements WotrEquipmentSlot {

    public static final ArrayList<WotrEquipmentSlotFromMC> SLOTS;

    public static final @NotNull Map<EquipmentSlot, WotrEquipmentSlotFromMC> SLOT_MAP;

    @Override
    public boolean canAccept(ItemStack stack) {
        var equippable = stack.getComponents().get(DataComponents.EQUIPPABLE);
        if (equippable != null) {
            return equippable.slot() == minecraftSlot;
        }
        return stack.is(tag);
    }

    @Override
    public ItemStack getContent(IAttachmentHolder entity) {
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity.getItemBySlot(minecraftSlot);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public String getSerializedName() {
        return minecraftSlot.getSerializedName();
    }

    static {
        var slots = new ArrayList<WotrEquipmentSlotFromMC>();
        slots.add(new WotrEquipmentSlotFromMC(EquipmentSlot.MAINHAND,
                TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_main_hand_slot"))));
        slots.add(new WotrEquipmentSlotFromMC(EquipmentSlot.OFFHAND,
                TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_off_hand_slot"))));
        slots.add(new WotrEquipmentSlotFromMC(EquipmentSlot.FEET,
                TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_boots_slot"))));
        slots.add(new WotrEquipmentSlotFromMC(EquipmentSlot.LEGS,
                TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_leggings_slot"))));
        slots.add(new WotrEquipmentSlotFromMC(EquipmentSlot.BODY,
                TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_chestplate_slot"))));
        slots.add(new WotrEquipmentSlotFromMC(EquipmentSlot.CHEST,
                TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_chestplate_slot"))));
        slots.add(new WotrEquipmentSlotFromMC(EquipmentSlot.HEAD,
                TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_helmet_slot"))));
        SLOTS = slots;
        SLOT_MAP = new EnumMap<>(
                Map.ofEntries(slots.stream().map(it -> Map.entry(it.minecraftSlot, it)).toArray(Map.Entry[]::new)));
    }

    @EventBusSubscriber(modid = WanderersOfTheRift.MODID)
    public static class Register {

        @SubscribeEvent
        private static void provideMinecraftSlots(ModifierHelper.IterateEquipmentSlotsEvent event) {
            event.getSlots().addAll(SLOTS);
        }
    }
}
