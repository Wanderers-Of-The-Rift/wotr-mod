package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.EffectMarkers;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Replicates the removal of an attach effect
 * 
 * @param id The instance id of the effect
 */
public record DetachEffectPayload(UUID id) implements CustomPacketPayload {
    public static final Type<DetachEffectPayload> TYPE = new Type<>(WanderersOfTheRift.id("detatch_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DetachEffectPayload> STREAM_CODEC = StreamCodec
            .composite(UUIDUtil.STREAM_CODEC, DetachEffectPayload::id, DetachEffectPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        EffectMarkers data = context.player().getData(WotrAttachments.EFFECT_MARKERS);
        data.remove(id);
    }
}
