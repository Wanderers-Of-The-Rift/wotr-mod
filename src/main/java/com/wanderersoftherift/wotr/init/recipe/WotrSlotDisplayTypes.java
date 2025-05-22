package com.wanderersoftherift.wotr.init.recipe;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.crafting.display.EssenceSlotDisplay;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrSlotDisplayTypes {
    public static final DeferredRegister<SlotDisplay.Type<?>> SLOT_DISPLAY_TYPES = DeferredRegister
            .create(Registries.SLOT_DISPLAY, WanderersOfTheRift.MODID);

    public static final Supplier<SlotDisplay.Type<EssenceSlotDisplay>> ESSENCE_SLOT_DISPLAY = SLOT_DISPLAY_TYPES
            .register("essence_slot",
                    () -> new SlotDisplay.Type<>(EssenceSlotDisplay.CODEC, EssenceSlotDisplay.STREAM_CODEC));
}
