package com.wanderersoftherift.wotr.core.quest.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * A predicate for assessing a rift
 *
 * @param tier      An optional minimum rift tier required
 * @param theme     An optional rift theme required
 * @param objective An optional objective required
 */
public record RiftPredicate(Optional<Integer> tier, Optional<Holder<RiftTheme>> theme,
        Optional<Holder<ObjectiveType>> objective) {

    public static final Codec<RiftPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("tier").forGetter(RiftPredicate::tier),
            RiftTheme.CODEC.optionalFieldOf("theme").forGetter(RiftPredicate::theme),
            ObjectiveType.CODEC.optionalFieldOf("objective").forGetter(RiftPredicate::objective)
    ).apply(instance, RiftPredicate::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RiftPredicate> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.INT), RiftPredicate::tier,
            ByteBufCodecs.optional(RiftTheme.STREAM_CODEC), RiftPredicate::theme,
            ByteBufCodecs.optional(ObjectiveType.STREAM_CODEC), RiftPredicate::objective, RiftPredicate::new
    );

    public boolean matches(RiftConfig config) {
        if (tier.isPresent() && config.tier() < tier.get()) {
            return false;
        }

        if (theme.isPresent() && !config.theme().map(actual -> actual.equals(theme.get())).orElse(false)) {
            return false;
        }

        if (objective.isPresent() && !config.objective().map(actual -> actual.equals(objective.get())).orElse(false)) {
            return false;
        }

        return true;
    }

    public MutableComponent displayText() {
        MutableComponent result = Component.empty();
        boolean empty = true;
        if (tier.isPresent()) {
            result.append(Component.translatable(WanderersOfTheRift.translationId("goal", "rift.tier"), tier.get()));
            empty = false;
        }
        if (theme.isPresent()) {
            if (!empty) {
                result.append(" ");
            }
            result.append(Component.translatable(WanderersOfTheRift.translationId("rift_theme",
                    ResourceLocation.parse(theme.get().getRegisteredName()))));
            empty = false;
        }
        if (objective.isPresent()) {
            if (!empty) {
                result.append(" ");
            }
            ResourceLocation id = ResourceLocation.parse(objective.get().getRegisteredName());
            result.append(Component.translatable("objective." + id.getNamespace() + "." + id.getPath() + ".name"));
            empty = false;
        }
        return result;
    }
}
