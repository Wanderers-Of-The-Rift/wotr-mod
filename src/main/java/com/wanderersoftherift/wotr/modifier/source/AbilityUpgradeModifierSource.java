package com.wanderersoftherift.wotr.modifier.source;

import org.jetbrains.annotations.NotNull;

public class AbilityUpgradeModifierSource implements ModifierSource {
    private final int selection;

    public AbilityUpgradeModifierSource(int selection) {
        this.selection = selection;
    }

    @Override
    public @NotNull String getSerializedName() {
        return "ability_upgrade_" + selection;
    }
}
