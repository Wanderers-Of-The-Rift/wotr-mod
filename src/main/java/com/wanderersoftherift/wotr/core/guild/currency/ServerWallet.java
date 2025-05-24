package com.wanderersoftherift.wotr.core.guild.currency;

import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.network.guild.WalletUpdatePayload;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Map;

/**
 * Wrapper for wallet attachment that replicates changes to owning player
 */
public class ServerWallet implements WalletAccessor {

    private ServerPlayer player;
    private Wallet wallet;

    public ServerWallet(ServerPlayer player) {
        this.player = player;
        this.wallet = player.getData(WotrAttachments.WALLET);
    }

    @Override
    public List<Holder<Currency>> availableCurrencies() {
        return List.of();
    }

    @Override
    public int get(Holder<Currency> currency) {
        return wallet.get(currency);
    }

    @Override
    public void set(Holder<Currency> currency, int amount) {
        wallet.set(currency, amount);
        PacketDistributor.sendToPlayer(player, new WalletUpdatePayload(Map.of(currency, amount)));
    }
}
