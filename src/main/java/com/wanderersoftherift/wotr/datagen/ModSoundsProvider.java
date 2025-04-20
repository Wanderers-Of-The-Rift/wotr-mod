package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModSoundEvents;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class ModSoundsProvider extends SoundDefinitionsProvider {
    public ModSoundsProvider(PackOutput output) {
        super(output, WanderersOfTheRift.MODID);
    }

    @Override
    public void registerSounds() {
        add(ModSoundEvents.RIFT_OPEN.value(),
                SoundDefinition.definition()
                        .with(sound(WanderersOfTheRift.id("rift_open"), SoundDefinition.SoundType.SOUND))
                        .subtitle("subtitles." + WanderersOfTheRift.MODID + ".rift_open")
                        .replace(false));
    }
}
