package com.wanderersoftherift.wotr.client;

import com.google.common.collect.ImmutableMap;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.config.ClientConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

import java.util.Map;

/**
 * Events related to accessibility
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class AccessibilityClientEvents {

    private static final Map<SoundEvent, SoundEvent> ARACHNOPHOBIA_REPLACEMENT_SOUNDS = ImmutableMap
            .<SoundEvent, SoundEvent>builder()
            .put(SoundEvents.SPIDER_AMBIENT, SoundEvents.TURTLE_AMBIENT_LAND)
            .put(SoundEvents.SPIDER_DEATH, SoundEvents.TURTLE_DEATH)
            .put(SoundEvents.SPIDER_HURT, SoundEvents.TURTLE_HURT)
            .put(SoundEvents.SPIDER_STEP, SoundEvents.TURTLE_SHAMBLE)
            .build();

    @SubscribeEvent
    public static void replaceSounds(PlayLevelSoundEvent.AtEntity event) {
        if (!event.getEntity().level().isClientSide()) {
            return;
        }
        if (ClientConfig.ACCESSIBILITY_ARACHNOPHOBIA.getAsBoolean()) {
            Registry<SoundEvent> soundEvents = event.getEntity()
                    .level()
                    .registryAccess()
                    .lookupOrThrow(Registries.SOUND_EVENT);
            SoundEvent newSound = ARACHNOPHOBIA_REPLACEMENT_SOUNDS.get(event.getSound().value());
            if (newSound != null) {
                event.setSound(soundEvents.wrapAsHolder(newSound));
            }
        }
    }

    @SubscribeEvent
    public static void replaceSounds(PlayLevelSoundEvent.AtPosition event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        if (ClientConfig.ACCESSIBILITY_ARACHNOPHOBIA.getAsBoolean()) {
            Registry<SoundEvent> soundEvents = event.getLevel().registryAccess().lookupOrThrow(Registries.SOUND_EVENT);
            SoundEvent newSound = ARACHNOPHOBIA_REPLACEMENT_SOUNDS.get(event.getSound().value());
            if (newSound != null) {
                event.setSound(soundEvents.wrapAsHolder(newSound));
            }
        }
    }
}
