package com.wanderersoftherift.wotr.effects;

import com.wanderersoftherift.wotr.init.Attributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

public class CriticalEffect {
    public static float calcFinalDamage(
            float initialDamage,
            LivingEntity attacker,
            LivingEntity foe,
            RandomSource random) {

        int critChance = (int) attacker.getAttributeValue(Attributes.CRITICAL_CHANCE)
                - (int) foe.getAttributeValue(Attributes.CRITICAL_AVOIDANCE);
        float critMult = (float) attacker.getAttributeValue(Attributes.CRITICAL_MULTIPLIER);

        int critApplications = 0;

        if (critChance > 100) {
            critApplications = critChance / 100;
        } else {
            if (random.nextInt(0, 100) < (critChance % 100)) {
                critApplications += 1;
            }
        }
        return initialDamage * ((critMult - 1) * critApplications + 1);
    }
}
