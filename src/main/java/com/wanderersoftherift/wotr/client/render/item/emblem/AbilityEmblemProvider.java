package com.wanderersoftherift.wotr.client.render.item.emblem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Provides an emblem icon from an Ability applied to the item
 */
public class AbilityEmblemProvider implements EmblemProvider {

    public static final MapCodec<AbilityEmblemProvider> CODEC = Codec.unit(new AbilityEmblemProvider())
            .fieldOf("ability");

    @Override
    public ResourceLocation getIcon(ItemStack item) {
        Holder<Ability> holder = item.get(WotrDataComponentType.ABILITY);
        if (holder != null) {
            return holder.value().getSmallIcon().orElse(holder.value().getIcon());
        }
        return null;
    }

    @Override
    public MapCodec<? extends EmblemProvider> type() {
        return CODEC;
    }
}
