package com.wanderersoftherift.wotr.events;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.network.C2SRuneAnvilApplyPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModPacketRegistrationEvent {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                C2SRuneAnvilApplyPacket.TYPE,
                C2SRuneAnvilApplyPacket.STREAM_CODEC,
                new C2SRuneAnvilApplyPacket.C2SRuneAnvilApplyPacketHandler()
        );
    }
}
