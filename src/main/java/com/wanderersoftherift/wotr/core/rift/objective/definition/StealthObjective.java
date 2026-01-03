package com.wanderersoftherift.wotr.core.rift.objective.definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.core.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.core.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.core.rift.objective.ongoing.StealthOngoingObjective;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftConfigDataTypes;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Optional;

/**
 * An objective to be stealthy
 */
public record StealthObjective(Holder<RiftParameter> stealthTicks, List<RewardProvider> rewardProviders)
        implements ObjectiveType {
    public static final MapCodec<StealthObjective> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
            .group(RiftParameter.HOLDER_CODEC.fieldOf("stealth_ticks").forGetter(StealthObjective::stealthTicks),
                    RewardProvider.DIRECT_CODEC.listOf()
                            .optionalFieldOf("rewards", List.of())
                            .forGetter(StealthObjective::rewardProviders))
            .apply(inst, StealthObjective::new));

    @Override
    public MapCodec<? extends ObjectiveType> getCodec() {
        return CODEC;
    }

    @Override
    public OngoingObjective generateObjective(ServerLevelAccessor level, RiftConfig config) {
        var parameters = config.getCustomData(WotrRiftConfigDataTypes.INITIAL_RIFT_PARAMETERS);
        var key = stealthTicks.getKey();
        var param = parameters.getParameter(key);
        LootParams lootParams = new LootParams.Builder(level.getLevel()).create(LootContextParamSets.EMPTY);
        LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());
        List<Reward> rewards = rewardProviders.stream()
                .flatMap(provider -> provider.generateReward(lootContext).stream())
                .toList();
        if (param == null) {
            return new StealthOngoingObjective(key, rewards, 0);
        }
        return new StealthOngoingObjective(key, rewards, (int) param.get());
    }

    @Override
    public List<JigsawListProcessor> processors() {
        return List.of();
    }
}
