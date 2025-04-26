package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.PlayerCooldownData;
import com.wanderersoftherift.wotr.init.ModAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Payload for transmitting ability cooldown slot information to a client
 * 
 * @param cooldowns The initial length of each cooldown (in ticks)
 * @param remaining The remaining length of each cooldown
 */
public record AbilitySlotsCooldownsPayload(List<Integer> cooldowns, List<Integer> remaining)
        implements CustomPacketPayload {
    public static final Type<AbilitySlotsCooldownsPayload> TYPE = new Type<>(
            WanderersOfTheRift.id("ability_slots_cooldowns"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilitySlotsCooldownsPayload> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.INT.apply(ByteBufCodecs.list()), AbilitySlotsCooldownsPayload::cooldowns,
                    ByteBufCodecs.INT.apply(ByteBufCodecs.list()), AbilitySlotsCooldownsPayload::remaining,
                    AbilitySlotsCooldownsPayload::new);

    public AbilitySlotsCooldownsPayload(PlayerCooldownData data) {
        this(new ArrayList<>(), new ArrayList<>());
        for (int i = 0; i < data.slots(); i++) {
            cooldowns.add(data.getLastCooldownValue(i));
            remaining.add(data.getCooldownRemaining(i));
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        PlayerCooldownData data = new PlayerCooldownData();
        for (int i = 0; i < cooldowns.size() && i < remaining.size(); i++) {
            data.setCooldown(i, cooldowns.get(i), remaining.get(i));
        }
        context.player().setData(ModAttachments.ABILITY_COOLDOWNS, data);
    }
}
