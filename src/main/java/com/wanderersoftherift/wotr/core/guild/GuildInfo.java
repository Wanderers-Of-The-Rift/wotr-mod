package com.wanderersoftherift.wotr.core.guild;

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
public record GuildInfo(ResourceLocation icon32, ResourceLocation icon16, List<GuildRank> ranks) {

    public static final Codec<GuildInfo> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("icon32").forGetter(GuildInfo::icon32),
            ResourceLocation.CODEC.fieldOf("icon16").forGetter(GuildInfo::icon16),
            GuildRank.DIRECT_CODEC.listOf().fieldOf("ranks").forGetter(GuildInfo::ranks)
    ).apply(instance, GuildInfo::new));

    public static final Codec<Holder<GuildInfo>> CODEC = RegistryFixedCodec.create(WotrRegistries.Keys.GUILDS);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<GuildInfo>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.GUILDS);

    public static Component getDisplayName(Holder<GuildInfo> guild) {
        ResourceLocation loc = guild.getKey().location();
        return Component.translatable(loc.toLanguageKey("guild"));
    }

    public static Component getRankTitle(Holder<GuildInfo> guild, int rank) {
        ResourceLocation loc = guild.getKey().location();
        return Component.translatable(loc.toLanguageKey("guild", "rank." + rank));
    }

}
