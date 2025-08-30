package com.wanderersoftherift.wotr.abilities.sources;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.core.inventory.slot.AbilityEquipmentSlot;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface AbilitySource extends StringRepresentable {

    Codec<AbilitySource> DIRECT_CODEC = WotrRegistries.ABILITY_SOURCES.byNameCodec()
            .dispatch(AbilitySource::getType, DualCodec::codec);
    StreamCodec<RegistryFriendlyByteBuf, AbilitySource> STREAM_CODEC = ByteBufCodecs
            .registry(WotrRegistries.Keys.ABILITY_SOURCES)
            .dispatch(AbilitySource::getType, DualCodec::streamCodec);

    static AbilitySource byModifierSource(ModifierSource source, int effectIndex) {
        return new ModifierAbilitySource(source, effectIndex);
    }

    static MainAbilitySource sourceForSlot(int slot) {
        return new MainAbilitySource(AbilityEquipmentSlot.forSlot(slot));
    }

    DualCodec<? extends AbilitySource> getType();

    @Nullable ItemStack getItem(LivingEntity entity);

    Holder<Ability> getAbility(Entity entity);

    AbilityUpgradePool upgrades(Entity entity);

}
