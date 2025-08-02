package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlotFromMC;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrEquipmentSlotTypes {
    public static final DeferredRegister<MapCodec<? extends WotrEquipmentSlot>> EQUIPMENT_SLOTS = DeferredRegister
            .create(WotrRegistries.EQUIPMENT_SLOTS, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends WotrEquipmentSlot>> VANILLA_SLOTS = EQUIPMENT_SLOTS
            .register("vanilla", () -> WotrEquipmentSlotFromMC.CODEC);
    public static final Supplier<MapCodec<? extends WotrEquipmentSlot>> ABILITY_SLOTS = EQUIPMENT_SLOTS
            .register("ability", () -> AbilityEquipmentSlot.CODEC);
}
