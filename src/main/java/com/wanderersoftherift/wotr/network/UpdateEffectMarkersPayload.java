package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectDisplayData;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectMarker;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Payload that transmits a change/delta in the effect markers a client should display.
 * 
 * @param updates Any new or updated EffectMarkers with their remaining durations (in ticks)
 * @param remove  Any EffectMarkers to remove
 */
public record UpdateEffectMarkersPayload(Map<Holder<EffectMarker>, Integer> updates, List<Holder<EffectMarker>> remove)
        implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateEffectMarkersPayload> TYPE = new CustomPacketPayload.Type<>(
            WanderersOfTheRift.id("update_effect_markers"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateEffectMarkersPayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.map(LinkedHashMap::new,
                            ByteBufCodecs.holderRegistry(WotrRegistries.Keys.EFFECT_MARKERS), ByteBufCodecs.INT),
                    UpdateEffectMarkersPayload::updates,
                    ByteBufCodecs.holderRegistry(WotrRegistries.Keys.EFFECT_MARKERS).apply(ByteBufCodecs.list()),
                    UpdateEffectMarkersPayload::remove, UpdateEffectMarkersPayload::new);

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        EffectDisplayData data = context.player().getData(WotrAttachments.EFFECT_DISPLAY);
        for (var entry : updates.entrySet()) {
            data.setMarker(entry.getKey(), entry.getValue());
        }
        for (Holder<EffectMarker> marker : remove) {
            data.removeMarker(marker);
        }
    }
}
