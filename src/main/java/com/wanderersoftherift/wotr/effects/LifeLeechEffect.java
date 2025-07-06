package com.wanderersoftherift.wotr.effects;

import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.world.entity.LivingEntity;

public class LifeLeechEffect {
    
    public static double calcHeal (LivingEntity causer, LivingEntity receiver, float incomingDamage) {
        double healthToHeal = 0;
        if (causer != null) {
            healthToHeal = Math.min(incomingDamage, receiver.getHealth()) * (float) causer.getAttributeValue(WotrAttributes.LIFE_LEECH);
        }
        return healthToHeal;
    }
}
