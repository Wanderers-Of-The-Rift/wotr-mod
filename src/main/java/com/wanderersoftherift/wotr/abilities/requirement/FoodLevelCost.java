package com.wanderersoftherift.wotr.abilities.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public record FoodLevelCost(int amount) implements AbilityRequirement {
    public static final MapCodec<FoodLevelCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("amount", 0).forGetter(FoodLevelCost::amount)
    ).apply(instance, FoodLevelCost::new));

    @Override
    public MapCodec<? extends AbilityRequirement> getCodec() {
        return CODEC;
    }

    @Override
    public boolean check(AbilityContext context) {
        if (context.caster() instanceof Player player) {
            return player.getFoodData().getFoodLevel() > amount;
        }
        return false;
    }

    @Override
    public void pay(AbilityContext context) {
        if (context.caster() instanceof Player player) {
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
