package com.wanderersoftherift.wotr.abilities.effects.marker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Information for display an effect marker - an icon showing an effect is attached to a player
 * 
 * @param icon
 * @param name
 */
public record EffectMarker(ResourceLocation icon, String name) {
    public static final Codec<EffectMarker> CODEC = RecordCodecBuilder
            .create(instance -> instance
                    .group(ResourceLocation.CODEC.fieldOf("icon").forGetter(EffectMarker::icon),
                            Codec.STRING.fieldOf("name").forGetter(EffectMarker::name))
                    .apply(instance, EffectMarker::new));

    public Component getLabel() {
        return Component.translatable(name);
    }
}
