package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.ability.Cooldown;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AbilityCooldownUpdatePayload(int slot, long from, long until) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AbilityCooldownUpdatePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "cooldown_activated"));

    public static final StreamCodec<ByteBuf, AbilityCooldownUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AbilityCooldownUpdatePayload::slot, ByteBufCodecs.LONG,
            AbilityCooldownUpdatePayload::from, ByteBufCodecs.LONG, AbilityCooldownUpdatePayload::until,
            AbilityCooldownUpdatePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        AbilitySlots data = context.player().getData(WotrAttachments.ABILITY_SLOTS);
        data.getStackInSlot(slot).set(WotrDataComponentType.COOLDOWN, new Cooldown(from, until));
    }
}
