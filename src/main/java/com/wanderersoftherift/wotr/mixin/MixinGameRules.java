package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.mixinextension.WotRGameRules;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(GameRules.class)
public class MixinGameRules implements WotRGameRules {

    @Final
    @Mutable
    @Shadow
    private Map<GameRules.Key<?>, GameRules.Value<?>> rules;

    @Final
    @Shadow
    private FeatureFlagSet enabledFeatures;

    @Unique public <T extends GameRules.Value<T>> T wotrSetRuleFresh(GameRules.Key<T> key, T value) {
        this.rules.put(key, value);
        return value;
    }

    @Shadow
    private static Stream<Map.Entry<GameRules.Key<?>, GameRules.Type<?>>> availableRules(
            FeatureFlagSet enabledFeatures) {
        return null;
    }

    @Unique public void wotrMakeMutable() {
        rules = rules.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue));
    }
}
