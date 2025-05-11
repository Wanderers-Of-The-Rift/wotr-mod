package com.wanderersoftherift.wotr.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModLootItemConditionTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Optional;

/**
 * A LootItemCondition that checks if the loottable contains a specific namespace and/or path fragment.
 */
public record PartialLootTableIdCondition(Optional<String> nameSpaceFragment, Optional<String> pathFragment)
        implements LootItemCondition {
    public static final MapCodec<PartialLootTableIdCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.optionalFieldOf("namespace").forGetter(PartialLootTableIdCondition::nameSpaceFragment),
                    Codec.STRING.optionalFieldOf("path").forGetter(PartialLootTableIdCondition::pathFragment)
            ).apply(instance, PartialLootTableIdCondition::new));

    @Override
    public LootItemConditionType getType() {
        return ModLootItemConditionTypes.PARTIAL_LOOT_TABLE_ID.get();
    }

    public boolean test(LootContext context) {
        if (nameSpaceFragment.isPresent() && pathFragment.isPresent()) {
            ResourceLocation tableId = context.getQueriedLootTableId();
            return tableId.getNamespace().contains(nameSpaceFragment.get())
                    && tableId.getPath().contains(pathFragment.get());
        } else if (nameSpaceFragment.isPresent()) {
            return context.getQueriedLootTableId().getNamespace().contains(nameSpaceFragment.get());
        } else if (pathFragment.isPresent()) {
            return context.getQueriedLootTableId().getPath().contains(pathFragment.get());
        }
        return false;
    }

    public static PartialLootTableIdCondition.Builder partialLootTableId() {
        return new PartialLootTableIdCondition.Builder();
    }

    public static PartialLootTableIdCondition.Builder partialLootTableId(
            String nameSpaceFragment,
            String pathFragment) {
        return new PartialLootTableIdCondition.Builder().nameSpaceFragment(nameSpaceFragment)
                .pathFragment(pathFragment);
    }

    public static class Builder implements LootItemCondition.Builder {
        private Optional<String> nameSpaceFragment = Optional.empty();
        private Optional<String> pathFragment = Optional.empty();

        public Builder nameSpaceFragment(String nameSpaceFragment) {
            this.nameSpaceFragment = Optional.of(nameSpaceFragment);
            return this;
        }

        public Builder pathFragment(String pathFragment) {
            this.pathFragment = Optional.of(pathFragment);
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new PartialLootTableIdCondition(nameSpaceFragment, pathFragment);
        }
    }
}
