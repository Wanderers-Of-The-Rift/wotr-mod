package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.EffectMarker;
import com.wanderersoftherift.wotr.abilities.attachment.ClientAttachEffects;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 *
 */
public record AttachEffectPayload(UUID id, Optional<Holder<EffectMarker>> marker, List<ModifierInstance> modifiers)
        implements CustomPacketPayload {

    public static final Type<AttachEffectPayload> TYPE = new Type<>(WanderersOfTheRift.id("attach_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AttachEffectPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, AttachEffectPayload::id,
            ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(WotrRegistries.Keys.EFFECT_MARKERS)),
            AttachEffectPayload::marker, ModifierInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
            AttachEffectPayload::modifiers, AttachEffectPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        ClientAttachEffects data = context.player().getData(WotrAttachments.CLIENT_ATTACH_EFFECTS);
        data.add(id, marker.orElse(null), modifiers);
    }
}
