package com.wanderersoftherift.wotr.abilities.effects.attachment;

import com.wanderersoftherift.wotr.abilities.EffectMarker;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

/**
 * Clientside information to display an attached effect
 * 
 * @param id
 * @param marker
 * @param until
 */
public record EffectMarkerInstance(UUID id, Optional<Holder<EffectMarker>> marker, Optional<Long> until) {
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectMarkerInstance> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, EffectMarkerInstance::id,
            ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(WotrRegistries.Keys.EFFECT_MARKERS)),
            EffectMarkerInstance::marker, ByteBufCodecs.optional(ByteBufCodecs.LONG), EffectMarkerInstance::until,
            EffectMarkerInstance::new);
}
