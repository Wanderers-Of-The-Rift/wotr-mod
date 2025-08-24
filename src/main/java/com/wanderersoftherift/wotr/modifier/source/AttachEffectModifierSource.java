package com.wanderersoftherift.wotr.modifier.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A modifier sourced from an attach effect
 * 
 * @param uuid  The attach effect id
 * @param index The index of the modifier
 */
public record AttachEffectModifierSource(UUID uuid, int index) implements ModifierSource {

    public static final DualCodec<AttachEffectModifierSource> TYPE = new DualCodec<>(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("uuid").forGetter(AttachEffectModifierSource::uuid),
                    Codec.INT.fieldOf("index").forGetter(AttachEffectModifierSource::index)
            ).apply(instance, AttachEffectModifierSource::new)), StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, AttachEffectModifierSource::uuid, ByteBufCodecs.INT,
                    AttachEffectModifierSource::index, AttachEffectModifierSource::new
            )
    );

    @Override
    public DualCodec<? extends ModifierSource> getType() {
        return TYPE;
    }

    @Override
    public @NotNull String getSerializedName() {
        return "attach_effect_" + uuid.toString() + "_" + index;
    }

    @Override
    public List<AbstractModifierEffect> getModifierEffects(Entity entity) {
        return entity.getExistingData(WotrAttachments.ATTACHED_EFFECTS)
                .map(it -> it.getModifiers(uuid).get(index).effects())
                .orElse(Collections.emptyList());
    }
}
