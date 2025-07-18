package com.wanderersoftherift.wotr.gui.widget.lookup;

import com.google.common.collect.Maps;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import com.wanderersoftherift.wotr.gui.widget.GoalDisplay;
import net.neoforged.fml.ModLoader;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class GoalDisplays {

    private static final Map<Class<?>, Function<?, GoalDisplay>> lookup = Maps.newHashMap();

    private GoalDisplays() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Goal> Optional<GoalDisplay> createFor(T goal) {
        Function<?, GoalDisplay> creator = lookup.get(goal.getClass());
        if (creator != null) {
            return Optional.of(((Function<T, GoalDisplay>) creator).apply(goal));
        }
        return Optional.empty();
    }

    public static void init() {
        RegisterGoalDisplaysEvent event = new RegisterGoalDisplaysEvent(lookup);
        ModLoader.postEvent(event);
    }
}
