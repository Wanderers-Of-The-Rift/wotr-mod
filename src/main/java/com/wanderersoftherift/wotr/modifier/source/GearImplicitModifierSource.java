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

import java.util.List;

public record GearImplicitModifierSource(WotrEquipmentSlot slot, int index)
        implements ModifierSource.SlotModifierSource {

    public static final DualCodec<GearImplicitModifierSource> TYPE = new DualCodec<>(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    WotrEquipmentSlot.DIRECT_CODEC.fieldOf("slot").forGetter(GearImplicitModifierSource::slot),
                    Codec.INT.fieldOf("index").forGetter(GearImplicitModifierSource::index)
            ).apply(instance, GearImplicitModifierSource::new)), StreamCodec.composite(
                    WotrEquipmentSlot.STREAM_CODEC, GearImplicitModifierSource::slot, ByteBufCodecs.INT,
                    GearImplicitModifierSource::index, GearImplicitModifierSource::new
            )
    );

    @Override
    public DualCodec<? extends ModifierSource> getType() {
        return TYPE;
    }

    @Override
    public String getSerializedName() {
        return "implicits_" + slot.getSerializedName() + "_" + index;
    }

    @Override
    public List<AbstractModifierEffect> getModifierEffects(Entity entity) {
        return getItem(entity).get(WotrDataComponentType.GEAR_IMPLICITS).modifierInstances().get(index).effects();
    }
}
