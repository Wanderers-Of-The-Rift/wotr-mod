package com.wanderersoftherift.wotr.modifier.source;

import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbilityUpgradeModifierSource implements ModifierSource {
    private final int selection;

    public AbilityUpgradeModifierSource(int selection) {
        this.selection = selection;
    }

    @Override
    public @NotNull String getSerializedName() {
        return "ability_upgrade_" + selection;
    }

    @Override
    public @Nullable WotrEquipmentSlot slot() {
        return null; // todo
    }
}
