package com.wanderersoftherift.wotr.core.goal;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * ModLoader event for registering GoalTracker lookup methods. Each method takes a player and returns zero or more
 * GoalTrackers related to that player
 */
public class RegisterGoalTrackerEvent extends Event implements IModBusEvent {

    /**
     * Registers a goal tracker lookup method
     * 
     * @param trackerLookupFunction A method taking a single player and returning a stream of trackers relating to them
     */
    public void register(Function<Player, Stream<? extends GoalTracker>> trackerLookupFunction) {
        GoalManager.registerGoalTrackerLookup(trackerLookupFunction);
    }
}
