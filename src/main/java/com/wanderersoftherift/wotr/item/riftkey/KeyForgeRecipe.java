package com.wanderersoftherift.wotr.item.riftkey;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.codec.DispatchedPair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class KeyForgeRecipe {
    private final int priority;
    private final List<EssencePredicate> essenceRequirements;
    private final List<ItemPredicate> itemRequirements;

    private final Output<?> output;

    private KeyForgeRecipe(Output<?> output, int priority, List<EssencePredicate> essenceRequirements,
            List<ItemPredicate> itemRequirements) {
        this.output = output;
        this.priority = priority;
        this.essenceRequirements = essenceRequirements;
        this.itemRequirements = itemRequirements;
    }

    public static Codec<KeyForgeRecipe> codec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Output.codec().fieldOf("output").forGetter(x -> (Output) x.output),
                Codec.INT.fieldOf("priority").forGetter(KeyForgeRecipe::getPriority),
                EssencePredicate.CODEC.listOf()
                        .optionalFieldOf("essence_reqs", List.of())
                        .forGetter(KeyForgeRecipe::getEssenceRequirements),
                ItemPredicate.CODEC.listOf()
                        .optionalFieldOf("item_reqs", List.of())
                        .forGetter(KeyForgeRecipe::getItemRequirements))
                .apply(instance, KeyForgeRecipe::new));
    }

    public static <T> Builder<T> create(DataComponentType<T> outputType, T output) {
        return new Builder<>(outputType, output);
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

    public DataComponentType<?> getOutputType() {
        return output.type;
    }

    public Object getOutput() {
        return output.value;
    }

    public void apply(ItemStack stack) {
        output.apply(stack);
    }

    public static final class Builder<T> {
        private final DataComponentType<T> outputType;
        private final T output;
        private int priority = 0;

        private final List<EssencePredicate> essenceRequirements = new ArrayList<>();
        private final List<ItemPredicate> itemRequirements = new ArrayList<>();

        /**
         * @param output The output this recipe produces
         */
        private Builder(DataComponentType<T> outputType, T output) {
            this.outputType = outputType;
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

        public KeyForgeRecipe build() {
            return new KeyForgeRecipe(new Output<T>(outputType, output), priority, essenceRequirements,
                    itemRequirements);
        }

    }

    private record Output<U>(DataComponentType<U> type, U value) {

        @SuppressWarnings("unchecked")
        public static <V> Codec<Output<V>> codec() {
            return new DispatchedPair<>(DataComponentType.CODEC.fieldOf("type").codec(), "value",
                    (x) -> (Codec<V>) x.codec())
                    .xmap(x -> new Output<>((DataComponentType<V>) x.getFirst(), x.getSecond()),
                            x -> Pair.of(x.type, x.value));
        }

        public void apply(ItemStack stack) {
            stack.set(type, value);
        }
    }
}
