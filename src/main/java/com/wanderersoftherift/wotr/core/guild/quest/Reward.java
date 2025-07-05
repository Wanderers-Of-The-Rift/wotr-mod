package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.codec.LaxRegistryCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * Interface for a type of quest reward
 */
public interface Reward {
    Codec<Reward> DIRECT_CODEC = WotrRegistries.REWARD_TYPES.byNameCodec()
            .dispatch(Reward::getCodec, Function.identity());
    Codec<Holder<Reward>> CODEC = LaxRegistryCodec.create(WotrRegistries.Keys.REWARDS);
    StreamCodec<RegistryFriendlyByteBuf, Holder<Reward>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.REWARDS);

    /**
     * @return The codec for serializing this reward
     */
    MapCodec<? extends Reward> getCodec();

    /**
     * @return Whether this is an item-based reward
     */
    boolean isItem();

    /**
     * Applies non-item reward elements to the player
     *
     * @param player The player to give the reward to
     */
    void apply(ServerPlayer player);

    /**
     * Generates item for this is an item reward
     */
    default ItemStack generateItem() {
        return ItemStack.EMPTY;
    }
}
