package com.wanderersoftherift.wotr.gui.widget.lookup;

import net.minecraft.client.gui.components.AbstractWidget;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.function.Function;

public class RegisterRewardDisplaysEvent extends Event implements IModBusEvent {

    private final Map<Class<?>, Function<?, AbstractWidget>> registeredMappings;

    public RegisterRewardDisplaysEvent(Map<Class<?>, Function<?, AbstractWidget>> registeredMappings) {
        this.registeredMappings = registeredMappings;
    }

    public <T> void register(Class<T> type, Function<T, AbstractWidget> widgetSupplier) {
        registeredMappings.put(type, widgetSupplier);
    }
}
