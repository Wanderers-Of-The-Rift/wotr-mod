package com.wanderersoftherift.wotr.core.guild.quest.goal.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import com.wanderersoftherift.wotr.core.guild.quest.GoalProvider;
import com.wanderersoftherift.wotr.core.guild.quest.goal.CompleteRiftGoal;
import com.wanderersoftherift.wotr.core.guild.quest.goal.RiftCompletionLevel;
import com.wanderersoftherift.wotr.core.guild.quest.goal.RiftPredicate;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.List;
import java.util.Optional;

public record CompleteRiftGoalProvider(RiftCompletionLevel completionLevel, Optional<NumberProvider> tier,
        List<Holder<RiftTheme>> themes, List<Holder<ObjectiveType>> objectives, NumberProvider count)
        implements GoalProvider {

    public static final MapCodec<CompleteRiftGoalProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    RiftCompletionLevel.CODEC.optionalFieldOf("completionLevel", RiftCompletionLevel.COMPLETE)
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
    public List<Goal> generateGoal(LootParams params) {
        LootContext context = new LootContext.Builder(params).create(Optional.empty());
        Optional<Integer> finalTier = tier.map(provider -> provider.getInt(context));
        Optional<Holder<RiftTheme>> theme;
        if (themes.isEmpty()) {
            theme = Optional.empty();
        } else {
            theme = Optional.of(themes.get(context.getRandom().nextInt(themes.size())));
        }
        Optional<Holder<ObjectiveType>> objective;
        if (objectives.isEmpty()) {
            objective = Optional.empty();
        } else {
            objective = Optional.of(objectives.get(context.getRandom().nextInt(objectives.size())));
        }
        return List.of(new CompleteRiftGoal(count.getInt(context), completionLevel,
                new RiftPredicate(finalTier, theme, objective)));
    }
}
