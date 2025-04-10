package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.RuneAnvilMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public record C2SRuneAnvilApplyPacket(int containerId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<C2SRuneAnvilApplyPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "c2s_rune_anvil_apply"));

    public static final StreamCodec<ByteBuf, C2SRuneAnvilApplyPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            C2SRuneAnvilApplyPacket::containerId,
            C2SRuneAnvilApplyPacket::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class C2SRuneAnvilApplyPacketHandler implements IPayloadHandler<C2SRuneAnvilApplyPacket> {
        public void handle(@NotNull C2SRuneAnvilApplyPacket packet, @NotNull IPayloadContext context) {
            Player player = context.player();
            AbstractContainerMenu menu = player.containerMenu;
            if (menu instanceof RuneAnvilMenu runeAnvilMenu && menu.containerId == packet.containerId()) {
                runeAnvilMenu.apply();
            }
        }
    }
}
