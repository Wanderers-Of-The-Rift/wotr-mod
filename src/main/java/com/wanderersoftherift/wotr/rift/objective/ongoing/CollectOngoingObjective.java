package com.wanderersoftherift.wotr.rift.objective.ongoing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ProgressObjective;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CollectOngoingObjective implements ProgressObjective {

    public static final MapCodec<CollectOngoingObjective> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    Codec.INT.fieldOf("targetAmount").forGetter(CollectOngoingObjective::getTargetProgress),
                    Codec.INT.fieldOf("currentAmount").forGetter(CollectOngoingObjective::getCurrentProgress),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(CollectOngoingObjective::getItem)
            ).apply(instance, CollectOngoingObjective::new));

    private final int targetAmount;
    private int currentAmount;
    private final Item item;

    public CollectOngoingObjective(int targetAmount, Item item) {
        this(targetAmount, 0, item);
    }

    public CollectOngoingObjective(int targetAmount, int currentAmount, Item item) {
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.item = item;
    }

    @Override
    public MapCodec<? extends OngoingObjective> getCodec() {
        return CODEC;
    }

    @Override
    public int getCurrentProgress() {
        return currentAmount;
    }

    @Override
    public int getTargetProgress() {
        return targetAmount;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public Component getObjectiveStartMessage() {
        return Component.translatable(
                WanderersOfTheRift.translationId("objective", "collect.description"), targetAmount,
                new ItemStack(item).getHoverName()
        );
    }

    public boolean onInventoryCheck(Player player) {
        if (isComplete()) return false;

        int count = countItemInInventory(player);
        if (count != currentAmount) {
            currentAmount = count;
            return true;
        }
        return false;
    }

    // Needs to check the amount of items player has when entering rift and that amount is the "0".
    private int countItemInInventory(Player player) {
        int total = 0;
        NonNullList<ItemStack> items = player.getInventory().items;
        for (ItemStack stack : items) {
            if (!stack.isEmpty() && stack.is(item)) {
                total += stack.getCount();
            }
        }
        return total;
    }
}
