package com.wanderersoftherift.wotr.item.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.codec.LaxRegistryCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.GEAR;

public abstract class AbstractGear {

    public static final Codec<AbstractGear> DIRECT_CODEC = WotrRegistries.GEAR_TYPES.byNameCodec()
            .dispatch(AbstractGear::getCodec, Function.identity());
    public static final Codec<Holder<AbstractGear>> CODEC = LaxRegistryCodec.create(GEAR);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<AbstractGear>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(GEAR);

    private final ResourceLocation name;
    private ResourceLocation heldIcon = ResourceLocation.withDefaultNamespace("textures/misc/forcefield.png");
    private ResourceLocation model;
    private Component displayName;

    public AbstractGear(ResourceLocation name, ResourceLocation heldIcon, ResourceLocation model, Component displayName){
        this.name = name;
        this.heldIcon = heldIcon;
        this.model = model;
        this.displayName = displayName;
    }

    public abstract MapCodec<? extends AbstractGear> getCodec();


}
