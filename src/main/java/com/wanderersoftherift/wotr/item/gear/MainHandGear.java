package com.wanderersoftherift.wotr.item.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.effects.AbstractEffect;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class MainHandGear extends AbstractGear{
    /*public Holder<AbstractAbility> basic;
    public Holder<AbstractAbility> secondary;*/

    public static final MapCodec<MainHandGear> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance
                    .group(ResourceLocation.CODEC.fieldOf("gear_name").forGetter(MainHandGear::getName),
                            ResourceLocation.CODEC.fieldOf("held_icon").forGetter(MainHandGear::getIcon),
                            ResourceLocation.CODEC.fieldOf("model").forGetter(MainHandGear::getModel)
                    ).apply(instance, MainHandGear::new));

    public MainHandGear(ResourceLocation name, ResourceLocation icon, ResourceLocation model) {
        super(name, icon, model);
        /*this.basic = basic;
        this.secondary = secondary;*/
    }

    @Override
    public MapCodec<? extends AbstractGear> getCodec() {
        return CODEC;
    }

    //public Holder<AbstractAbility> getBasic() {
        //return this.basic;


    //public Holder<AbstractAbility> getSecondary() {
        //return this.secondary;

}
