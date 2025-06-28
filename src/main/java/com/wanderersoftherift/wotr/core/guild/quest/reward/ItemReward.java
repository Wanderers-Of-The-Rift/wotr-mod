package com.wanderersoftherift.wotr.core.guild.quest.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Reward;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemReward extends Reward {

    public static final MapCodec<ItemReward> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Item.CODEC.fieldOf("item").forGetter(ItemReward::item),
                    Codec.INT.fieldOf("quantity").forGetter(ItemReward::quantity)
            ).apply(instance, ItemReward::new));

    private final Holder<Item> item;
    private final int quantity;

    public ItemReward(Holder<Item> item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    @Override
    public MapCodec<? extends Reward> getCodec() {
        return CODEC;
    }

    public Holder<Item> item() {
        return item;
    }

    public int quantity() {
        return quantity;
    }

    public ItemStack getItemStack() {
        return new ItemStack(item, quantity);
    }
}
