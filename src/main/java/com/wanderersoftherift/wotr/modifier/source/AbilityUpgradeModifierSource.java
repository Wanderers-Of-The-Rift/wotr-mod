package com.wanderersoftherift.wotr.modifier.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AbilityUpgradeModifierSource(ItemStack stack, int selection)
        implements ModifierSource.ItemModifierSource {

    public static final DualCodec<AbilityUpgradeModifierSource> TYPE = new DualCodec<>(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ItemStack.CODEC.fieldOf("stack").forGetter(AbilityUpgradeModifierSource::stack),
                    Codec.INT.fieldOf("selection").forGetter(AbilityUpgradeModifierSource::selection)
            ).apply(instance, AbilityUpgradeModifierSource::new)), StreamCodec.composite(
                    ItemStack.STREAM_CODEC, AbilityUpgradeModifierSource::stack, ByteBufCodecs.INT,
                    AbilityUpgradeModifierSource::selection, AbilityUpgradeModifierSource::new
            )
    );

    @Override
    public DualCodec<? extends ModifierSource> getType() {
        return TYPE;
    }

    @Override
    public @NotNull String getSerializedName() {
        return "ability_upgrade_" + selection;
    }

    @Override
    public ItemStack getItem(Entity entity) {
        return null;
    }

    @Override
    public List<AbstractModifierEffect> getModifierEffects(Entity entity) {
        return stack.get(WotrDataComponentType.ABILITY_UPGRADE_POOL)
                .getSelectedUpgrade(selection)
                .get()
                .modifierEffects();
    }
}
