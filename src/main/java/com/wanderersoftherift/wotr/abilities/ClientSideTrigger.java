package com.wanderersoftherift.wotr.abilities;

import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.abilities.triggers.MainAttackTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.TrackableTrigger;
import com.wanderersoftherift.wotr.network.ability.ClientTriggerTriggerPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public interface ClientSideTrigger {

    public static boolean hasTrigger(Holder<TrackableTrigger.TriggerType<?>> type) {
        return TriggerTracker.forEntity(Minecraft.getInstance().player).hasListenersOnTrigger(type);
    }

    public static void useTrigger(TrackableTrigger trigger) {
        switch (trigger) {
            case MainAttackTrigger mainAttackTrigger ->
                PacketDistributor.sendToServer(new ClientTriggerTriggerPayload(0));
            default -> {
            }
        }
    }

    static void useTriggerServer(ServerPlayer player, int trigger) {
        switch (trigger) {
            case 0 -> {
                TriggerTracker.forEntity(player).trigger(MainAttackTrigger.INSTANCE);
            }
        }
    }
}
