package com.wanderersoftherift.wotr.loot;

import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.init.ModLootContextParams;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;

public class LootUtil {

    public static int getRiftTierFromContext(LootContext context) {
        Integer riftTier = context.getOptionalParameter(ModLootContextParams.RIFT_TIER);
        if (riftTier == null) {
            ServerLevel serverlevel = context.getLevel();
            if (!RiftData.isRift(serverlevel)) {
                return 0;
            }
            riftTier = RiftData.get(serverlevel).getTier();
        }
        return riftTier;
    }
}
