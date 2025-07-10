package com.wanderersoftherift.wotr.effects;

import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * <p>
 * The main class for Wanderers Of the Rifts' critical system.
 * <p/>
 */

public class CriticalEffect {
    /**
     * <p>
     * Calculates damage for the current critical hit.
     * </p>
     *
     * @param initialDamage The initial damage that is used in the calculation.
     * @param causer        Causer of the damage.
     * @param receiver      Entity the causer is attacking.
     * @param random        RandomSource.
     * @return The calculated damage for the critical, including bonuses to the critical multiplier and applications of
     *         the critical. (Base Damage + Base Damage * Critical Bonus * Critical Applications)
     */
    public static float calcCriticalDamage(
            float initialDamage,
            LivingEntity causer,
            LivingEntity receiver,
            RandomSource random) {

        int critChance = (int) causer.getAttributeValue(WotrAttributes.CRITICAL_CHANCE)
                - (int) receiver.getAttributeValue(WotrAttributes.CRITICAL_AVOIDANCE);
        float critBonus = (float) causer.getAttributeValue(WotrAttributes.CRITICAL_BONUS);
        int critApplications = (critChance + random.nextInt(100)) / 100;

        return initialDamage + initialDamage * critBonus * critApplications;
    }
}
