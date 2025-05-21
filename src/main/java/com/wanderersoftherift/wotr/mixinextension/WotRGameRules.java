package com.wanderersoftherift.wotr.mixinextension;

import net.minecraft.world.level.GameRules;

public interface WotRGameRules {

    <T extends GameRules.Value<T>> T wotrSetRuleFresh(GameRules.Key<T> key, T value);

    void wotrMakeMutable();
}
