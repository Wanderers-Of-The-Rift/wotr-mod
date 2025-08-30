package com.wanderersoftherift.wotr.abilities.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public record FoodLevelCost(int amount, boolean consume) implements AbilityRequirement {
    public static final MapCodec<FoodLevelCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("amount", 1).forGetter(FoodLevelCost::amount),
            Codec.BOOL.optionalFieldOf("consume", true).forGetter(FoodLevelCost::consume)
    ).apply(instance, FoodLevelCost::new));

    @Override
    public MapCodec<? extends AbilityRequirement> getCodec() {
        return CODEC;
    }

    @Override
    public boolean check(AbilityContext context) {
        if (context.caster() instanceof Player player) {
            return player.getFoodData().getFoodLevel() + player.getFoodData().getSaturationLevel() >= amount;
        }
        return false;
    }

    @Override
    public void pay(AbilityContext context) {
        if (consume && context.caster() instanceof Player player) {
            FoodData foodData = player.getFoodData();
            float remainingCost = amount;
            if (remainingCost > foodData.getSaturationLevel()) {
                foodData.setSaturation(foodData.getSaturationLevel() - remainingCost);
            } else {
                remainingCost -= foodData.getSaturationLevel();
                foodData.setSaturation(0);
            }
            if (remainingCost > 0) {
                foodData.setFoodLevel(Math.max(0, (int) (foodData.getFoodLevel() - remainingCost)));
            }
        }
    }
}
