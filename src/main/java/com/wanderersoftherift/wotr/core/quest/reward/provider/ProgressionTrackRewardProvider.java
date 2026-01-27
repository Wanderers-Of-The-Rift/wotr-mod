package com.wanderersoftherift.wotr.core.quest.reward.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import com.wanderersoftherift.wotr.core.quest.reward.ProgressionTrackPointReward;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTrack;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Generates a potentially randomised reputation reward
 * 
 * @param tracks A list of progression tracks, of which one will be selected for the reward
 * @param amount A provider for the amount to reward
 */
public record ProgressionTrackRewardProvider(List<Holder<ProgressionTrack>> tracks, NumberProvider amount)
        implements RewardProvider {

    public static final MapCodec<ProgressionTrackRewardProvider> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.withAlternative(ProgressionTrack.CODEC.listOf(1, Integer.MAX_VALUE), ProgressionTrack.CODEC,
                            List::of).fieldOf("tracks").forGetter(ProgressionTrackRewardProvider::tracks),
                    NumberProviders.CODEC.fieldOf("amount").forGetter(ProgressionTrackRewardProvider::amount)
            ).apply(instance, ProgressionTrackRewardProvider::new));

    @Override
    public MapCodec<? extends RewardProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Reward> generateReward(LootContext context) {
        Holder<ProgressionTrack> track;
        if (tracks.size() > 1) {
            track = tracks.get(context.getRandom().nextInt(tracks.size()));
        } else {
            track = tracks.getFirst();
        }
        return List.of(new ProgressionTrackPointReward(track, amount.getInt(context)));
    }
}
