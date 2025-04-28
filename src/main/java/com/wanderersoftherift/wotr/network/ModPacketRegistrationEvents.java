package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.network.map.S2CRiftMapperPlayerRemove;
import com.wanderersoftherift.wotr.network.map.S2CRiftMapperPlayerUpdatePacket;
import com.wanderersoftherift.wotr.network.map.S2CRiftMapperRoomPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModPacketRegistrationEvents {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(S2CRiftObjectiveStatusPacket.TYPE, S2CRiftObjectiveStatusPacket.STREAM_CODEC,
                new S2CRiftObjectiveStatusPacket.S2CRiftObjectiveStatusPacketHandler());
        registrar.playToServer(C2SRuneAnvilApplyPacket.TYPE, C2SRuneAnvilApplyPacket.STREAM_CODEC,
                new C2SRuneAnvilApplyPacket.C2SRuneAnvilApplyPacketHandler());

        registrar.playToClient(S2CLevelListUpdatePacket.TYPE, S2CLevelListUpdatePacket.STREAM_CODEC,
                new S2CLevelListUpdatePacket.S2CLevelListUpdatePacketHandler());

        registrar.playToClient(S2CRiftMapperRoomPacket.TYPE, S2CRiftMapperRoomPacket.STREAM_CODEC,
                new S2CRiftMapperRoomPacket.S2CRiftMapperRoomPacketHandler());
        registrar.playToClient(S2CRiftMapperPlayerUpdatePacket.TYPE, S2CRiftMapperPlayerUpdatePacket.STREAM_CODEC,
                new S2CRiftMapperPlayerUpdatePacket.S2CRiftMapperPlayerUpdatePacketHandler());
    }
}
