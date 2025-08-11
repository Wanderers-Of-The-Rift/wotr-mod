package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.ClientAttachEffects;
import com.wanderersoftherift.wotr.abilities.effects.attachment.ClientAttachEffect;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Replicates a single attach effect to a client
 */
public record AttachEffectPayload(ClientAttachEffect effect) implements CustomPacketPayload {

    public static final Type<AttachEffectPayload> TYPE = new Type<>(WanderersOfTheRift.id("attach_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AttachEffectPayload> STREAM_CODEC = StreamCodec
            .composite(ClientAttachEffect.STREAM_CODEC, AttachEffectPayload::effect, AttachEffectPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        ClientAttachEffects data = context.player().getData(WotrAttachments.CLIENT_ATTACH_EFFECTS);
        data.add(effect);
    }
}
