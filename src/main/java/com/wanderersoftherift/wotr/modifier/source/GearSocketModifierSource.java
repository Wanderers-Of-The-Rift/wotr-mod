package com.wanderersoftherift.wotr.modifier.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.List;

public record GearSocketModifierSource(WotrEquipmentSlot slot, int socket)
        implements ModifierSource.SlotModifierSource {

    public static final DualCodec<GearSocketModifierSource> TYPE = new DualCodec<>(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    WotrEquipmentSlot.DIRECT_CODEC.fieldOf("slot").forGetter(GearSocketModifierSource::slot),
                    Codec.INT.fieldOf("socket").forGetter(GearSocketModifierSource::socket)
            ).apply(instance, GearSocketModifierSource::new)), StreamCodec.composite(
                    WotrEquipmentSlot.STREAM_CODEC, GearSocketModifierSource::slot, ByteBufCodecs.INT,
                    GearSocketModifierSource::socket, GearSocketModifierSource::new
            )
    );

    @Override
    public DualCodec<? extends ModifierSource> getType() {
        return TYPE;
    }

    @Override
    public String getSerializedName() {
        return slot.getSerializedName() + "_" + socket;
    }

    @Override
    public List<AbstractModifierEffect> getModifierEffects(Entity entity) {
        return getItem(entity).get(WotrDataComponentType.GEAR_SOCKETS)
                .sockets()
                .get(socket)
                .modifier()
                .map(it -> it.effects())
                .orElse(Collections.emptyList());
    }
}
