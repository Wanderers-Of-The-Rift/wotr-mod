package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.VirtualEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlotFromMC;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrEquipmentSlotTypes {
    public static final DeferredRegister<DualCodec<? extends WotrEquipmentSlot>> EQUIPMENT_SLOTS = DeferredRegister
            .create(WotrRegistries.EQUIPMENT_SLOTS, WanderersOfTheRift.MODID);

    public static final Supplier<DualCodec<? extends WotrEquipmentSlot>> VANILLA_SLOTS = EQUIPMENT_SLOTS
            .register("vanilla", () -> WotrEquipmentSlotFromMC.TYPE);
    public static final Supplier<DualCodec<? extends WotrEquipmentSlot>> ABILITY_SLOTS = EQUIPMENT_SLOTS
            .register("ability", () -> AbilityEquipmentSlot.TYPE);
    public static final Supplier<DualCodec<? extends WotrEquipmentSlot>> VIRTUAL_SLOTS = EQUIPMENT_SLOTS
            .register("virtual", () -> VirtualEquipmentSlot.TYPE);
}
