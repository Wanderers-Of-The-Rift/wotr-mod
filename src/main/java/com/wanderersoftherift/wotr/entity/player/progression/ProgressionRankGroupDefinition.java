package com.wanderersoftherift.wotr.entity.player.progression;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

/**
 * A definition used when defining a progression track. Defines multiple ranks at once.
 * 
 * @param size    How many ranks to define.
 * @param cost    The cost for each.
 * @param rewards The rewards for each.
 */
public record ProgressionRankGroupDefinition(int size, int cost, List<RewardProvider> rewards,
        Optional<ResourceLocation> icon) {

    public static final Codec<ProgressionRankGroupDefinition> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(
                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("size", 1).forGetter(ProgressionRankGroupDefinition::size),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("cost").forGetter(ProgressionRankGroupDefinition::cost),
                    RewardProvider.DIRECT_CODEC.listOf()
                            .optionalFieldOf("rewards", List.of())
                            .forGetter(ProgressionRankGroupDefinition::rewards),
                    ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(ProgressionRankGroupDefinition::icon)
            ).apply(instance, ProgressionRankGroupDefinition::new));

    public static List<ProgressionRank> toRanks(List<ProgressionRankGroupDefinition> groupDefs) {
        ImmutableList.Builder<ProgressionRank> builder = ImmutableList.builder();
        int rankReq = 0;
        if (!groupDefs.isEmpty() && groupDefs.getFirst().cost() > 0) {
            builder.add(new ProgressionRank(0, List.of(), Optional.empty()));
        }
        for (var group : groupDefs) {
            for (int i = 0; i < group.size(); i++) {
                rankReq += group.cost();
                builder.add(new ProgressionRank(rankReq, group.rewards(), group.icon));
            }
        }
        return builder.build();
    }
}
