package com.wanderersoftherift.wotr.core.guild.quest.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * This goal requires items to be handed in to complete.
 * 
 * @param item     The item (or set of items) that the quest requires
 * @param quantity How many total items need to be provided
 */
public record GiveItemGoal(Ingredient item, int quantity) implements Goal {

    public static final MapCodec<GiveItemGoal> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.CODEC.fieldOf("item").forGetter(GiveItemGoal::item),
                    Codec.INT.optionalFieldOf("quantity", 1).forGetter(GiveItemGoal::progressTarget)
            ).apply(instance, GiveItemGoal::new));

    @Override
    public MapCodec<? extends Goal> getCodec() {
        return CODEC;
    }

    @Override
    public int progressTarget() {
        return quantity;
    }

}
