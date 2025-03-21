package com.wanderersoftherift.wotr.events;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.network.S2CRiftObjectiveStatusPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModPacketRegistrationEvents {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                S2CRiftObjectiveStatusPacket.TYPE,
                S2CRiftObjectiveStatusPacket.STREAM_CODEC,
                new S2CRiftObjectiveStatusPacket.S2CRiftObjectiveStatusPacketHandler()
        );
    }
}
