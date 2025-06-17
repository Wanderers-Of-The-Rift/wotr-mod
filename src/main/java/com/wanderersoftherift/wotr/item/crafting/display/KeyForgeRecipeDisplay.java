package com.wanderersoftherift.wotr.item.crafting.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.recipe.WotrRecipeDisplayTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * RecipeDisplay for a KeyForge recipe. This provides the data for rendering the recipe
 * 
 * @param requirements
 * @param result
 * @param craftingStation
 */
public record KeyForgeRecipeDisplay(List<SlotDisplay> requirements, SlotDisplay result, SlotDisplay craftingStation)
        implements RecipeDisplay {

    public static final MapCodec<KeyForgeRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    SlotDisplay.CODEC.listOf().fieldOf("requirements").forGetter(KeyForgeRecipeDisplay::requirements),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(KeyForgeRecipeDisplay::result),
                    SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(KeyForgeRecipeDisplay::craftingStation)
            ).apply(instance, KeyForgeRecipeDisplay::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, KeyForgeRecipeDisplay> STREAM_CODEC = StreamCodec
            .composite(
                    SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), KeyForgeRecipeDisplay::requirements,
                    SlotDisplay.STREAM_CODEC, KeyForgeRecipeDisplay::result, SlotDisplay.STREAM_CODEC,
                    KeyForgeRecipeDisplay::craftingStation, KeyForgeRecipeDisplay::new
            );

    @Override
    public @NotNull Type<? extends RecipeDisplay> type() {
        return WotrRecipeDisplayTypes.KEY_FORGE_RECIPE_DISPLAY.get();
    }
}
