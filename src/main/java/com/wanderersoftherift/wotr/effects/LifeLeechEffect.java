package com.wanderersoftherift.wotr.effects;

import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.world.entity.LivingEntity;

public class LifeLeechEffect {
    /**
     *
     * @param causer         Causer of the damage.
     * @param receiver       The entity the causer is attacking.
     * @param incomingDamage The hit's damage.
     * @return The life to be leeched. Can either be the initial damage, or the receiver's health, whichever is less.
     */
    public static float calcHeal(LivingEntity causer, LivingEntity receiver, float incomingDamage) {
        return Math.min(incomingDamage, receiver.getHealth()) * (float) causer.getAttributeValue(WotrAttributes.LIFE_LEECH);
    }
}
