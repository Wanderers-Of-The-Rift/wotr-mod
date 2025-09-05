package com.wanderersoftherift.wotr.core.rift.predicate;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;

public record RiftThemePredicate(Holder<RiftTheme> theme) implements RiftConfigPredicate {

    public static final Codec<RiftThemePredicate> CODEC = RiftTheme.CODEC.xmap(RiftThemePredicate::new,
            RiftThemePredicate::theme);

    public static final StreamCodec<RegistryFriendlyByteBuf, RiftThemePredicate> STREAM_CODEC = RiftTheme.STREAM_CODEC
            .map(RiftThemePredicate::new, RiftThemePredicate::theme);

    @Override
    public boolean match(RiftConfig config) {
        return config.theme().equals(theme);
    }

    @Override
    public MutableComponent displayText() {
        return Component.translatable(
                WanderersOfTheRift.translationId("rift_theme", theme.getKey().location()));
    }
}
