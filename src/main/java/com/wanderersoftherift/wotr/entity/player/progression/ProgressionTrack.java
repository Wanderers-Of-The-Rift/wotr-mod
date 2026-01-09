package com.wanderersoftherift.wotr.entity.player.progression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

/**
 * A linear track of progression, composed of ranks
 * 
 * @param ranks
 */
public record ProgressionTrack(List<ProgressionRank> ranks, String toastTitleId, String rankFormatId, String pointsId,
        boolean hasCustomRankTitles, ResourceLocation rewardIcon, Optional<ResourceLocation> displayIcon) {

    public static final Codec<ProgressionTrack> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.withAlternative(ProgressionRank.CODEC.listOf(), ProgressionRankGroupDefinition.CODEC.listOf(),
                    ProgressionRankGroupDefinition::toRanks).fieldOf("ranks").forGetter(ProgressionTrack::ranks),
            Codec.STRING.optionalFieldOf("toast_title_id", "toast.wotr.rank_up")
                    .forGetter(ProgressionTrack::toastTitleId),
            Codec.STRING.optionalFieldOf("rank_format_id", "toast.wotr.rank_format.default")
                    .forGetter(ProgressionTrack::toastTitleId),
            Codec.STRING.optionalFieldOf("points_id", "track.wotr.points.default")
                    .forGetter(ProgressionTrack::pointsId),
            Codec.BOOL.optionalFieldOf("custom_rank_titles", true).forGetter(ProgressionTrack::hasCustomRankTitles),
            ResourceLocation.CODEC.fieldOf("reward_icon").forGetter(ProgressionTrack::rewardIcon),
            ResourceLocation.CODEC.optionalFieldOf("display_icon").forGetter(ProgressionTrack::displayIcon)
    ).apply(instance, ProgressionTrack::new));

    public static final Codec<Holder<ProgressionTrack>> CODEC = RegistryFixedCodec
            .create(WotrRegistries.Keys.PROGRESSION_TRACKS);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ProgressionTrack>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.PROGRESSION_TRACKS);

    public boolean hasRankUpToast() {
        return !toastTitleId.isEmpty();
    }

    public static Component getDisplayName(Holder<ProgressionTrack> track) {
        ResourceLocation loc = track.getKey().location();
        return Component.translatable(loc.toLanguageKey("track"));
    }

    public static Component getRankTitle(Holder<ProgressionTrack> track, int rank) {
        if (rank < 0 || rank > track.value().ranks.size()) {
            return Component.empty();
        }
        if (!track.value().hasCustomRankTitles) {
            return Component.translatable(track.value().rankFormatId, rank + 1);
        } else {
            return Component.translatable(track.value().rankFormatId,
                    Component.translatable(track.getKey().location().toLanguageKey("track", "rank." + (rank + 1))));
        }
    }

    public int rankCount() {
        return ranks.size();
    }
}
