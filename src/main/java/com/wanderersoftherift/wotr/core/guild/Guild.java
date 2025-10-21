package com.wanderersoftherift.wotr.core.guild;

import com.google.common.base.Preconditions;
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

/**
 * GuildInfo provides information describing a guild
 * 
 * @param icon32 - 32x32 pixel icon for the guild
 * @param icon16 - 16x16 pixel icon for the guild
 */
public record Guild(ResourceLocation icon32, ResourceLocation icon16, List<GuildRank> ranks) {

    public static final Codec<Guild> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("icon32").forGetter(Guild::icon32),
            ResourceLocation.CODEC.fieldOf("icon16").forGetter(Guild::icon16),
            GuildRank.DIRECT_CODEC.listOf().fieldOf("ranks").forGetter(Guild::ranks)
    ).apply(instance, Guild::new));

    public static final Codec<Holder<Guild>> CODEC = RegistryFixedCodec.create(WotrRegistries.Keys.GUILDS);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Guild>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.GUILDS);

    public GuildRank getRank(int index) {
        if (index == 0) {
            return new GuildRank(icon16, 0, List.of());
        }
        return ranks.get(Math.min(index, ranks.size()) - 1);
    }

    public GuildRank getNextRank(int index) {
        Preconditions.checkArgument(index >= 0, "Rank must be 0 or greater");
        return ranks.get(Math.min(index, ranks.size() - 1));
    }

    public static Component getDisplayName(Holder<Guild> guild) {
        ResourceLocation loc = guild.getKey().location();
        return Component.translatable(loc.toLanguageKey("guild"));
    }

    public static Component getRankTitle(Holder<Guild> guild, int rank) {
        ResourceLocation loc = guild.getKey().location();
        return Component.translatable(loc.toLanguageKey("guild", "rank." + rank));
    }

}
