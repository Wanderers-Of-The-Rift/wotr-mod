package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.effects.attachment.EffectMarkerInstance;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Replicates addition of an effect marker
 */
public record AddEffectMarkerPayload(EffectMarkerInstance effect) implements CustomPacketPayload {

    public static final Type<AddEffectMarkerPayload> TYPE = new Type<>(WanderersOfTheRift.id("add_effect_marker"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AddEffectMarkerPayload> STREAM_CODEC = StreamCodec
            .composite(EffectMarkerInstance.STREAM_CODEC, AddEffectMarkerPayload::effect, AddEffectMarkerPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        context.player().getData(WotrAttachments.EFFECT_MARKERS).add(effect);
    }
}
