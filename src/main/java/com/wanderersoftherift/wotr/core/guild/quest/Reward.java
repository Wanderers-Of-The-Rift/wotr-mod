package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface for a type of quest reward
 * <p>
 * All rewards are also reward providers that provide themselves - this allows quests to either directly give specific
 * rewards or to generate rewards with some random elements
 * </p>
 */
public interface Reward extends RewardProvider {
    Codec<Reward> DIRECT_CODEC = WotrRegistries.REWARD_TYPES.byNameCodec().dispatch(Reward::getType, RewardType::codec);

    StreamCodec<RegistryFriendlyByteBuf, Reward> STREAM_CODEC = ByteBufCodecs.registry(WotrRegistries.Keys.REWARD_TYPES)
            .dispatch(Reward::getType, RewardType::streamCodec);

    /**
     * @return The type of this reward
     */
    RewardType<?> getType();

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

    /// Overrides to allow rewards to act as reward providers for themselves

    @Override
    default @NotNull List<Reward> generateReward(LootParams params) {
        return List.of(this);
    }

    @Override
    default MapCodec<? extends RewardProvider> getCodec() {
        return getType().codec();
    }
}
