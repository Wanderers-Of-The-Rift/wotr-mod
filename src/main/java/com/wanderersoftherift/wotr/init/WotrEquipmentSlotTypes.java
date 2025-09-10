package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.slot.AbilityEquipmentSlot;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlotFromMC;
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
}
