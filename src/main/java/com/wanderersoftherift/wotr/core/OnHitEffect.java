package com.wanderersoftherift.wotr.core;

import com.wanderersoftherift.wotr.init.WotrAttributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class OnHitEffect {

    public static float critical(LivingEntity attacker, LivingEntity foe, RandomSource random) {
        int critChance = (int) attacker.getAttributeValue(WotrAttributes.CRITICAL_CHANCE);
        float critMult = (float) attacker.getAttributeValue(WotrAttributes.CRITICAL_MULTIPLIER);
        int critAvoid = (int) foe.getAttributeValue(WotrAttributes.CRITICAL_AVOIDANCE);

        float damage = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);

        if(critChance - critAvoid > 100) {
            int i;
            for (i = critChance; i >= 100; i = i - 100) ;
            {
                if (i > 100 || i > random.nextInt(0, 100)) {
                    damage = damage * critMult;
                }
            }
        }
        else if(critChance - critAvoid < 100) {
            if((critChance - critAvoid) > random.nextInt(1, critAvoid)) {
                damage = damage * critMult;
            }
        }
        return damage;
    }
}
