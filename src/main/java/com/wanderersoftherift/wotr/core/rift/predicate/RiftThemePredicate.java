package com.wanderersoftherift.wotr.core.rift.predicate;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record RiftThemePredicate(Holder<RiftTheme> theme) implements RiftConfigPredicate {

    public static final Codec<RiftThemePredicate> CODEC = RiftTheme.CODEC.xmap(RiftThemePredicate::new,
            RiftThemePredicate::theme);

    public static final StreamCodec<RegistryFriendlyByteBuf, RiftThemePredicate> STREAM_CODEC = RiftTheme.STREAM_CODEC
            .map(RiftThemePredicate::new, RiftThemePredicate::theme);

    @Override
    public boolean match(RiftConfig config) {
        return !config.theme().map(actual -> actual.equals(theme)).orElse(false);
    }

    @Override
    public MutableComponent displayText() {
        return Component.translatable(
                WanderersOfTheRift.translationId("rift_theme", ResourceLocation.parse(theme.getRegisteredName())));
    }
}
