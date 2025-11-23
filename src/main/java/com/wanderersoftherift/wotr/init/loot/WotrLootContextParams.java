package com.wanderersoftherift.wotr.init.loot;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import net.minecraft.util.context.ContextKey;

public final class WotrLootContextParams {

    public static final ContextKey<Integer> RIFT_TIER = new ContextKey<>(WanderersOfTheRift.id("rift_tier"));
    public static final ContextKey<RiftConfig> RIFT_CONFIG = new ContextKey<>(WanderersOfTheRift.id("rift_config"));

    private WotrLootContextParams() {
    }
}
