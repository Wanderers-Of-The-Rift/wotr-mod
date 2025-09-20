package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ChainAbilityState(long resetAge, boolean activated) {
    public static final ChainAbilityState DEFAULT = new ChainAbilityState(0, false);

    public static final Codec<ChainAbilityState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("reset_age").forGetter(ChainAbilityState::resetAge),
            Codec.BOOL.fieldOf("activated").forGetter(ChainAbilityState::activated)
    ).apply(instance, ChainAbilityState::new));
}
