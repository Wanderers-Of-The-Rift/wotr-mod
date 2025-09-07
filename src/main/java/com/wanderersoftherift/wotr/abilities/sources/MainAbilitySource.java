package com.wanderersoftherift.wotr.abilities.sources;

import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgrade;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.ability.ActivatableAbility;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

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
    public @Nullable WotrEquipmentSlot getLinkedSlot() {
        return slot;
    }

    @Override
    public Holder<Ability> getAbility(Entity entity) {
        ActivatableAbility mainAbility = slot.getContent(entity).get(WotrDataComponentType.ABILITY);
        if (mainAbility != null) {
            return mainAbility.ability();
        }
        return null;
    }

    @Override
    public @NotNull List<Holder<AbilityUpgrade>> upgrades(Entity entity) {
        return slot.getContent(entity)
                .getOrDefault(WotrDataComponentType.ABILITY_UPGRADE_POOL, AbilityUpgradePool.EMPTY)
                .getAllSelected();
    }

    @Override
    public @NotNull String getSerializedName() {
        return "slot_ability_" + slot.getSerializedName();
    }
}
