package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.util.context.ContextKey;

public final class ModLootContextParams {

    public static final ContextKey<Integer> RIFT_TIER = new ContextKey<>(WanderersOfTheRift.id("rift_tier"));

    private ModLootContextParams() {
    }
}
