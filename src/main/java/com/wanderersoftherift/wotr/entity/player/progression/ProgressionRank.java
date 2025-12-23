package com.wanderersoftherift.wotr.entity.player.progression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

/**
 * A single progression rank.
 * 
 * @param requirement How many points needed to achieve this rank
 * @param rewards     The rewards for achieving this rank
 * @param icon        Optional icon for this rank (used in toast at least)
 */
public record ProgressionRank(int requirement, List<RewardProvider> rewards, Optional<ResourceLocation> icon) {
    public static final Codec<ProgressionRank> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("requirement").forGetter(ProgressionRank::requirement),
            RewardProvider.DIRECT_CODEC.listOf()
                    .optionalFieldOf("rewards", List.of())
                    .forGetter(ProgressionRank::rewards),
            ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(ProgressionRank::icon)
    ).apply(instance, ProgressionRank::new));
}
