package com.wanderersoftherift.wotr.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * EssencePredicate provides the ability to filter on a map of essence type to value, for use as a recipe requirement
 *
 * @param essenceType The type of essence being tested
 * @param min         The minimum amount required
 * @param max         The maximum amount allowed
 * @param minPercent  The minimum percent of available essence that must be of this type
 * @param maxPercent  The maximum percent of available essence that must be of this type
 */
public record EssencePredicate(ResourceLocation essenceType, int min, int max, float minPercent, float maxPercent) {

    public static final Codec<EssencePredicate> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ResourceLocation.CODEC.fieldOf("essenceType").forGetter(x -> x.essenceType),
                    Codec.INT.optionalFieldOf("min", 0).forGetter(x -> x.min),
                    Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(x -> x.max),
                    Codec.FLOAT.optionalFieldOf("minPercent", 0f).forGetter(x -> x.minPercent),
                    Codec.FLOAT.optionalFieldOf("maxPercent", 100f).forGetter(x -> x.maxPercent))
            .apply(instance, EssencePredicate::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EssencePredicate> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, EssencePredicate::essenceType, ByteBufCodecs.INT, EssencePredicate::min,
            ByteBufCodecs.INT, EssencePredicate::max, ByteBufCodecs.FLOAT, EssencePredicate::minPercent,
            ByteBufCodecs.FLOAT, EssencePredicate::maxPercent, EssencePredicate::new
    );

    /**
     * @param input
     * @return Whether the provided input satisfies this predicate
     */
    public boolean match(EssenceRecipeInput input) {
        int amount = input.getEssenceCount(essenceType);
        if (amount < min) {
            return false;
        }
        if (amount > max) {
            return false;
        }
        float percent = 100f * amount / input.getTotalEssence();
        if (percent < minPercent) {
            return false;
        }
        if (percent > maxPercent) {
            return false;
        }
        return true;
    }

    public static class Builder {
        private final ResourceLocation essenceType;
        private int min = 0;
        private int max = Integer.MAX_VALUE;
        private float minPercent = 0;
        private float maxPercent = 100f;

        /**
         * @param essenceType The type of essence to filter on
         */
        public Builder(ResourceLocation essenceType) {
            this.essenceType = essenceType;
        }

        /**
         * @param min The minimum quantity of the essence type required
         * @return this builder for method chaining
         */
        public Builder setMin(int min) {
            this.min = min;
            return this;
        }

        /**
         * @param max The maximum quantity of the essence type allowed
         * @return
         */
        public Builder setMax(int max) {
            this.max = max;
            return this;
        }

        /**
         * @param min The minimum percentage of the essence type required
         * @return
         */
        public Builder setMinPercent(float min) {
            this.minPercent = min;
            return this;
        }

        /**
         * @param max The maximum percentage of the essence type allowed
         * @return
         */
        public Builder setMaxPercent(float max) {
            this.maxPercent = max;
            return this;
        }

        public EssencePredicate build() {
            return new EssencePredicate(essenceType, min, max, minPercent, maxPercent);
        }

    }
}
