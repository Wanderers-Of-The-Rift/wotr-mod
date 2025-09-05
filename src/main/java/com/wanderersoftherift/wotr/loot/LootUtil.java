package com.wanderersoftherift.wotr.loot;

import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.init.loot.WotrLootContextParams;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;

public class LootUtil {

    public static int getRiftTierFromContext(LootContext context) {
        Integer riftTier = context.getOptionalParameter(WotrLootContextParams.RIFT_TIER);
        if (riftTier == null) {
            ServerLevel serverlevel = context.getLevel();
            if (!RiftLevelManager.isRift(serverlevel)) {
                return 0;
            }
            var optionalTier = RiftData.get(serverlevel).getTier();
            riftTier = optionalTier.orElse(0);
        }
        return riftTier;
    }
}
