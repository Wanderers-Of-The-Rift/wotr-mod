package com.wanderersoftherift.wotr.item.ability;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.abilities.Ability;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ActivatableAbility(@NotNull Holder<Ability> ability) {
    public static final Codec<ActivatableAbility> CODEC = Ability.CODEC.xmap(ActivatableAbility::new,
            ActivatableAbility::ability);
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivatableAbility> STREAM_CODEC = StreamCodec.composite(
            Ability.STREAM_CODEC, ActivatableAbility::ability, ActivatableAbility::new
    );
}
