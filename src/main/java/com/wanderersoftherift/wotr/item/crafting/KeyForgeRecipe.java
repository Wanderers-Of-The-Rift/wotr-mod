package com.wanderersoftherift.wotr.item.crafting;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.codec.DispatchedPair;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.recipe.WotrRecipeCategories;
import com.wanderersoftherift.wotr.init.recipe.WotrRecipeSerializers;
import com.wanderersoftherift.wotr.init.recipe.WotrRecipeTypes;
import com.wanderersoftherift.wotr.item.crafting.display.EssenceSlotDisplay;
import com.wanderersoftherift.wotr.item.crafting.display.KeyForgeRecipeDisplay;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recipe for the Key Forge.
 */
public final class KeyForgeRecipe implements Recipe<EssenceRecipeInput> {

    private final int priority;
    private final List<EssencePredicate> essenceRequirements;
    private final List<Ingredient> itemRequirements;

    private final DataComponentApplier<?> output;

    private KeyForgeRecipe(DataComponentApplier<?> output, int priority, List<EssencePredicate> essenceRequirements,
            List<Ingredient> itemRequirements) {
        this.output = output;
        this.priority = priority;
        this.essenceRequirements = essenceRequirements;
        this.itemRequirements = itemRequirements;
    }

    public static MapCodec<KeyForgeRecipe> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                DataComponentApplier.codec().fieldOf("output").forGetter(x -> (DataComponentApplier) x.output),
                Codec.INT.fieldOf("priority").forGetter(KeyForgeRecipe::getPriority),
                EssencePredicate.CODEC.listOf()
                        .optionalFieldOf("essence_reqs", List.of())
                        .forGetter(KeyForgeRecipe::getEssenceRequirements),
                Ingredient.CODEC.listOf()
                        .optionalFieldOf("item_reqs", List.of())
                        .forGetter(KeyForgeRecipe::getItemRequirements)
        ).apply(instance, KeyForgeRecipe::new));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, KeyForgeRecipe> streamCodec() {
        return StreamCodec.composite(
                DataComponentApplier.streamCodec(), KeyForgeRecipe::getOutputObject, ByteBufCodecs.INT,
                KeyForgeRecipe::getPriority, EssencePredicate.STREAM_CODEC.apply(ByteBufCodecs.list()),
                KeyForgeRecipe::getEssenceRequirements, Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                KeyForgeRecipe::getItemRequirements, KeyForgeRecipe::new
        );
    }

    public static <T> Builder<T> create(DataComponentType<T> outputType, T output) {
        return new Builder<>(outputType, output);
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<EssenceRecipeInput>> getType() {
        return WotrRecipeTypes.KEY_FORGE_RECIPE.get();
    }

    /**
     * @return The priority of this recipe. In the presence of multiple recipes that are valid for a given output type
     *         the higher priority one will be used
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @return All the essence requirements for this recipe
     */
    public List<EssencePredicate> getEssenceRequirements() {
        return Collections.unmodifiableList(essenceRequirements);
    }

    /**
     * @return All the item requirements for this recipe
     */
    public List<Ingredient> getItemRequirements() {
        return Collections.unmodifiableList(itemRequirements);
    }

    /**
     * @return The DataComponentType this recipe applies
     */
    public DataComponentType<?> getOutputType() {
        return output.type;
    }

    /**
     * @param stack The stack to apply the output of this recipe to
     */
    public void apply(ItemStack stack) {
        output.apply(stack);
    }

    /**
     * @return An example output for display purposes
     */
    public ItemStack getExampleOutput() {
        ItemStack result = WotrItems.RIFT_KEY.toStack();
        apply(result);
        return result;
    }

    @Override
    public boolean matches(@NotNull EssenceRecipeInput input, @NotNull Level level) {
        for (EssencePredicate essenceReq : essenceRequirements) {
            if (!essenceReq.match(input)) {
                return false;
            }
        }
        for (Ingredient itemReq : itemRequirements) {
            boolean met = false;
            for (int i = 0; i < input.size(); i++) {
                ItemStack item = input.getItem(i);
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

    private DataComponentApplier<?> getOutputObject() {
        return output;
    }

    ///
    /// Recipe Display related methods
    ///

    @Override
    public @NotNull List<RecipeDisplay> display() {
        ItemStack result = getExampleOutput();
        return List.of(
                new KeyForgeRecipeDisplay(
                        essenceRequirements.stream().<SlotDisplay>map(EssenceSlotDisplay::new).toList(),
                        new SlotDisplay.ItemStackSlotDisplay(result),
                        new SlotDisplay.ItemSlotDisplay(WotrBlocks.KEY_FORGE.asItem()))
        );
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull EssenceRecipeInput input, @NotNull HolderLookup.Provider registries) {
        return getExampleOutput();
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<EssenceRecipeInput>> getSerializer() {
        return WotrRecipeSerializers.KEY_FORGE_RECIPE.get();
    }

    @Override
    public boolean isSpecial() {
        // This excludes the recipe from Minecraft's recipe book
        return true;
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        // This prevents ingredients being addable into the key forge from the recipe display
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return WotrRecipeCategories.KEYFORGE_RECIPE.get();
    }

    public static final class Builder<T> implements RecipeBuilder {
        private final DataComponentType<T> resultType;
        private final T result;
        private int priority = 0;

        private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
        private String group;
        private ResourceLocation id;

        private final List<EssencePredicate> essenceRequirements = new ArrayList<>();
        private final List<Ingredient> itemRequirements = new ArrayList<>();

        /**
         * @param output The output this recipe produces
         */
        private Builder(DataComponentType<T> outputType, T output) {
            this.resultType = outputType;
            this.result = output;
        }

        /**
         * @param priority The priority of this recipe against other recipes (higher overrides lower)
         * @return
         */
        public Builder<T> setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder<T> setId(ResourceLocation id) {
            this.id = id;
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
        public Builder<T> withItemReq(Ingredient itemReq) {
            this.itemRequirements.add(itemReq);
            return this;
        }

        public KeyForgeRecipe build() {
            return new KeyForgeRecipe(new DataComponentApplier<T>(resultType, result), priority, essenceRequirements,
                    itemRequirements);
        }

        @Override
        public @NotNull RecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
            criteria.put(name, criterion);
            return this;
        }

        @Override
        public RecipeBuilder group(@Nullable String groupName) {
            group = groupName;
            return this;
        }

        @Override
        public Item getResult() {
            return null;
        }

        @Override
        public void save(@NotNull RecipeOutput recipeOutput) {
            if (result instanceof Holder<?> holder) {
                this.save(recipeOutput,
                        ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(holder.getRegisteredName())));
                return;
            }
            throw new IllegalStateException("Id needs to be explicitly provided");
        }

        @Override
        public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
            ResourceLocation explicitId = ResourceLocation.parse(id);
            if (result instanceof Holder<?> holder
                    && ResourceLocation.parse(holder.getRegisteredName()).equals(explicitId)) {
                throw new IllegalStateException(
                        "Recipe " + id + " should remove its 'save' argument as it is equal to default one");
            }
            this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, explicitId));
        }

        public void save(@NotNull RecipeOutput output, @NotNull ResourceLocation id) {
            save(output, ResourceKey.create(Registries.RECIPE, id));
        }

        @Override
        public void save(@NotNull RecipeOutput output, @NotNull ResourceKey<Recipe<?>> key) {
            AdvancementHolder advancement = null;
            if (!criteria.isEmpty()) {
                Advancement.Builder builder = output.advancement()
                        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
                        .rewards(AdvancementRewards.Builder.recipe(key))
                        .requirements(AdvancementRequirements.Strategy.OR);
                criteria.forEach(builder::addCriterion);
                advancement = builder.build(key.location().withPrefix("recipes/"));
            }
            KeyForgeRecipe recipe = new KeyForgeRecipe(new DataComponentApplier<T>(resultType, result), priority,
                    essenceRequirements, itemRequirements);
            output.accept(key, recipe, advancement);
        }
    }

    /**
     * Serializer for this recipe
     */
    public static class Serializer implements RecipeSerializer<KeyForgeRecipe> {

        @Override
        public @NotNull MapCodec<KeyForgeRecipe> codec() {
            return KeyForgeRecipe.codec();
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, KeyForgeRecipe> streamCodec() {
            return KeyForgeRecipe.streamCodec();
        }
    }

    /**
     * DataComponentApplier captures both the type and value of a data component, to be applied as a recipe output
     * 
     * @param type
     * @param value
     * @param <U>
     */
    private record DataComponentApplier<U>(DataComponentType<U> type, U value) {

        @SuppressWarnings("unchecked")
        public static <V> Codec<DataComponentApplier<V>> codec() {
            return new DispatchedPair<>(DataComponentType.CODEC.fieldOf("type").codec(), "value",
                    (x) -> (Codec<V>) x.codec())
                    .xmap(x -> new DataComponentApplier<>((DataComponentType<V>) x.getFirst(), x.getSecond()),
                            x -> Pair.of(x.type, x.value));
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public static StreamCodec<RegistryFriendlyByteBuf, DataComponentApplier<?>> streamCodec() {
            return new StreamCodec<>() {

                @Override
                public @NotNull KeyForgeRecipe.DataComponentApplier<?> decode(@NotNull RegistryFriendlyByteBuf buffer) {
                    DataComponentType<?> type = DataComponentType.STREAM_CODEC.decode(buffer);
                    Object value = type.streamCodec().decode(buffer);
                    return new DataComponentApplier(type, value);
                }

                @Override
                public void encode(
                        @NotNull RegistryFriendlyByteBuf buffer,
                        @NotNull KeyForgeRecipe.DataComponentApplier value) {
                    DataComponentType.STREAM_CODEC.encode(buffer, value.type);
                    value.type.streamCodec().encode(buffer, value.value);
                }
            };
        }

        /**
         * Apply this data component to the given holder
         * 
         * @param holder
         */
        public void apply(MutableDataComponentHolder holder) {
            holder.set(type, value);
        }
    }
}
