package com.wanderersoftherift.wotr.init.loot;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterData;
import net.minecraft.util.context.ContextKey;

public final class WotrLootContextParams {

    public static final ContextKey<Integer> RIFT_TIER = new ContextKey<>(WanderersOfTheRift.id("rift_tier"));
    public static final ContextKey<RiftParameterData> RIFT_PARAMETERS = new ContextKey<>(
            WanderersOfTheRift.id("rift_parameters"));

    private WotrLootContextParams() {
    }
}
