package com.wanderersoftherift.wotr.core;

import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

public class OnHitEffect {

    public static float critical(float initialDamage, LivingEntity attacker, LivingEntity foe, RandomSource random) {
        int baseCritChance = (int) attacker.getAttributeValue(WotrAttributes.CRITICAL_CHANCE);
        float critMult = (float) attacker.getAttributeValue(WotrAttributes.CRITICAL_MULTIPLIER);
        int critAvoid = (int) foe.getAttributeValue(WotrAttributes.CRITICAL_AVOIDANCE);

        int critChance = baseCritChance - critAvoid;
        int critApplications = critChance / 100;

        if (random.nextInt(100) < (critChance % 100)) {
            critApplications += 1;
        }
        float finalDamage = initialDamage * ((critMult - 1) * critApplications + 1);
        return finalDamage;
    }
}
