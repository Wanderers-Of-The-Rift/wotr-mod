package com.wanderersoftherift.wotr.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

public record PrimaryStatistic(Holder<Attribute> attribute) {
    public static final Codec<PrimaryStatistic> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(PrimaryStatistic::attribute)
    ).apply(instance, PrimaryStatistic::new));
    public static final Codec<Holder<PrimaryStatistic>> CODEC = RegistryFixedCodec
            .create(WotrRegistries.Keys.PRIMARY_STATISTICS);

    public static Component displayName(Holder<PrimaryStatistic> stat) {
        ResourceLocation statId = stat.getKey().location();
        return Component.translatable(statId.toLanguageKey("attribute"));
    }

}
