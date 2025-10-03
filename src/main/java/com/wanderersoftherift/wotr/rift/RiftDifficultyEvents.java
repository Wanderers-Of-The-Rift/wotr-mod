package com.wanderersoftherift.wotr.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterData;
import com.wanderersoftherift.wotr.init.WotrDataMaps;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class RiftDifficultyEvents {

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
            serverLevel.registryAccess().lookupOrThrow(Registries.ATTRIBUTE).asHolderIdMap().forEach(attribute -> {
                var parameter = attribute.getData(WotrDataMaps.DIFFICULTY_SCALING);
                if (parameter == null) {
                    return;
                }
                var value = riftData.getParameter(parameter.getKey());
                if (value == null) {
                    return;
                }
                updateAttribute(mob, attribute, value.get());
                if ("minecraft:max_health".equals(attribute.getKey().location().toString())) {
                    livingEntity.setHealth(livingEntity.getMaxHealth());
                }
            });
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
