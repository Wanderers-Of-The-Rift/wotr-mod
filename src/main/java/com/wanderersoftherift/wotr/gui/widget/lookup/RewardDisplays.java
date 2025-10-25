package com.wanderersoftherift.wotr.gui.widget.lookup;

import com.google.common.collect.Maps;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.gui.widget.reward.RewardWidget;
import net.neoforged.fml.ModLoader;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class RewardDisplays {

    private static final Map<Class<?>, Function<? extends Reward, RewardWidget>> lookup = Maps.newHashMap();

    private RewardDisplays() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Reward> Optional<RewardWidget> createFor(T object) {
        Function<? extends Reward, RewardWidget> creator = lookup.get(object.getClass());
        if (creator != null) {
            return Optional.of(((Function<T, RewardWidget>) creator).apply(object));
        }
        return Optional.empty();
    }

    public static void init() {
        RegisterRewardDisplaysEvent event = new RegisterRewardDisplaysEvent(lookup);
        ModLoader.postEvent(event);
    }
}
