package com.wanderersoftherift.wotr.gui.widget.lookup;

import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.gui.widget.reward.RewardWidget;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.function.Function;

public class RegisterRewardDisplaysEvent extends Event implements IModBusEvent {

    private final Map<Class<?>, Function<? extends Reward, RewardWidget>> registeredMappings;

    public RegisterRewardDisplaysEvent(Map<Class<?>, Function<? extends Reward, RewardWidget>> registeredMappings) {
        this.registeredMappings = registeredMappings;
    }

    public <T extends Reward> void register(Class<T> type, Function<T, RewardWidget> widgetSupplier) {
        registeredMappings.put(type, widgetSupplier);
    }
}
