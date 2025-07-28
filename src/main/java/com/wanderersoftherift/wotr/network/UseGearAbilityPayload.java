package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.gear.AbstractGearAbility;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UseGearAbilityPayload(boolean isBasic) implements CustomPacketPayload {
    public static final Type<UseGearAbilityPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "gear_ability_type"));

    public static final StreamCodec<ByteBuf, UseGearAbilityPayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL,
            UseGearAbilityPayload::isBasic, UseGearAbilityPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player) || player.isSpectator() || player.isDeadOrDying()) {
            return;
        }
        ItemStack abilityItem = player.getMainHandItem();
        if (abilityItem.isEmpty() || !abilityItem.has(WotrDataComponentType.GEAR_BASIC) || !abilityItem.has(WotrDataComponentType.GEAR_SECONDARY)) {
            return;
        }
        if (isBasic) {
            AbstractGearAbility basic = abilityItem.get(WotrDataComponentType.GEAR_BASIC).value();
            basic.onActivate(player, abilityItem);
        }
        else {
            AbstractGearAbility secondary = abilityItem.get(WotrDataComponentType.GEAR_SECONDARY).value();
            secondary.onActivate(player, abilityItem);
        }
    }
}
