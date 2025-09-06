package com.wanderersoftherift.wotr.abilities.sources;

import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public record MainAbilitySource(WotrEquipmentSlot slot) implements AbilitySource {

    public static final DualCodec<com.wanderersoftherift.wotr.abilities.sources.MainAbilitySource> TYPE = new DualCodec<>(
            WotrEquipmentSlot.DIRECT_CODEC.fieldOf("slot")
                    .xmap(com.wanderersoftherift.wotr.abilities.sources.MainAbilitySource::new,
                            com.wanderersoftherift.wotr.abilities.sources.MainAbilitySource::slot),
            StreamCodec.composite(
                    WotrEquipmentSlot.STREAM_CODEC,
                    com.wanderersoftherift.wotr.abilities.sources.MainAbilitySource::slot,
                    com.wanderersoftherift.wotr.abilities.sources.MainAbilitySource::new
            )
    );

    @Override
    public DualCodec<? extends AbilitySource> getType() {
        return TYPE;
    }

    @Override
    public @Nullable ItemStack getItem(LivingEntity entity) {
        return slot.getContent(entity);
    }

    @Override
    public Holder<Ability> getAbility(Entity entity) {
        return slot.getContent(entity).get(WotrDataComponentType.ABILITY).ability();
    }

    @Override
    public AbilityUpgradePool upgrades(Entity entity) {
        return slot.getContent(entity).get(WotrDataComponentType.ABILITY_UPGRADE_POOL);
    }

    public Holder<Ability> getMainAbility(Entity entity) {
        var abi = slot.getContent(entity).get(WotrDataComponentType.ABILITY);
        if (abi == null) {
            return null;
        }
        return abi.ability();
    }

    @Override
    public String getSerializedName() {
        return "slot_ability_" + slot.getSerializedName();
    }
}
