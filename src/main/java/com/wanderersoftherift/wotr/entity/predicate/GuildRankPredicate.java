package com.wanderersoftherift.wotr.entity.predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Predicate requiring the entity is in a specific range of guild rank
 * 
 * @param guild
 * @param min   The minimum rank (inclusive)
 * @param max   The maximum rank (inclusive)
 */
public record GuildRankPredicate(Holder<Guild> guild, int min, int max) implements EntitySubPredicate {

    public static final MapCodec<GuildRankPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Guild.CODEC.fieldOf("guild").forGetter(GuildRankPredicate::guild),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min", 0).forGetter(GuildRankPredicate::min),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(GuildRankPredicate::max)
    ).apply(instance, GuildRankPredicate::new));

    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(@NotNull Entity entity, @NotNull ServerLevel level, @Nullable Vec3 position) {
        int rank = entity.getExistingData(WotrAttachments.GUILD_STATUS).map(status -> status.getRank(guild)).orElse(0);
        return rank >= min && rank <= max;
    }
}
