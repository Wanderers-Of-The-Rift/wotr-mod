package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilitySource;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AbilityCooldownUpdatePayload(AbilitySource source, long from, long until) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AbilityCooldownUpdatePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "cooldown_activated"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityCooldownUpdatePayload> STREAM_CODEC = StreamCodec
            .composite(
                    AbilitySource.STREAM_CODEC, AbilityCooldownUpdatePayload::source, ByteBufCodecs.LONG,
                    AbilityCooldownUpdatePayload::from, ByteBufCodecs.LONG, AbilityCooldownUpdatePayload::until,
                    AbilityCooldownUpdatePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.ABILITY_COOLDOWNS).setCooldown(source, from, until);
    }
}
