package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Information for display an effect marker - an icon showing an effect is attached to a player
 * 
 * @param icon
 * @param name
 */
public record EffectMarker(ResourceLocation icon, String name) {
    public static final Codec<EffectMarker> DIRECT_CODEC = RecordCodecBuilder
            .create(instance -> instance
                    .group(ResourceLocation.CODEC.fieldOf("icon").forGetter(EffectMarker::icon),
                            Codec.STRING.fieldOf("name").forGetter(EffectMarker::name))
                    .apply(instance, EffectMarker::new));
    public static final Codec<Holder<EffectMarker>> CODEC = RegistryFixedCodec
            .create(WotrRegistries.Keys.EFFECT_MARKERS);

    public Component getLabel() {
        return Component.translatable(name);
    }
}
