package com.wanderersoftherift.wotr.modifier;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.implicit.GearImplicits;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import com.wanderersoftherift.wotr.modifier.source.GearImplicitModifierSource;
import com.wanderersoftherift.wotr.modifier.source.GearSocketModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ModifierHelper {

    public static void runIterationOnItem(
            ItemStack stack,
            EquipmentSlot slot,
            LivingEntity entity,
            ModifierInSlotVisitor visitor) {
        if (!stack.isEmpty() && itemWorksInSlot(stack, slot)) {
            runOnImplicits(stack, slot, entity, visitor);
            runOnGearSockets(stack, slot, entity, visitor);
        }
    }

    private static boolean itemWorksInSlot(ItemStack item, EquipmentSlot slot) {
        var equippable = item.getComponents().get(DataComponents.EQUIPPABLE);
        if (equippable != null) {
            return equippable.slot() == slot;
        }
        var tag = switch (slot) {
            case MAINHAND -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_main_hand_slot"));
            case OFFHAND -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_off_hand_slot"));
            case FEET -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_boots_slot"));
            case LEGS -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_leggings_slot"));
            case CHEST, BODY -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_chestplate_slot"));
            case HEAD -> TagKey.create(Registries.ITEM, WanderersOfTheRift.id("socketable_helmet_slot"));
        };
        if (item.is(tag)) {
            return true;
        }
        return false;
    }

    private static void runOnGearSockets(
            ItemStack stack,
            EquipmentSlot slot,
            LivingEntity entity,
            ModifierInSlotVisitor visitor) {
        GearSockets gearSockets = stack.get(WotrDataComponentType.GEAR_SOCKETS);
        if (gearSockets != null && !gearSockets.isEmpty()) {
            for (GearSocket socket : gearSockets.sockets()) {
                if (socket.isEmpty()) {
                    continue;
                }
                ModifierInstance modifierInstance = socket.modifier().get();
                Holder<Modifier> modifier = modifierInstance.modifier();
                if (modifier != null) {
                    ModifierSource source = new GearSocketModifierSource(socket, gearSockets, slot, entity);
                    visitor.accept(modifier, modifierInstance.tier(), modifierInstance.roll(), source);
                }
            }
        }
    }

    private static void runOnImplicits(
            ItemStack stack,
            EquipmentSlot slot,
            LivingEntity entity,
            ModifierInSlotVisitor visitor) {
        GearImplicits implicits = stack.get(WotrDataComponentType.GEAR_IMPLICITS);
        if (implicits != null) {
            List<ModifierInstance> modifierInstances = implicits.modifierInstances(stack, entity.level());
            for (ModifierInstance modifier : modifierInstances) {
                ModifierSource source = new GearImplicitModifierSource(implicits, slot, entity);
                visitor.accept(modifier.modifier(), modifier.tier(), modifier.roll(), source);
            }
        }
    }

    public static void runIterationOnEquipment(LivingEntity entity, ModifierInSlotVisitor visitor) {
        for (EquipmentSlot equipmentslot : EquipmentSlot.VALUES) {
            runIterationOnItem(entity.getItemBySlot(equipmentslot), equipmentslot, entity, visitor);
        }
    }

    public static void enableModifier(LivingEntity entity) {
        runIterationOnEquipment(entity, (modifierHolder, tier, roll, source) -> modifierHolder.value()
                .enableModifier(roll, entity, source, tier));
    }

    public static void enableModifier(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
        runIterationOnItem(stack, slot, entity, (modifierHolder, tier, roll, source) -> modifierHolder.value()
                .enableModifier(roll, entity, source, tier));
    }

    public static void disableModifier(LivingEntity entity) {
        runIterationOnEquipment(entity, (modifierHolder, tier, roll, source) -> modifierHolder.value()
                .disableModifier(roll, entity, source, tier));
    }

    public static void disableModifier(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
        runIterationOnItem(stack, slot, entity, (modifierHolder, tier, roll, source) -> modifierHolder.value()
                .disableModifier(roll, entity, source, tier));
    }

    @FunctionalInterface
    public interface ModifierInSlotVisitor {
        void accept(Holder<Modifier> modifierHolder, int tier, float roll, ModifierSource item);
    }
}
