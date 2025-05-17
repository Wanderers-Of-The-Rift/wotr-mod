package com.wanderersoftherift.wotr.init.loot;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.util.context.ContextKey;

public final class WotrLootContextParams {

    public static final ContextKey<Integer> RIFT_TIER = new ContextKey<>(WanderersOfTheRift.id("rift_tier"));

    private WotrLootContextParams() {
    }
}
