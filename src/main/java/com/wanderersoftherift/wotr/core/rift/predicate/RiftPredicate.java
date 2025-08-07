package com.wanderersoftherift.wotr.core.rift.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Optional;

/**
 * A predicate for assessing a rift
 *
 * @param tier      An optional minimum rift tier required
 * @param theme     An optional rift theme required
 * @param objective An optional objective required
 */
public record RiftPredicate(Optional<RiftTierPredicate> tier, Optional<RiftThemePredicate> theme,
        Optional<RiftObjectivePredicate> objective) {

    public static final Codec<RiftPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RiftTierPredicate.CODEC.optionalFieldOf("tier").forGetter(RiftPredicate::tier),
            RiftThemePredicate.CODEC.optionalFieldOf("theme").forGetter(RiftPredicate::theme),
            RiftObjectivePredicate.CODEC.optionalFieldOf("objective").forGetter(RiftPredicate::objective)
    ).apply(instance, RiftPredicate::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RiftPredicate> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(RiftTierPredicate.STREAM_CODEC), RiftPredicate::tier,
            ByteBufCodecs.optional(RiftThemePredicate.STREAM_CODEC), RiftPredicate::theme,
            ByteBufCodecs.optional(RiftObjectivePredicate.STREAM_CODEC), RiftPredicate::objective, RiftPredicate::new
    );

    public boolean matches(RiftConfig config) {
        for (var subpredicate : subpredicates()) {
            if (!subpredicate.map(x -> x.match(config)).orElse(true)) {
                return false;
            }
        }
        return true;
    }

    public MutableComponent displayText() {
        MutableComponent result = Component.empty();
        boolean empty = true;
        for (var subpredicate : subpredicates()) {
            if (subpredicate.isPresent()) {
                if (!empty) {
                    result.append(" ");
                }
                result.append(subpredicate.get().displayText());
                empty = false;
            }
        }
        return result;
    }

    private Iterable<Optional<? extends RiftConfigPredicate>> subpredicates() {
        return List.of(tier, theme, objective);
    }
}
