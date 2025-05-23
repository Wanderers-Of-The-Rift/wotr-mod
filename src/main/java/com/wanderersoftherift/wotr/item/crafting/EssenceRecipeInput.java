package com.wanderersoftherift.wotr.item.crafting;

import com.wanderersoftherift.wotr.init.WotrDataMaps;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Recipe input for crafting involving essence
 */
public class EssenceRecipeInput implements RecipeInput {

    private final List<ItemStack> items;
    private final int totalEssence;
    private final Object2IntMap<ResourceLocation> essenceCounts;

    /**
     * @param essenceSources The items providing essence for the recipe
     */
    public EssenceRecipeInput(List<ItemStack> essenceSources) {
        this.items = essenceSources;

        int totalEssence = 0;
        essenceCounts = new Object2IntArrayMap<>();
        for (ItemStack item : essenceSources) {
            if (item.isEmpty()) {
                continue;
            }

            EssenceValue valueMap = item.getItemHolder().getData(WotrDataMaps.ESSENCE_VALUE_DATA);
            if (valueMap == null) {
                continue;
            }
            for (Object2IntMap.Entry<ResourceLocation> entry : valueMap.values().object2IntEntrySet()) {
                essenceCounts.mergeInt(entry.getKey(), entry.getIntValue() * item.getCount(), Integer::sum);
                totalEssence += entry.getIntValue() * item.getCount();
            }
        }
        this.totalEssence = totalEssence;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }

    /**
     * @return The total amount of essence (of all types) provided by this input
     */
    public int getTotalEssence() {
        return totalEssence;
    }

    /**
     * @param essenceType
     * @return How much of a specific essence type is provided by this input
     */
    public int getEssenceCount(ResourceLocation essenceType) {
        return essenceCounts.getOrDefault(essenceType, 0);
    }

    /**
     * @param essenceType
     * @return The percentage of the input that is of the provided essence type
     */
    public float getEssencePercent(ResourceLocation essenceType) {
        return 100.f * essenceCounts.getOrDefault(essenceType, 0) / totalEssence;
    }

    /**
     * @return The full set of essence counts
     */
    public Object2IntMap<ResourceLocation> getEssenceCounts() {
        return Object2IntMaps.unmodifiable(essenceCounts);
    }
}
