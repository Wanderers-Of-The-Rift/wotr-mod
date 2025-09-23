package com.wanderersoftherift.wotr.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.core.rift.RiftParameterData;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class RiftDifficultyEvents {

    public static final ResourceLocation MOB_DAMAGE_MULTIPLIER = WanderersOfTheRift.id(
            "mob_difficulty/damage_multiplier");
    public static final ResourceLocation MOB_HEALTH_MULTIPLIER = WanderersOfTheRift.id(
            "mob_difficulty/health_multiplier");
    public static final ResourceLocation MOB_SPEED_MULTIPLIER = WanderersOfTheRift.id(
            "mob_difficulty/speed_multiplier");

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onMobSpawning(FinalizeSpawnEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (event.getLevel() instanceof ServerLevel serverLevel && RiftLevelManager.isRift(serverLevel)) {
            applyDifficultyToEntity(event.getEntity(), serverLevel);
        }
    }

    private static void applyDifficultyToEntity(LivingEntity livingEntity, ServerLevel serverLevel) {
        var riftData = RiftParameterData.forLevel(serverLevel);
        if (livingEntity instanceof Mob mob && riftData != null) {
            var damage = riftData.getParameter(MOB_DAMAGE_MULTIPLIER);
            var health = riftData.getParameter(MOB_HEALTH_MULTIPLIER);
            var speed = riftData.getParameter(MOB_SPEED_MULTIPLIER);
            updateAttribute(mob, Attributes.ATTACK_DAMAGE, damage.get());
            updateAttribute(mob, Attributes.MAX_HEALTH, health.get());
            updateAttribute(mob, Attributes.MOVEMENT_SPEED, speed.get());
            livingEntity.setHealth(livingEntity.getMaxHealth());
        }
    }

    private static void updateAttribute(LivingEntity livingEntity, Holder<Attribute> attribute, double tier) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance == null) {
            return;
        }
        attributeInstance.setBaseValue(attributeInstance.getBaseValue() * tier);
    }
}
