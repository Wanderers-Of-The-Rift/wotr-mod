package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD)
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
                ModDatapackRegistries.MODIFIER_KEY,
                Modifier.DIRECT_CODEC,
                Modifier.DIRECT_CODEC
        );
        event.dataPackRegistry(
                ModDatapackRegistries.RUNEGEM_DATA_KEY,
                RunegemData.CODEC,
                RunegemData.CODEC
        );
    }
}
