package com.wanderersoftherift.wotr.interop.rei.client;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.item.crafting.display.KeyForgeRecipeDisplay;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * REI Display for a Key Forge recipe. This captures a KeyForge recipe for display in REI.
 */
public class RuneAnvilDisplay extends BasicDisplay {

    public static final DisplaySerializer<RuneAnvilDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(RuneAnvilDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(RuneAnvilDisplay::getOutputEntries),
                    ResourceLocation.CODEC.optionalFieldOf("id").forGetter(RuneAnvilDisplay::getDisplayLocation)
            ).apply(instance, RuneAnvilDisplay::new)), StreamCodec.composite(
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()), RuneAnvilDisplay::getInputEntries,
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()), RuneAnvilDisplay::getOutputEntries,
                    ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), RuneAnvilDisplay::getDisplayLocation,
                    RuneAnvilDisplay::new
            ), false);

    public RuneAnvilDisplay(KeyForgeRecipeDisplay display, Optional<RecipeDisplayId> id) {
        super(EntryIngredients.ofSlotDisplays(display.requirements()),
                List.of(EntryIngredients.ofSlotDisplay(display.result())));
    }

    public RuneAnvilDisplay(RunegemData data, ResourceLocation id) {
        super(List.of(EntryIngredients.of(createRunegem(data))), List.of(EntryIngredients.of(createRunegem(data))),
                Optional.ofNullable(id));
    }

    public RuneAnvilDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs,
            Optional<ResourceLocation> id) {
        super(inputs, outputs, id);
    }

    private static ItemStack createRunegem(RunegemData data) {
        ItemStack runegem = WotrItems.RUNEGEM.toStack();
        runegem.set(WotrDataComponentType.RUNEGEM_DATA, data);
        return runegem;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return WotrDisplayCategories.RUNE_ANVIL;
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
