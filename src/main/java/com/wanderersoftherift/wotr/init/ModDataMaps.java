package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

/**
 * Registers any Data Maps defined by the mod
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModDataMaps {
    public static final DataMapType<Item, EssenceValue> ESSENCE_VALUE_DATA = DataMapType
            .builder(WanderersOfTheRift.id("essence_value"), Registries.ITEM, EssenceValue.CODEC)
            .synced(EssenceValue.CODEC, true)
            .build();

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ESSENCE_VALUE_DATA);
    }
}
