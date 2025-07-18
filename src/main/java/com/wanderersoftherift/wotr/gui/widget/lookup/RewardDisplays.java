package com.wanderersoftherift.wotr.gui.widget.lookup;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.components.AbstractWidget;
import net.neoforged.fml.ModLoader;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class RewardDisplays {

    private static final Map<Class<?>, Function<?, AbstractWidget>> lookup = Maps.newHashMap();

    private RewardDisplays() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<AbstractWidget> createFor(T object) {
        Function<?, AbstractWidget> creator = lookup.get(object.getClass());
        if (creator != null) {
            return Optional.of(((Function<T, AbstractWidget>) creator).apply(object));
        }
        return Optional.empty();
    }

    public static void init() {
        RegisterRewardDisplaysEvent event = new RegisterRewardDisplaysEvent(lookup);
        ModLoader.postEvent(event);
    }
}
