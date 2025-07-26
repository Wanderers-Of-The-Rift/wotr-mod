package com.wanderersoftherift.wotr.gui.widget.lookup;

import com.wanderersoftherift.wotr.gui.widget.quest.GoalDisplay;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.function.Function;

public class RegisterGoalDisplaysEvent extends Event implements IModBusEvent {
    private final Map<Class<?>, Function<?, GoalDisplay>> registeredMappings;

    public RegisterGoalDisplaysEvent(Map<Class<?>, Function<?, GoalDisplay>> registeredMappings) {
        this.registeredMappings = registeredMappings;
    }

    public <T> void register(Class<T> type, Function<T, GoalDisplay> widgetSupplier) {
        registeredMappings.put(type, widgetSupplier);
    }
}
