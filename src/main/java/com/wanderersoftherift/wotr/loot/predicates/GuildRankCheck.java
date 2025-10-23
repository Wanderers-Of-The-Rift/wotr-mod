package com.wanderersoftherift.wotr.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.core.guild.GuildStatus;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.loot.WotrLootItemConditionTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A LootItemCondition that checks if the player is between the min (inc) and max (inc) rank of a guild.
 */
public record GuildRankCheck(Holder<Guild> guild, int minRank, int maxRank) implements LootItemCondition {

    public static final MapCodec<GuildRankCheck> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Guild.CODEC.fieldOf("guild").forGetter(GuildRankCheck::guild),
                    Codec.INT.optionalFieldOf("min", 0).forGetter(GuildRankCheck::minRank),
                    Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(GuildRankCheck::maxRank)
            ).apply(instance, GuildRankCheck::new));

    @Override
    public @NotNull LootItemConditionType getType() {
        return WotrLootItemConditionTypes.GUILD_RANK_CHECK.get();
    }

    public boolean test(LootContext context) {
        Entity entity = context.getParameter(LootContextParams.THIS_ENTITY);
        Optional<GuildStatus> guildStatus = entity.getExistingData(WotrAttachments.GUILD_STATUS);
        if (guildStatus.isPresent()) {
            int rank = guildStatus.get().getRank(guild);
            return rank >= minRank && rank <= maxRank;
        }
        return false;
    }

    public static GuildRankCheck.Builder guildRank(Holder<Guild> guild) {
        return new GuildRankCheck.Builder(guild);
    }

    public static GuildRankCheck.Builder guildRank(Holder<Guild> guild, int min, int max) {
        return new GuildRankCheck.Builder(guild).min(min).max(max);
    }

    public static class Builder implements LootItemCondition.Builder {
        private final Holder<Guild> guild;
        private int min = 0;
        private int max = Integer.MAX_VALUE;

        public Builder(Holder<Guild> guild) {
            this.guild = guild;
        }

        public Builder min(int min) {
            this.min = min;
            return this;
        }

        public Builder max(int max) {
            this.max = max;
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new GuildRankCheck(guild, min, max);
        }
    }
}
