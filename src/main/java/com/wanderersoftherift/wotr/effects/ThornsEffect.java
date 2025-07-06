package com.wanderersoftherift.wotr.effects;

import com.wanderersoftherift.wotr.init.Attributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

public class ThornsEffect {
    public static int calcThornsMult(LivingEntity entity, RandomSource random) {
        int thornsChance = (int) entity.getAttributeValue(Attributes.THORNS_CHANCE);
        int thornsApplications = 0;

        if (thornsChance > 100 || thornsChance == 100) {
            thornsApplications = thornsChance / 100;
        } else {
            if (random.nextInt(0, 100) < (thornsChance % 100)) {
                thornsApplications += 1;
            }
        }
        return thornsApplications;
    }
}
