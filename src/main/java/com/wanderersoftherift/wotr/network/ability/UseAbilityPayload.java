package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.ability.Cooldown;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UseAbilityPayload(int slot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UseAbilityPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "ability_type"));

    public static final StreamCodec<ByteBuf, UseAbilityPayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT,
            UseAbilityPayload::slot, UseAbilityPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player) || player.isSpectator() || player.isDeadOrDying()) {
            return;
        }
        AbilitySlots abilitySlots = player.getData(WotrAttachments.ABILITY_SLOTS);
        ItemStack abilityItem = abilitySlots.getStackInSlot(slot());
        if (abilityItem.isEmpty() || !abilityItem.has(WotrDataComponentType.ABILITY)) {
            return;
        }
        Ability ability = abilityItem.get(WotrDataComponentType.ABILITY).value();
        abilitySlots.setSelectedSlot(slot());

        ability.onActivate(player, abilityItem);
        Cooldown cooldown = abilityItem.get(WotrDataComponentType.COOLDOWN);
        PacketDistributor.sendToPlayer(player,
                new AbilityCooldownUpdatePayload(slot, cooldown.from(), cooldown.until()));
    }
}
