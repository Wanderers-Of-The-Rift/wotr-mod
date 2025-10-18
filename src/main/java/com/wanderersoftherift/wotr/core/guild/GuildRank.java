package com.wanderersoftherift.wotr.core.guild;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record GuildRank(int reputationRequirement, List<RewardProvider> rewards) {
    public static final Codec<GuildRank> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("reputation_requirement", 0)
                    .forGetter(GuildRank::reputationRequirement),
            RewardProvider.DIRECT_CODEC.listOf().optionalFieldOf("rewards", List.of()).forGetter(GuildRank::rewards)
    ).apply(instance, GuildRank::new));
}
