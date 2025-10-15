package com.wanderersoftherift.wotr.rift.objective.definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftConfigDataTypes;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.CollectOngoingObjective;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record CollectObjective(Holder<RiftParameter> quantity, Item item, ResourceKey<LootTable> lootTable)
        implements ObjectiveType {

    public static final MapCodec<CollectObjective> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RiftParameter.HOLDER_CODEC.fieldOf("quantity").forGetter(CollectObjective::quantity),
            BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item").forGetter(o -> Optional.ofNullable(o.item())),
            ResourceKey.codec(Registries.LOOT_TABLE)
                    .optionalFieldOf("loot_table")
                    .forGetter(o -> Optional.ofNullable(o.lootTable()))
    ).apply(inst, (q, optItem, optLoot) -> new CollectObjective(q, optItem.orElse(null), optLoot.orElse(null))));

    @Override
    public MapCodec<? extends ObjectiveType> getCodec() {
        return CODEC;
    }

    @Override
    public OngoingObjective generate(ServerLevelAccessor level, RiftConfig config) {
        final var params = config.getCustomData(WotrRiftConfigDataTypes.INITIAL_RIFT_PARAMETERS);
        final var key = quantity.getKey();
        final var inst = params.getParameter(key);
        final int rolls = Math.max(1, (inst == null) ? 1 : (int) Math.round(inst.get()));

        if (lootTable != null) {
            ItemAndCount ic = pickItemAndCountFromLoot(level.getLevel(), lootTable, rolls);
            return new CollectOngoingObjective(ic.count(), ic.item());
        }
        Item picked;

        if (item != null) {
            picked = item;
        } else {
            picked = Items.IRON_INGOT;
        }
        return new CollectOngoingObjective(rolls, picked);
    }

    private static ItemAndCount pickItemAndCountFromLoot(
            ServerLevel level,
            ResourceKey<LootTable> tableKey,
            int rolls) {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(tableKey);

        LootParams lootParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(BlockPos.ZERO))
                .withLuck(0.0f)
                .create(LootContextParamSets.CHEST);

        Map<Item, Integer> counts = new LinkedHashMap<>();
        var rng = level.getRandom();

        for (int i = 0; i < rolls; i++) {
            List<ItemStack> roll = table.getRandomItems(lootParams, rng.nextLong());
            for (ItemStack s : roll) {
                if (!s.isEmpty() && s.getMaxStackSize() > 1) {
                    counts.merge(s.getItem(), s.getCount(), Integer::sum);
                }
            }
        }

        if (!counts.isEmpty()) {
            int idx = rng.nextInt(counts.size());
            Item picked = counts.keySet()
                    .stream()
                    .skip(idx)
                    .findFirst()
                    .orElseGet(() -> counts.keySet().iterator().next());
            int total = Math.max(1, counts.getOrDefault(picked, 1));
            return new ItemAndCount(picked, total);
        }

        List<ItemStack> fallback = table.getRandomItems(lootParams, rng.nextLong());
        for (ItemStack s : fallback) {
            if (!s.isEmpty()) {
                int total = Math.max(1, s.getCount());
                return new ItemAndCount(s.getItem(), total);
            }
        }

        return new ItemAndCount(Items.IRON_INGOT, Math.max(1, rolls));
    }

    private record ItemAndCount(Item item, int count) {
    }
}
