package com.wanderersoftherift.wotr.modifier.source;

import com.wanderersoftherift.wotr.item.socket.GearSocket;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class GearSocketModifierSource implements ModifierSource {
    private final GearSocket socket;
    private final GearSockets sockets;
    private final WotrEquipmentSlot slot;
    private final Entity entity;

    public GearSocketModifierSource(GearSocket socket, GearSockets sockets, WotrEquipmentSlot slot, Entity entity) {
        this.socket = socket;
        this.sockets = sockets;
        this.slot = slot;
        this.entity = entity;
    }

    @Override
    public String getSerializedName() {
        return slot.getSerializedName() + "_" + sockets.sockets().indexOf(socket);
    }

    @Override
    public @Nullable WotrEquipmentSlot slot() {
        return slot;
    }
}
