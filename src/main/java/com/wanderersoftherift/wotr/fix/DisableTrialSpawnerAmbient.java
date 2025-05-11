package com.wanderersoftherift.wotr.fix;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

import java.util.List;

/**
 * Disables play of trial spawner ambient sounds - these play at any distance and the rifts are full of them so they
 * quickly max available sounds.
 * <p>
 * Might be worth converting to a mixin for performance in the future
 * </p>
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class DisableTrialSpawnerAmbient {

    private static final List<ResourceLocation> TRIAL_AMBIENT_SOUNDS = List
            .of(SoundEvents.TRIAL_SPAWNER_AMBIENT_OMINOUS.location(), SoundEvents.TRIAL_SPAWNER_AMBIENT.location());

    private DisableTrialSpawnerAmbient() {

    }

    @SubscribeEvent
    public static void catchPlaySound(PlaySoundEvent event) {
        if (TRIAL_AMBIENT_SOUNDS.contains(event.getOriginalSound().getLocation())) {
            event.setSound(null);
        }
    }
}
