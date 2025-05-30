package com.wanderersoftherift.wotr.client.render.item.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class AbilityEmblemProvider implements EmblemProvider {

    public static final MapCodec<AbilityEmblemProvider> CODEC = Codec.unit(new AbilityEmblemProvider())
            .fieldOf("ability");

    @Override
    public ResourceLocation getIcon(ItemStack item) {
        Holder<AbstractAbility> holder = item.get(WotrDataComponentType.ABILITY);
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
