package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ChainAbilityStates {

    public static final Codec<ChainAbilityStates> CODEC = RecordCodecBuilder
            .<Pair<AbilitySource, State>>create(instance -> instance.group(
                    AbilitySource.DIRECT_CODEC.fieldOf("source").forGetter(Pair::getA),
                    State.CODEC.fieldOf("state").forGetter(Pair::getB)
            ).apply(instance, Pair::new))
            .listOf()
            .xmap(pairs -> new ChainAbilityStates(pairs.stream().collect(Collectors.toMap(Pair::getA, Pair::getB))),
                    states -> states.states.entrySet()
                            .stream()
                            .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                            .toList());

    private final Map<AbilitySource, State> states = new HashMap<>();

    public ChainAbilityStates() {
    }

    private ChainAbilityStates(Map<AbilitySource, State> states) {
        this.states.putAll(states);
    }

    public void setResetAge(AbilitySource source, long age) {
        states.put(source, new State(age, false));
    }

    public void setActivated(AbilitySource source) {
        states.put(source, new State(0, true));
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

    private record State(long resetAge, boolean activated) {
        public static final State EMPTY = new State(0, false);

        public static final Codec<State> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.fieldOf("reset_age").forGetter(State::resetAge),
                Codec.BOOL.fieldOf("activated").forGetter(State::activated)
        ).apply(instance, State::new));
    }
}
