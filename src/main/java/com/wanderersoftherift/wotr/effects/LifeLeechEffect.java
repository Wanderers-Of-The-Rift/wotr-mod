package com.wanderersoftherift.wotr.effects;

import com.wanderersoftherift.wotr.init.Attributes;
import net.minecraft.world.entity.LivingEntity;

public class LifeLeechEffect {

    public static float calcHeal(LivingEntity causer, LivingEntity receiver, float incomingDamage) {
        float healthToHeal = 0;
        if (causer != null) {
            healthToHeal = Math.min(incomingDamage, receiver.getHealth())
                    * (float) causer.getAttributeValue(Attributes.LIFE_LEECH);
        }
        return healthToHeal;
    }
}
