package com.wanderersoftherift.wotr.effects;

import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * <p>
 * The main class for the Thorns system.
 * </p>
 */
public class ThornsEffect {
    /**
     * <p>
     * Used to damage attacking entities.
     * </p>
     *
     * @param entity The entity that was damaged.
     * @param random RandomSource.
     * @return Thorns Damage multiplied by how many applications of said damage. (Thorns Damage * Thorns Applications)
     */
    public static double calcThornsDamage(LivingEntity entity, RandomSource random) {
        int thornsChance = (int) entity.getAttributeValue(WotrAttributes.THORNS_CHANCE);
        int thornsApplications = (thornsChance + random.nextInt(100)) / 100;

        return entity.getAttributeValue(WotrAttributes.THORNS_DAMAGE) * thornsApplications;
    }
}
