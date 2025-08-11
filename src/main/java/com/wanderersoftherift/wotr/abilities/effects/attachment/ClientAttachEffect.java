package com.wanderersoftherift.wotr.abilities.effects.attachment;

import com.wanderersoftherift.wotr.abilities.EffectMarker;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Clientside information on an attached effect
 * 
 * @param id
 * @param marker
 * @param modifiers
 * @param until
 */
public record ClientAttachEffect(UUID id, Optional<Holder<EffectMarker>> marker, List<ModifierInstance> modifiers,
        Optional<Long> until) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientAttachEffect> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, ClientAttachEffect::id,
            ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(WotrRegistries.Keys.EFFECT_MARKERS)),
            ClientAttachEffect::marker, ModifierInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ClientAttachEffect::modifiers, ByteBufCodecs.optional(ByteBufCodecs.LONG), ClientAttachEffect::until,
            ClientAttachEffect::new);
}
