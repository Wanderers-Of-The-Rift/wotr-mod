package com.wanderersoftherift.wotr.rift.objective.definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.CollectOngoingObjective;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;

public class CollectObjective implements ObjectiveType {

    public static final MapCodec<CollectObjective> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("min_quantity").forGetter(CollectObjective::getMinQuantity),
            ExtraCodecs.POSITIVE_INT.fieldOf("max_quantity").forGetter(CollectObjective::getMaxQuantity),

            BuiltInRegistries.ITEM.byNameCodec()
                    .optionalFieldOf("item")
                    .forGetter(o -> Optional.ofNullable(o.getItem())),

            ResourceLocation.CODEC.optionalFieldOf("loot_table")
                    .forGetter(o -> Optional.ofNullable(o.getLootTable()).map(ResourceKey::location))

    ).apply(inst, (minQ, maxQ, optItem, optLootRl) -> {
        Item item = optItem.orElse(null);
        ResourceKey<LootTable> lootKey = optLootRl.map(rl -> ResourceKey.create(Registries.LOOT_TABLE, rl))
                .orElse(null);
        return new CollectObjective(minQ, maxQ, item, lootKey);
    }));

    private final int minQuantity;
    private final int maxQuantity;
    private final Item item;
    private final ResourceKey<LootTable> lootTable;

    public CollectObjective(int minQuantity, int maxQuantity, Item item) {
        this(minQuantity, maxQuantity, item, null);
    }

    public CollectObjective(int minQuantity, int maxQuantity, Item item, ResourceKey<LootTable> lootTable) {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.item = item;
        this.lootTable = lootTable;
    }

    @Override
    public MapCodec<? extends ObjectiveType> getCodec() {
        return CODEC;
    }

    @Override
    public OngoingObjective generate(LevelAccessor level) {
        int target = level.getRandom().nextIntBetweenInclusive(minQuantity, maxQuantity);

        if (lootTable != null && level instanceof ServerLevel sl) {
            Item picked = CollectOngoingObjective.pickItemFromLootOrThrow(sl, lootTable);
            return new CollectOngoingObjective(target, picked);
        }
        return new CollectOngoingObjective(target, item != null ? item : Items.IRON_INGOT);
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public Item getItem() {
        return item;
    }

    public ResourceKey<LootTable> getLootTable() {
        return lootTable;
    }
}
