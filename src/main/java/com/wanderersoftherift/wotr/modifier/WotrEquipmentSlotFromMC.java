package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    public static final MapCodec<WotrEquipmentSlotFromMC> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    EquipmentSlot.CODEC.fieldOf("minecraft_slot").forGetter(WotrEquipmentSlotFromMC::minecraftSlot)
            ).apply(instance, WotrEquipmentSlotFromMC::fromVanillaSlot));

    // has same order as net.minecraft.world.entity.EquipmentSlot enum
    public static final List<WotrEquipmentSlotFromMC> SLOTS;

    public static WotrEquipmentSlotFromMC fromVanillaSlot(EquipmentSlot vanillaSlot) {
        return SLOTS.get(vanillaSlot.ordinal());
    }

    @Override
    public MapCodec<? extends WotrEquipmentSlot> codec() {
        return CODEC;
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
        SLOTS = EquipmentSlot.VALUES.stream().map(slot -> {
            var tag = switch (slot) {
                case MAINHAND -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_main_hand_slot"));
                case OFFHAND -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_off_hand_slot"));
                case FEET -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_boots_slot"));
                case LEGS -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_leggings_slot"));
                case CHEST -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_chestplate_slot"));
                case HEAD -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_helmet_slot"));
                default -> TagKey.create(Registries.ITEM, ResourceLocation.parse("minecraft:empty"));
            };
            return new WotrEquipmentSlotFromMC(slot, tag);
        }).toList();
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
