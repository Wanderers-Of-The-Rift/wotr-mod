package com.dimensiondelvers.dimensiondelvers.events;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.network.C2SRuneAnvilCombinePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = DimensionDelvers.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModPacketRegistrationEvent {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                C2SRuneAnvilCombinePacket.TYPE,
                C2SRuneAnvilCombinePacket.STREAM_CODEC,
                new C2SRuneAnvilCombinePacket.C2SRuneAnvilCombinePacketHandler()
        );
    }
}
