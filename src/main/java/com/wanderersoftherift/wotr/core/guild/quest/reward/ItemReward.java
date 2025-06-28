package com.wanderersoftherift.wotr.core.guild.quest.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Reward;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * This reward provides an item when the quest is completed
 *
 * @param item     The item to award upon completion
 * @param quantity The quantity of the item
 */
public record ItemReward(Holder<Item> item, int quantity) implements Reward {

    public static final MapCodec<ItemReward> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Item.CODEC.fieldOf("item").forGetter(ItemReward::item),
                    Codec.INT.fieldOf("quantity").forGetter(ItemReward::quantity)
            ).apply(instance, ItemReward::new));

    @Override
    public MapCodec<? extends Reward> getCodec() {
        return CODEC;
    }

    public ItemStack getItemStack() {
        return new ItemStack(item, quantity);
    }
}
