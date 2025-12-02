package com.wanderersoftherift.wotr.core.goal.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalProvider;
import com.wanderersoftherift.wotr.core.goal.type.KillMobGoal;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * A provider for kill mob goals.
 *
 * @param mob      The type of mob. If empty, any mob.
 * @param rawLabel The translation string for the type of mob.
 * @param count    A number provider for the amount of mobs that must be killed
 */
public record KillMobGoalProvider(Optional<EntityTypePredicate> mob, String rawLabel, NumberProvider count)
        implements GoalProvider {

    public static final MapCodec<KillMobGoalProvider> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    EntityTypePredicate.CODEC.optionalFieldOf("mob").forGetter(KillMobGoalProvider::mob),
                    Codec.STRING.optionalFieldOf("mob_label", WanderersOfTheRift.translationId("goal", "mobs"))
                            .forGetter(KillMobGoalProvider::rawLabel),
                    NumberProviders.CODEC.fieldOf("count").forGetter(KillMobGoalProvider::count)
            ).apply(instance, KillMobGoalProvider::new));

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Goal> generateGoal(LootParams params) {
        return List.of(
                new KillMobGoal(mob, rawLabel, count.getInt(new LootContext.Builder(params).create(Optional.empty()))));
    }
}
