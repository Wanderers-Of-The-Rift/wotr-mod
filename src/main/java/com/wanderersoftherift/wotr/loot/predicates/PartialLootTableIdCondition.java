package com.wanderersoftherift.wotr.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.loot.WotrLootItemConditionTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A LootItemCondition that checks if the loottable contains a specific namespace and/or path fragment.
 */
public record PartialLootTableIdCondition(Optional<String> nameSpaceFragment,
        PartialLootTableIdCheckType nameSpaceCheckType, Optional<String> pathFragment,
        PartialLootTableIdCheckType pathCheckType) implements LootItemCondition {

    public static final MapCodec<PartialLootTableIdCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.optionalFieldOf("namespace").forGetter(PartialLootTableIdCondition::nameSpaceFragment),
                    PartialLootTableIdCheckType.CODEC
                            .optionalFieldOf("namespace_check_type", PartialLootTableIdCheckType.EQUALS)
                            .forGetter(PartialLootTableIdCondition::nameSpaceCheckType),
                    Codec.STRING.optionalFieldOf("path").forGetter(PartialLootTableIdCondition::pathFragment),
                    PartialLootTableIdCheckType.CODEC
                            .optionalFieldOf("path_check_type", PartialLootTableIdCheckType.STARTS_WITH)
                            .forGetter(PartialLootTableIdCondition::pathCheckType)
            ).apply(instance, PartialLootTableIdCondition::new));

    @Override
    public LootItemConditionType getType() {
        return WotrLootItemConditionTypes.PARTIAL_LOOT_TABLE_ID.get();
    }

    public boolean test(LootContext context) {
        ResourceLocation tableId = context.getQueriedLootTableId();
        return checkNamespaceFragment(tableId) && checkPathFragment(tableId);
    }

    private boolean checkNamespaceFragment(ResourceLocation tableId) {
        return nameSpaceFragment.map(s -> nameSpaceCheckType.apply(tableId.getNamespace(), s)).orElse(true);
    }

    private boolean checkPathFragment(ResourceLocation tableId) {
        return pathFragment.map(s -> pathCheckType.apply(tableId.getPath(), s)).orElse(true);
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
        private PartialLootTableIdCheckType nameSpaceCheckType = PartialLootTableIdCheckType.STARTS_WITH;
        private Optional<String> pathFragment = Optional.empty();
        private PartialLootTableIdCheckType pathCheckType = PartialLootTableIdCheckType.STARTS_WITH;

        public Builder nameSpaceFragment(String nameSpaceFragment) {
            this.nameSpaceFragment = Optional.of(nameSpaceFragment);
            return this;
        }

        public Builder pathFragment(String pathFragment) {
            this.pathFragment = Optional.of(pathFragment);
            return this;
        }

        public Builder nameSpaceCheckType(PartialLootTableIdCheckType nameSpaceCheckType) {
            this.nameSpaceCheckType = nameSpaceCheckType;
            return this;
        }

        public Builder pathCheckType(PartialLootTableIdCheckType pathCheckType) {
            this.pathCheckType = pathCheckType;
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new PartialLootTableIdCondition(nameSpaceFragment, nameSpaceCheckType, pathFragment, pathCheckType);
        }
    }

    public enum PartialLootTableIdCheckType {
        STARTS_WITH("starts_with", String::startsWith),
        CONTAINS("contains", String::contains),
        ENDS_WITH("ends_with", String::endsWith),
        EQUALS("equals", String::equals);

        public static final Codec<PartialLootTableIdCheckType> CODEC = Codec.STRING
                .flatComapMap(s -> PartialLootTableIdCheckType.byName(s, null), d -> DataResult.success(d.getName()));

        private final String name;
        private final BiFunction<String, String, Boolean> checkFunction;

        PartialLootTableIdCheckType(String name, BiFunction<String, String, Boolean> checkFunction) {
            this.name = name;
            this.checkFunction = checkFunction;
        }

        public String getName() {
            return name;
        }

        public BiFunction<String, String, Boolean> getCheckFunction() {
            return checkFunction;
        }

        public boolean apply(String str, String fragment) {
            return checkFunction.apply(str, fragment);
        }

        public static PartialLootTableIdCheckType byName(String name, PartialLootTableIdCheckType defaultReturn) {
            for (PartialLootTableIdCheckType value : values()) {
                if (value.name.equalsIgnoreCase(name)) {
                    return value;
                }
            }
            return defaultReturn;
        }
    }
}
