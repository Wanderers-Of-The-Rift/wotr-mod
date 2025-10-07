package com.wanderersoftherift.wotr.rift.objective.ongoing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerItemWrapper;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerType;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerWrapper;
import com.wanderersoftherift.wotr.core.inventory.containers.DirectContainerItemWrapper;
import com.wanderersoftherift.wotr.core.inventory.containers.NonContainerWrapper;
import com.wanderersoftherift.wotr.core.inventory.snapshot.InventorySnapshot;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftEntryState;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterData;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ProgressObjective;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
        if (isComplete()) {
            return false;
        }

        int newAmount;
        if (player instanceof ServerPlayer sp) {
            InventorySnapshot baseline = getEntranceSnapshot(sp);
            if (baseline != null && !baseline.isEmpty()) {
                newAmount = Math.max(0, deltaFromSnapshot(sp, baseline, item));
            } else {
                newAmount = countItemInInventory(sp, item);
            }
        } else {
            newAmount = countItemFlat(player, item);
        }

        if (newAmount != currentAmount) {
            currentAmount = newAmount;
            return true;
        }
        return false;
    }

    private static int deltaFromSnapshot(ServerPlayer player, InventorySnapshot snapshot, ItemLike itemLike) {
        int now = countItemInInventory(player, itemLike);
        int baseline = countItemInSnapshot(snapshot, itemLike);
        return now - baseline;
    }

    private static int countItemInSnapshot(InventorySnapshot snapshot, ItemLike itemLike) {
        int total = 0;
        for (ItemStack snapStack : snapshot.items()) {
            if (ItemStack.isSameItemSameComponents(snapStack, new ItemStack(itemLike.asItem()))) {
                total += snapStack.getCount();
            }
        }
        return total;
    }

    private static InventorySnapshot getEntranceSnapshot(ServerPlayer player) {
        var states = player.getData(WotrAttachments.RIFT_ENTRY_STATES);
        if (states == null || states.isEmpty()) {
            return null;
        }
        RiftEntryState last = states.get(states.size() - 1);
        return last.entranceInventory();
    }

    private static int countItemInInventory(ServerPlayer player, ItemLike itemLike) {
        var containerTypes = player.level().registryAccess().lookupOrThrow(WotrRegistries.Keys.CONTAINER_TYPES);
        int total = 0;
        total += countInIterable(containerTypes, player.getInventory().items, itemLike);
        total += countInIterable(containerTypes, player.getInventory().armor, itemLike);
        total += countInIterable(containerTypes, player.getInventory().offhand, itemLike);
        return total;
    }

    private static int countInIterable(
            Registry<ContainerType> containerTypes,
            Iterable<ItemStack> stacks,
            ItemLike itemLike) {
        int total = 0;
        for (ItemStack stack : stacks) {
            total += countInWrapper(containerTypes, new DirectContainerItemWrapper(stack), itemLike);
        }
        return total;
    }

    private static int countInWrapper(
            Registry<ContainerType> containerTypes,
            ContainerItemWrapper wrapper,
            ItemLike itemLike) {
        ItemStack is = wrapper.getReadOnlyItemStack();
        int total = 0;

        if (!is.isEmpty() && is.is(itemLike.asItem())) {
            total += is.getCount();
        }

        ContainerWrapper contents = getContents(containerTypes, is);
        for (ContainerItemWrapper content : contents) {
            total += countInWrapper(containerTypes, content, itemLike);
        }
        return total;
    }

    private static ContainerWrapper getContents(Registry<ContainerType> containerTypes, ItemStack stack) {
        return containerTypes.stream()
                .filter(type -> type.isContainer(stack))
                .findAny()
                .map(type -> type.getWrapper(stack))
                .orElse(NonContainerWrapper.INSTANCE);
    }

    private static int countItemFlat(Player player, ItemLike itemLike) {
        int total = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty() && stack.is(itemLike.asItem())) {
                total += stack.getCount();
            }
        }
        return total;
    }

    public static Item pickItemFromLootOrThrow(ServerLevel level, ResourceKey<LootTable> tableKey) {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(tableKey);

        final int attempts = 6;
        for (int attempt = 0; attempt < attempts; attempt++) {
            long seed = level.getRandom().nextLong();

            LootParams params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(BlockPos.ZERO))
                    .withLuck(0.0f)
                    .create(LootContextParamSets.CHEST);

            List<ItemStack> rolls = table.getRandomItems(params, seed);

            Set<Item> stackables = new LinkedHashSet<>();
            for (ItemStack s : rolls) {
                if (!s.isEmpty() && s.getMaxStackSize() > 1) {
                    stackables.add(s.getItem());
                }
            }
            if (!stackables.isEmpty()) {
                int idx = level.getRandom().nextInt(stackables.size());
                return stackables.stream().skip(idx).findFirst().orElseGet(() -> stackables.iterator().next());
            }
        }

        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(BlockPos.ZERO))
                .withLuck(0.0f)
                .create(LootContextParamSets.CHEST);
        List<ItemStack> rolls = table.getRandomItems(params, level.getRandom().nextLong());
        for (ItemStack s : rolls) {
            if (!s.isEmpty()) {
                return s.getItem();
            }
        }

        return net.minecraft.world.item.Items.IRON_INGOT;
    }

    public static Item pickItemFromLootOrThrow(ServerPlayer sp, ResourceKey<LootTable> tableKey) {
        return pickItemFromLootOrThrow(sp.serverLevel(), tableKey);
    }

    @Override
    public void registerUpdaters(RiftParameterData params, RiftData data, ServerLevel level) {
    }
}
