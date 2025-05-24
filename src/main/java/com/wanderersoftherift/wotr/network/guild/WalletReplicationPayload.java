package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.currency.Wallet;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record WalletReplicationPayload(Wallet wallet) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<WalletReplicationPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "wallet_replication"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WalletReplicationPayload> STREAM_CODEC = StreamCodec
            .composite(
                    Wallet.STREAM_CODEC, WalletReplicationPayload::wallet, WalletReplicationPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().setData(WotrAttachments.WALLET.get(), wallet);
    }
}
