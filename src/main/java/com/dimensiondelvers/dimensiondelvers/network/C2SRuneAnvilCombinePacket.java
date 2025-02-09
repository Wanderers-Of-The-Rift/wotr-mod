package com.dimensiondelvers.dimensiondelvers.network;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.gui.menu.RuneAnvilMenu;
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

public record C2SRuneAnvilCombinePacket(int containerId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<C2SRuneAnvilCombinePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionDelvers.MODID, "c2s_rune_anvil_combine"));

    public static final StreamCodec<ByteBuf, C2SRuneAnvilCombinePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            C2SRuneAnvilCombinePacket::containerId,
            C2SRuneAnvilCombinePacket::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class C2SRuneAnvilCombinePacketHandler implements IPayloadHandler<C2SRuneAnvilCombinePacket> {
        public void handle(@NotNull C2SRuneAnvilCombinePacket packet, @NotNull IPayloadContext context) {
            Player player = context.player();
            AbstractContainerMenu menu = player.containerMenu;
            if (menu instanceof RuneAnvilMenu runeAnvilMenu && menu.containerId == packet.containerId()) {
                runeAnvilMenu.combine();
            }
        }
    }
}
