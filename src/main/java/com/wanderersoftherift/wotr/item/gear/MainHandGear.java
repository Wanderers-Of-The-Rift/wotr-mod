package com.wanderersoftherift.wotr.item.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.effects.AbstractEffect;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class MainHandGear extends AbstractGear{
    public ResourceLocation basic;
    public ResourceLocation secondary;

    public static final MapCodec<MainHandGear> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance
                    .group(ResourceLocation.CODEC.fieldOf("gear_name").forGetter(MainHandGear::getName),
                            ResourceLocation.CODEC.fieldOf("held_icon").forGetter(MainHandGear::getIcon),
                            ResourceLocation.CODEC.fieldOf("model").forGetter(MainHandGear::getModel),
                            ResourceLocation.CODEC.fieldOf("basic").forGetter(MainHandGear::getBasic),
                            ResourceLocation.CODEC.fieldOf("secondary").forGetter(MainHandGear::getSecondary)
                    ).apply(instance, MainHandGear::new));

    public MainHandGear(ResourceLocation name, ResourceLocation icon, ResourceLocation model, ResourceLocation basic, ResourceLocation secondary) {
        super(name, icon, model);
        this.basic = basic;
        this.secondary = secondary;
    }

    @Override
    public MapCodec<? extends AbstractGear> getCodec() {
        return CODEC;
    }

    public ResourceLocation getBasic() {
        return this.basic;
    }

    public ResourceLocation getSecondary() {
        return this.secondary;
    }

}
