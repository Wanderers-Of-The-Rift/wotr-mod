package com.wanderersoftherift.wotr.core.goal.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalProvider;
import com.wanderersoftherift.wotr.core.goal.type.GiveItemGoal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A goal provider for generating GiveItem goals
 *
 * @param item  The type of item required
 * @param count A number provider for the count of items required
 */
public record GiveItemGoalProvider(Ingredient item, NumberProvider count) implements GoalProvider {

    public static final MapCodec<GiveItemGoalProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("item").forGetter(GiveItemGoalProvider::item),
            NumberProviders.CODEC.fieldOf("count").forGetter(GiveItemGoalProvider::count)
    ).apply(instance, GiveItemGoalProvider::new));

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Goal> generateGoal(LootContext context) {
        return List.of(new GiveItemGoal(item, count.getInt(context)));
    }
}
