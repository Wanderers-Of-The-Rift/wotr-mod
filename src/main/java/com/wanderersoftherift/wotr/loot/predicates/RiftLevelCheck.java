package com.wanderersoftherift.wotr.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModLootItemConditionTypes;
import com.wanderersoftherift.wotr.loot.LootUtil;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * A LootItemCondition that checks if the current rift level is between the min (inc) and max (inc) values.
 */
public record RiftLevelCheck(int minTier, int maxTier) implements LootItemCondition {
    public static final MapCodec<RiftLevelCheck> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf("min_tier").forGetter(RiftLevelCheck::minTier),
                    Codec.INT.fieldOf("max_tier").forGetter(RiftLevelCheck::maxTier)
            ).apply(instance, RiftLevelCheck::new));

    @Override
    public LootItemConditionType getType() {
        return ModLootItemConditionTypes.RIFT_LEVEL_CHECK.get();
    }

    public boolean test(LootContext context) {
        int riftTier = LootUtil.getRiftTierFromContext(context);
        return riftTier >= minTier && riftTier <= maxTier;
    }

    public static RiftLevelCheck.Builder riftTier() {
        return new RiftLevelCheck.Builder();
    }

    public static RiftLevelCheck.Builder riftTier(int min, int max) {
        return new RiftLevelCheck.Builder().min(min).max(max);
    }

    public static class Builder implements LootItemCondition.Builder {
        private int minTier = 0;
        private int maxTier = 0;

        public Builder min(int min) {
            this.minTier = min;
            return this;
        }

        public Builder max(int max) {
            this.maxTier = max;
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new RiftLevelCheck(minTier, maxTier);
        }
    }
}
