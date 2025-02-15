package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.modifier.Modifier;
import com.dimensiondelvers.dimensiondelvers.modifier.effect.AbstractModifierEffect;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = DimensionDelvers.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
    @SubscribeEvent
    static void registerRegistries(NewRegistryEvent event) {
        event.register(ModModifierEffects.MODIFIER_TYPE_REGISTRY);
    }

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                ModModifierEffects.MODIFIER_EFFECT_KEY,
                AbstractModifierEffect.DIRECT_CODEC,
                AbstractModifierEffect.DIRECT_CODEC
        );
        event.dataPackRegistry(
                ModModifiers.MODIFIER_KEY,
                Modifier.DIRECT_CODEC,
                Modifier.DIRECT_CODEC
        );
    }
}
