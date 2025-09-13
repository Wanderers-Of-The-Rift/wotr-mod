package com.wanderersoftherift.wotr.abilities.sources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.ChainAbility;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgrade;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ChainAbilitySource(AbilitySource parent, int index) implements AbilitySource {

    public static final DualCodec<ChainAbilitySource> TYPE = new DualCodec<>(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    AbilitySource.DIRECT_CODEC.fieldOf("parent").forGetter(ChainAbilitySource::parent),
                    Codec.INT.fieldOf("index").forGetter(ChainAbilitySource::index)
            ).apply(instance, ChainAbilitySource::new)), StreamCodec.composite(
                    AbilitySource.STREAM_CODEC, ChainAbilitySource::parent, ByteBufCodecs.INT,
                    ChainAbilitySource::index, ChainAbilitySource::new
            )
    );

    @Override
    public DualCodec<? extends AbilitySource> getType() {
        return TYPE;
    }

    @Override
    public @Nullable ItemStack getItem(LivingEntity entity) {
        return parent.getItem(entity);
    }

    @Override
    public Holder<Ability> getAbility(Entity entity) {
        if (parent.getAbility(entity).value() instanceof ChainAbility chainAbility) {
            return chainAbility.abilities().get(index).ability();
        }
        WanderersOfTheRift.LOGGER.warn("Incorrect parent ability type: {}", getSerializedName());
        return null;
    }

    @Override
    public @Nullable WotrEquipmentSlot getLinkedSlot() {
        return parent.getLinkedSlot();
    }

    @Override
    public @NotNull List<Holder<AbilityUpgrade>> upgrades(Entity entity) {
        return parent.upgrades(entity);
    }

    @Override
    public @NotNull String getSerializedName() {
        return parent.getSerializedName() + "_" + index;
    }
}
