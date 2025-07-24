package com.wanderersoftherift.wotr.core.rift.predicate;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record RiftObjectivePredicate(Holder<ObjectiveType> objective) implements RiftConfigPredicate {
    public static final Codec<RiftObjectivePredicate> CODEC = ObjectiveType.CODEC.xmap(RiftObjectivePredicate::new,
            RiftObjectivePredicate::objective);

    public static final StreamCodec<RegistryFriendlyByteBuf, RiftObjectivePredicate> STREAM_CODEC = ObjectiveType.STREAM_CODEC
            .map(RiftObjectivePredicate::new, RiftObjectivePredicate::objective);

    @Override
    public boolean match(RiftConfig config) {
        return !config.objective().map(actual -> actual.equals(objective)).orElse(false);
    }

    @Override
    public MutableComponent displayText() {
        ResourceLocation id = ResourceLocation.parse(objective.getRegisteredName());
        return Component.translatable(id.toLanguageKey("objective", "name"));
    }
}
