package com.wanderersoftherift.wotr.core.quest.goal.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Goal;
import com.wanderersoftherift.wotr.core.quest.GoalProvider;
import com.wanderersoftherift.wotr.core.quest.goal.CompleteRiftGoal;
import com.wanderersoftherift.wotr.core.quest.goal.RiftCompletionLevel;
import com.wanderersoftherift.wotr.core.rift.predicate.RiftObjectivePredicate;
import com.wanderersoftherift.wotr.core.rift.predicate.RiftPredicate;
import com.wanderersoftherift.wotr.core.rift.predicate.RiftThemePredicate;
import com.wanderersoftherift.wotr.core.rift.predicate.RiftTierPredicate;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Provider for generating CompleteRiftGoals.
 *
 * @param completionLevel The completion level required
 * @param tier            A number provider for the range of tier to generate
 * @param themes          A list of possible themes requirements, which may be empty for no theme required
 * @param objectives      A list of possible objective requirements, which may be empty for no objective required
 * @param count           A number provider for the number of rifts required to be completed
 */
public record CompleteRiftGoalProvider(RiftCompletionLevel completionLevel, Optional<NumberProvider> tier,
        List<Holder<RiftTheme>> themes, List<Holder<ObjectiveType>> objectives, NumberProvider count)
        implements GoalProvider {

    public static final MapCodec<CompleteRiftGoalProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    RiftCompletionLevel.CODEC.optionalFieldOf("completion_level", RiftCompletionLevel.COMPLETE)
                            .forGetter(CompleteRiftGoalProvider::completionLevel),
                    NumberProviders.CODEC.optionalFieldOf("tier").forGetter(CompleteRiftGoalProvider::tier),
                    RiftTheme.CODEC.listOf()
                            .optionalFieldOf("themes", List.of())
                            .forGetter(CompleteRiftGoalProvider::themes),
                    ObjectiveType.CODEC.listOf()
                            .optionalFieldOf("objectives", List.of())
                            .forGetter(CompleteRiftGoalProvider::objectives),
                    NumberProviders.CODEC.fieldOf("count").forGetter(CompleteRiftGoalProvider::count)
            ).apply(instance, CompleteRiftGoalProvider::new));

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Goal> generateGoal(LootParams params) {
        LootContext context = new LootContext.Builder(params).create(Optional.empty());
        Optional<RiftTierPredicate> finalTier = tier.map(provider -> new RiftTierPredicate(provider.getInt(context)));
        Optional<RiftThemePredicate> theme;
        if (themes.isEmpty()) {
            theme = Optional.empty();
        } else {
            theme = Optional.of(new RiftThemePredicate(themes.get(context.getRandom().nextInt(themes.size()))));
        }
        Optional<RiftObjectivePredicate> objective;
        if (objectives.isEmpty()) {
            objective = Optional.empty();
        } else {
            objective = Optional
                    .of(new RiftObjectivePredicate(objectives.get(context.getRandom().nextInt(objectives.size()))));
        }
        return List.of(new CompleteRiftGoal(count.getInt(context), completionLevel,
                new RiftPredicate(finalTier, theme, objective)));
    }
}
