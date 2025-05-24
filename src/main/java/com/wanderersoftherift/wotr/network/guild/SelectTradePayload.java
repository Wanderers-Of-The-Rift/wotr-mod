package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.trading.TradeListing;
import com.wanderersoftherift.wotr.gui.menu.TradingMenu;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SelectTradePayload(Holder<TradeListing> trade) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SelectTradePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "trade_selection"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectTradePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(WotrRegistries.Keys.TRADE_LISTING), SelectTradePayload::trade,
            SelectTradePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        if (context.player().containerMenu instanceof TradingMenu menu && menu.stillValid(context.player())) {
            menu.selectTrade(trade);
        }
    }
}
