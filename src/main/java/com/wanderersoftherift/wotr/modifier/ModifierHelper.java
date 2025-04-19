package com.wanderersoftherift.wotr.modifier;

import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.item.implicit.GearImplicits;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import com.wanderersoftherift.wotr.modifier.source.GearImplicitModifierSource;
import com.wanderersoftherift.wotr.modifier.source.GearSocketModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ModifierHelper {

    public static void runIterationOnItem(ItemStack stack, EquipmentSlot slot, LivingEntity entity,
            ModifierHelper.ModifierInSlotVisitor visitor) {
        if (!stack.isEmpty()) {
            runOnImplicits(stack, slot, entity, visitor);
            runOnGearSockets(stack, slot, entity, visitor);
        }
    }

    private static void runOnGearSockets(ItemStack stack, EquipmentSlot slot, LivingEntity entity,
            ModifierInSlotVisitor visitor) {
        GearSockets gearSockets = stack.get(ModDataComponentType.GEAR_SOCKETS);
        if (gearSockets != null && !gearSockets.isEmpty()) {
            for (GearSocket socket : gearSockets.sockets()) {
                if (socket.isEmpty()) {
                    continue;
                }
                Holder<Modifier> modifier = socket.modifier().get().modifier();
                if (modifier != null) {
                    ModifierSource source = new GearSocketModifierSource(socket, gearSockets, slot, entity);
                    visitor.accept(modifier, socket.modifier().get().roll(), source);
                }
            }
        }
    }

    private static void runOnImplicits(ItemStack stack, EquipmentSlot slot, LivingEntity entity,
            ModifierInSlotVisitor visitor) {
        GearImplicits implicits = stack.get(ModDataComponentType.GEAR_IMPLICITS);
        if (implicits != null) {
            List<ModifierInstance> modifierInstances = implicits.modifierInstances(stack, entity.level());
            for (ModifierInstance modifier : modifierInstances) {
                ModifierSource source = new GearImplicitModifierSource(implicits, slot, entity);
                visitor.accept(modifier.modifier(), modifier.roll(), source);
            }
        }
    }

    public static void runIterationOnEquipment(LivingEntity entity, ModifierHelper.ModifierInSlotVisitor visitor) {
        for (EquipmentSlot equipmentslot : EquipmentSlot.VALUES) {
            runIterationOnItem(entity.getItemBySlot(equipmentslot), equipmentslot, entity, visitor);
        }
    }

    public static void enableModifier(LivingEntity entity) {
        runIterationOnEquipment(entity,
                (modifierHolder, roll, source) -> modifierHolder.value().enableModifier(roll, entity, source));
    }

    public static void enableModifier(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
        runIterationOnItem(stack, slot, entity,
                (modifierHolder, roll, source) -> modifierHolder.value().enableModifier(roll, entity, source));
    }

    public static void disableModifier(LivingEntity entity) {
        runIterationOnEquipment(entity,
                (modifierHolder, roll, source) -> modifierHolder.value().disableModifier(roll, entity, source));
    }

    public static void disableModifier(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
        runIterationOnItem(stack, slot, entity,
                (modifierHolder, roll, source) -> modifierHolder.value().disableModifier(roll, entity, source));
    }

    @FunctionalInterface
    public interface ModifierInSlotVisitor {
        void accept(Holder<Modifier> modifierHolder, float roll, ModifierSource item);
    }
}
