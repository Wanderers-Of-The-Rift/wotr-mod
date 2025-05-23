package com.wanderersoftherift.wotr.interop.rei.client;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.interop.rei.common.WotrEntryTypes;
import com.wanderersoftherift.wotr.item.crafting.EssencePredicate;
import com.wanderersoftherift.wotr.item.crafting.KeyForgeRecipe;
import com.wanderersoftherift.wotr.item.crafting.display.KeyForgeRecipeDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * REI Display for a Key Forge recipe. This captures a KeyForge recipe for display in REI.
 */
public class KeyForgeDisplay extends BasicDisplay {

    public static final DisplaySerializer<KeyForgeDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(KeyForgeDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(KeyForgeDisplay::getOutputEntries),
                    ResourceLocation.CODEC.optionalFieldOf("id").forGetter(KeyForgeDisplay::getDisplayLocation)
            ).apply(instance, KeyForgeDisplay::new)), StreamCodec.composite(
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()), KeyForgeDisplay::getInputEntries,
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()), KeyForgeDisplay::getOutputEntries,
                    ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), KeyForgeDisplay::getDisplayLocation,
                    KeyForgeDisplay::new
            ), false);

    public KeyForgeDisplay(KeyForgeRecipeDisplay display, Optional<RecipeDisplayId> id) {
        super(EntryIngredients.ofSlotDisplays(display.requirements()),
                List.of(EntryIngredients.ofSlotDisplay(display.result())));
    }

    public KeyForgeDisplay(KeyForgeRecipe recipe, ResourceLocation id) {
        super(compileIngredients(recipe), Collections.singletonList(EntryIngredients.of(recipe.getExampleOutput())),
                Optional.ofNullable(id));
    }

    public KeyForgeDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> id) {
        super(inputs, outputs, id);
    }

    private static List<EntryIngredient> compileIngredients(KeyForgeRecipe recipe) {
        List<EntryIngredient> ingredients = new ArrayList<>(
                EntryIngredients.ofIngredients(recipe.getItemRequirements()));
        for (EssencePredicate essenceRequirement : recipe.getEssenceRequirements()) {
            ingredients.add(EntryIngredient.of(EntryStack.of(WotrEntryTypes.ESSENCE, essenceRequirement)));
        }

        return ingredients;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return WotrDisplayCategories.KEY_FORGE;
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public List<InputIngredient<EntryStack<?>>> getInputIngredients(
            @Nullable AbstractContainerMenu menu,
            @Nullable Player player) {
        return super.getInputIngredients(menu, player);
    }
}
