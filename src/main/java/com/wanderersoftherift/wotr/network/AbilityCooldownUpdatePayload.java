package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.PlayerCooldownData;
import com.wanderersoftherift.wotr.init.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AbilityCooldownUpdatePayload(int slot, int cooldownLength, int cooldownRemaining)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AbilityCooldownUpdatePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "cooldown_activated"));

    public static final StreamCodec<ByteBuf, AbilityCooldownUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AbilityCooldownUpdatePayload::slot, ByteBufCodecs.INT,
            AbilityCooldownUpdatePayload::cooldownLength, ByteBufCodecs.INT,
            AbilityCooldownUpdatePayload::cooldownRemaining, AbilityCooldownUpdatePayload::new);
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        PlayerCooldownData cooldowns = context.player().getData(ModAttachments.ABILITY_COOLDOWNS);
        cooldowns.setCooldown(slot(), cooldownLength(), cooldownRemaining());
        context.player().setData(ModAttachments.ABILITY_COOLDOWNS, cooldowns);
    }
}
