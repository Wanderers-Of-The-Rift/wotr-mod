package com.wanderersoftherift.wotr.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class RiftDifficultyEvents {

    @SubscribeEvent
    public static void onEntitySpawned(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.getEntity() instanceof Player) {
            return;
        }
        if (event.getLevel() instanceof ServerLevel serverLevel && RiftData.isRift(serverLevel)
                && event.getEntity() instanceof LivingEntity livingEntity) {
            applyDifficultyToEntity(livingEntity, serverLevel);
        }
    }

    private static void applyDifficultyToEntity(LivingEntity livingEntity, ServerLevel serverLevel) {
        int tier = RiftData.get(serverLevel).getTier();
        if (livingEntity instanceof Mob mob) {
            updateAttribute(mob, Attributes.ATTACK_DAMAGE, getDamageMultiplier(tier));
            updateAttribute(mob, Attributes.MAX_HEALTH, getHealthMultiplier(tier));
            updateAttribute(mob, Attributes.MOVEMENT_SPEED, getSpeedMultiplier(tier));
        }
    }

    private static void updateAttribute(LivingEntity livingEntity, Holder<Attribute> attribute, double tier) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance == null) {
            return;
        }
        attributeInstance.setBaseValue(attributeInstance.getBaseValue() * tier);
    }

    private static double getSpeedMultiplier(int tier) {
        return tier * 0.02 + 1;
    }

    private static double getHealthMultiplier(int tier) {
        return tier * 0.3 + 1;
    }

    private static double getDamageMultiplier(int tier) {
        return tier * 0.15 + 1;
    }
}
