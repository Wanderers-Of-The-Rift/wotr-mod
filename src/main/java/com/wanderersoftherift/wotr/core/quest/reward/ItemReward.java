package com.wanderersoftherift.wotr.core.quest.reward;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.serialization.DualCodec;
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

    public static final DualCodec<ItemReward> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<ItemReward> getType() {
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
