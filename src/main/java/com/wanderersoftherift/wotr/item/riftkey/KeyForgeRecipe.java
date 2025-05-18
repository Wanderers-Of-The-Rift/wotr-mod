package com.wanderersoftherift.wotr.item.riftkey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class KeyForgeRecipe<T> {
    private final int priority;
    private final List<EssencePredicate> essenceRequirements;
    private final List<ItemPredicate> itemRequirements;

    private final T output;

    public KeyForgeRecipe(T output, int priority, List<EssencePredicate> essenceRequirements,
            List<ItemPredicate> itemRequirements) {
        this.output = output;
        this.priority = priority;
        this.essenceRequirements = essenceRequirements;
        this.itemRequirements = itemRequirements;
    }

    public static <T> Codec<KeyForgeRecipe<T>> codec(Codec<T> outputCodec) {
        return RecordCodecBuilder
                .create(instance -> instance
                        .group(outputCodec.fieldOf("output").forGetter(KeyForgeRecipe::getOutput),
                                Codec.INT.fieldOf("priority").forGetter(KeyForgeRecipe::getPriority),
                                EssencePredicate.CODEC.listOf()
                                        .optionalFieldOf("essence_reqs", List.of())
                                        .forGetter(KeyForgeRecipe::getEssenceRequirements),
                                ItemPredicate.CODEC.listOf()
                                        .optionalFieldOf("item_reqs", List.of())
                                        .forGetter(KeyForgeRecipe::getItemRequirements))
                        .apply(instance, KeyForgeRecipe::new));
    }

    public static <T> Builder<T> create(T output) {
        return new Builder<>(output);
    }

    public boolean matches(List<ItemStack> items, Object2IntMap<ResourceLocation> essences) {
        for (EssencePredicate essenceReq : essenceRequirements) {
            if (!essenceReq.match(essences)) {
                return false;
            }
        }
        for (ItemPredicate itemReq : itemRequirements) {
            boolean met = false;
            for (ItemStack item : items) {
                if (itemReq.test(item)) {
                    met = true;
                    break;
                }
            }
            if (!met) {
                return false;
            }
        }
        return true;
    }

    public int getPriority() {
        return priority;
    }

    public List<EssencePredicate> getEssenceRequirements() {
        return Collections.unmodifiableList(essenceRequirements);
    }

    public List<ItemPredicate> getItemRequirements() {
        return Collections.unmodifiableList(itemRequirements);
    }

    public T getOutput() {
        return output;
    }

    public static final class Builder<T> {
        private final T output;
        private int priority = 0;

        private final List<EssencePredicate> essenceRequirements = new ArrayList<>();
        private final List<ItemPredicate> itemRequirements = new ArrayList<>();

        /**
         * @param output The output this recipe produces
         */
        private Builder(T output) {
            this.output = output;
        }

        /**
         * @param priority The priority of this recipe against other recipes (higher overrides lower)
         * @return
         */
        public Builder<T> setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Can be specified multiple times, with all requirements needing to be met
         *
         * @param essenceReq A predicate specifying a requirement on a type of essence that has to be met by the
         *                   ingredients.
         * @return
         */
        public Builder<T> withEssenceReq(EssencePredicate essenceReq) {
            this.essenceRequirements.add(essenceReq);
            return this;
        }

        /**
         * Can be specified multiple times, with all requirements needing to be met
         *
         * @param itemReq A predicate specifying a requirement on a type of item that has to be met by the ingredients
         * @return
         */
        public Builder<T> withItemReq(ItemPredicate itemReq) {
            this.itemRequirements.add(itemReq);
            return this;
        }

        public KeyForgeRecipe<T> build() {
            return new KeyForgeRecipe<T>(output, priority, essenceRequirements, itemRequirements);
        }

    }
}
