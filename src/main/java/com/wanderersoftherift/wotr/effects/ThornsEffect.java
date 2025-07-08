package com.wanderersoftherift.wotr.effects;

import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

public class ThornsEffect {
    public static double calcThornsDamage(LivingEntity entity, RandomSource random) {
        int thornsChance = (int) entity.getAttributeValue(WotrAttributes.THORNS_CHANCE);
        int thornsApplications = (thornsChance + random.nextInt(100)) / 100;

        return entity.getAttributeValue(WotrAttributes.THORNS_DAMAGE) * thornsApplications;
    }
}
