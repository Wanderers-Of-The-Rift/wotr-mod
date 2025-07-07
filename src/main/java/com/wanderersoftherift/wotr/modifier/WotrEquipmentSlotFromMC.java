package com.wanderersoftherift.wotr.modifier;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.List;

public record WotrEquipmentSlotFromMC(EquipmentSlot minecraftSlot, TagKey<Item> tag) implements WotrEquipmentSlot {

    // has same order as net.minecraft.world.entity.EquipmentSlot enum
    public static final List<WotrEquipmentSlotFromMC> SLOTS;

    public static WotrEquipmentSlot fromVanillaSlot(EquipmentSlot vanillaSlot) {
        return SLOTS.get(vanillaSlot.ordinal());
    }

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
        SLOTS = ImmutableList.of(
                new WotrEquipmentSlotFromMC(EquipmentSlot.MAINHAND,
                        TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_main_hand_slot"))),
                new WotrEquipmentSlotFromMC(EquipmentSlot.OFFHAND,
                        TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_off_hand_slot"))),
                new WotrEquipmentSlotFromMC(EquipmentSlot.FEET,
                        TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_boots_slot"))),
                new WotrEquipmentSlotFromMC(EquipmentSlot.LEGS,
                        TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_leggings_slot"))),
                new WotrEquipmentSlotFromMC(EquipmentSlot.CHEST,
                        TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_chestplate_slot"))),
                new WotrEquipmentSlotFromMC(EquipmentSlot.HEAD,
                        TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_helmet_slot"))),
                // this is apparently slot for horse armor
                new WotrEquipmentSlotFromMC(EquipmentSlot.BODY,
                        TagKey.create(Registries.ITEM, ResourceLocation.parse("minecraft:empty"))));
    }

    @EventBusSubscriber(modid = WanderersOfTheRift.MODID)
    public static class Register {

        @SubscribeEvent
        private static void collectMinecraftSlots(ModifierHelper.CollectEquipmentSlotsEvent event) {
            if (event.getEntity() instanceof LivingEntity) {
                if (event.getEntity() instanceof Animal) {
                    SLOTS.stream()
                            .filter(slot -> slot.minecraftSlot.getType() == EquipmentSlot.Type.ANIMAL_ARMOR)
                            .forEach(event.getSlots()::add);
                } else {
                    SLOTS.stream()
                            .filter(slot -> slot.minecraftSlot.getType() != EquipmentSlot.Type.ANIMAL_ARMOR)
                            .forEach(event.getSlots()::add);
                }
            }
        }
    }
}
