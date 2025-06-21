package com.wanderersoftherift.wotr.modifier.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.MODIFIER_EFFECTS;
import static com.wanderersoftherift.wotr.init.WotrRegistries.MODIFIER_TYPES;

public abstract class AbstractModifierEffect {
    public static final Codec<AbstractModifierEffect> DIRECT_CODEC = MODIFIER_TYPES.byNameCodec()
            .dispatch(AbstractModifierEffect::getCodec, Function.identity());

    public static final Codec<Holder<AbstractModifierEffect>> CODEC = RegistryFixedCodec.create(MODIFIER_EFFECTS);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<AbstractModifierEffect>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(MODIFIER_EFFECTS);

    public abstract MapCodec<? extends AbstractModifierEffect> getCodec();

    public abstract void enableModifier(double roll, Entity entity, ModifierSource source);

    public abstract void disableModifier(double roll, Entity entity, ModifierSource source);

    public abstract void applyModifier();

    public abstract TooltipComponent getTooltipComponent(ItemStack stack, float roll, int color);
}
