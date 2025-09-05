package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChainAbilityStates {

    public static final Codec<ChainAbilityStates> CODEC = State.CODEC.listOf()
            .xmap(ChainAbilityStates::new, states -> List.copyOf(states.states.values()));

    private final Map<AbilitySource, State> states = new HashMap<>();

    public ChainAbilityStates() {
    }

    private ChainAbilityStates(List<State> states) {
        for (State state : states) {
            this.states.put(state.source, state);
        }
    }

    public void setResetAge(AbilitySource source, long age) {
        states.put(source, new State(source, age, false));
    }

    public void setActivated(AbilitySource source) {
        states.put(source, new State(source, 0, true));
    }

    public void clear(AbilitySource source) {
        states.remove(source);
    }

    public boolean hasBeenActivated(AbilitySource source) {
        return states.getOrDefault(source, State.EMPTY).activated;
    }

    public long resetTime(AbilitySource source) {
        return states.getOrDefault(source, State.EMPTY).resetAge;
    }

    private record State(AbilitySource source, long resetAge, boolean activated) {
        public static final State EMPTY = new State(null, 0, false);

        public static final Codec<State> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                AbilitySource.DIRECT_CODEC.fieldOf("source").forGetter(State::source),
                Codec.LONG.fieldOf("reset_age").forGetter(State::resetAge),
                Codec.BOOL.fieldOf("activated").forGetter(State::activated)
        ).apply(instance, State::new));
    }
}
