package com.wanderersoftherift.wotr.core.guild;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record GuildRank(ResourceLocation icon, int reputationRequirement, List<RewardProvider> rewards) {
    public static final Codec<GuildRank> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("icon").forGetter(GuildRank::icon),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("reputation_requirement", 0)
                    .forGetter(GuildRank::reputationRequirement),
            RewardProvider.DIRECT_CODEC.listOf().optionalFieldOf("rewards", List.of()).forGetter(GuildRank::rewards)
    ).apply(instance, GuildRank::new));
}
