package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectDisplayData;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectMarker;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Sends an effect marker to a player
 * 
 * @param marker
 * @param durationTicks
 */
public record SetEffectMarkerPayload(Holder<EffectMarker> marker, int durationTicks) implements CustomPacketPayload {
    public static final Type<SetEffectMarkerPayload> TYPE = new Type<>(WanderersOfTheRift.id("set_effect_marker"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetEffectMarkerPayload> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.holderRegistry(RegistryEvents.EFFECT_MARKER_REGISTRY),
                    SetEffectMarkerPayload::marker, ByteBufCodecs.INT, SetEffectMarkerPayload::durationTicks,
                    SetEffectMarkerPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        EffectDisplayData data = context.player().getData(ModAttachments.EFFECT_DISPLAY);
        data.setMarker(marker, durationTicks);
    }
}
