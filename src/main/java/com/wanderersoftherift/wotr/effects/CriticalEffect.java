package com.wanderersoftherift.wotr.effects;

import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

public class CriticalEffect {
    public static float calcFinalDamage(
            float initialDamage,
            LivingEntity attacker,
            LivingEntity foe,
            RandomSource random) {

        int critChance = (int) attacker.getAttributeValue(WotrAttributes.CRITICAL_CHANCE)
                - (int) foe.getAttributeValue(WotrAttributes.CRITICAL_AVOIDANCE);
        float critMult = (float) attacker.getAttributeValue(WotrAttributes.CRITICAL_MULTIPLIER);
        int critApplications = (critChance + random.nextInt(100)) / 100;

        return initialDamage * (critMult * critApplications);
    }
}
