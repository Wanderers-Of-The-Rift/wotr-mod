package com.wanderersoftherift.wotr.core.guild;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * GuildInfo provides information describing a guild
 * 
 * @param emblem
 */
public record GuildInfo(ResourceLocation emblem) {
    public static final Codec<GuildInfo> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("emblem").forGetter(GuildInfo::emblem)
    ).apply(instance, GuildInfo::new));

    // TODO: Max rank? reputation reqs? What else do we need to capture about guilds?

    public static Component getDisplayName(Holder<GuildInfo> guild) {
        ResourceLocation loc = ResourceLocation.parse(guild.getRegisteredName());
        return Component.translatable(loc.toLanguageKey("guild"));
    }

    public static Component getRankTitle(Holder<GuildInfo> guild, int rank) {
        ResourceLocation loc = ResourceLocation.parse(guild.getRegisteredName());
        return Component.translatable(loc.toLanguageKey("guild", "rank." + rank));
    }
}
