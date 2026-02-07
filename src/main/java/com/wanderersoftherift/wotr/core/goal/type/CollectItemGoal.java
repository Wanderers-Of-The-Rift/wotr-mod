package com.wanderersoftherift.wotr.core.goal.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalManager;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerType;
import com.wanderersoftherift.wotr.core.inventory.containers.Containers;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;

import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber
public record CollectItemGoal(Ingredient item, int count) implements ItemGoal {

    public static final MapCodec<CollectItemGoal> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.CODEC.fieldOf("item").forGetter(CollectItemGoal::item),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(CollectItemGoal::count)
            ).apply(instance, CollectItemGoal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CollectItemGoal> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, CollectItemGoal::item, ByteBufCodecs.INT, CollectItemGoal::count,
            CollectItemGoal::new
    );

    public static final DualCodec<CollectItemGoal> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<? extends Goal> getType() {
        return TYPE;
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        GoalManager.getGoalStates(event.getEntity(), CollectItemGoal.class).forEach(goalState -> {
            // Count total items that match
            // Update progress
            int amount = countItemInInventory(event.getEntity(), goalState.getGoal().item());
            goalState.setProgress(event.getEntity(), amount);
        });
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        GoalManager.getGoalStates(event.getPlayer(), CollectItemGoal.class).forEach(goalState -> {
            Registry<ContainerType> containerTypes = event.getPlayer()
                    .registryAccess()
                    .lookupOrThrow(WotrRegistries.Keys.CONTAINER_TYPES);
            if (!goalState.getGoal().item().acceptsItem(event.getOriginalStack().getItemHolder())
                    && !Containers.isContainer(containerTypes, event.getOriginalStack())) {
                return;
            }
            // Count total items that match
            // Update progress
            int amount = countItemInInventory(event.getPlayer(), goalState.getGoal().item());
            goalState.setProgress(event.getPlayer(), amount);
        });
    }

    @SubscribeEvent
    public static void onDestroyItem(PlayerDestroyItemEvent event) {
        GoalManager.getGoalStates(event.getEntity(), CollectItemGoal.class).forEach(goalState -> {
            if (!goalState.getGoal().item().acceptsItem(event.getOriginal().getItemHolder())) {
                return;
            }
            // Count total items that match
            // Update progress
            int amount = countItemInInventory(event.getEntity(), goalState.getGoal().item());
            goalState.setProgress(event.getEntity(), amount);
        });
    }

    @SubscribeEvent
    public static void onTossItem(ItemTossEvent event) {
        GoalManager.getGoalStates(event.getPlayer(), CollectItemGoal.class).forEach(goalState -> {
            if (!goalState.getGoal().item().acceptsItem(event.getEntity().getItem().getItemHolder())
                    || event.isCanceled()) {
                return;
            }
            // Count total items that match
            int amount = countItemInInventory(event.getPlayer(), goalState.getGoal().item());
            goalState.setProgress(event.getPlayer(), amount);
        });
    }

    private static int countItemInInventory(Player player, Ingredient item) {
        AtomicInteger count = new AtomicInteger();
        Containers.walk(player, heldItem -> {
            ItemStack heldItemStack = heldItem.getReadOnlyItemStack();
            if (item.acceptsItem(heldItem.getReadOnlyItemStack().getItemHolder())) {
                count.addAndGet(heldItemStack.getCount());
            }
        });
        return count.get();
    }

}
