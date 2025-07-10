package com.wanderersoftherift.wotr.core.guild.quest.reward;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Reward;
import com.wanderersoftherift.wotr.core.guild.quest.RewardType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * This reward provides an item when the quest is completed
 *
 * @param item The item to award upon completion
 */
public record ItemReward(ItemStack item) implements Reward {

    public static final MapCodec<ItemReward> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ItemStack.STRICT_CODEC.fieldOf("item").forGetter(ItemReward::item)
            ).apply(instance, ItemReward::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemReward> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, ItemReward::item, ItemReward::new
    );

    public static final RewardType<ItemReward> TYPE = new RewardType<>(CODEC, STREAM_CODEC);

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isItem() {
        return true;
    }

    @Override
    public void apply(ServerPlayer player) {
    }

    @Override
    public ItemStack generateItem() {
        return item.copy();
    }

}
