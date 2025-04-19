package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectMarker;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgrade;
import com.wanderersoftherift.wotr.item.implicit.ImplicitConfig;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    /// Datapack registries

    public static final ResourceKey<Registry<AbstractAbility>> ABILITY_REGISTRY = ResourceKey
            .createRegistryKey(WanderersOfTheRift.id("abilities"));
    public static final ResourceKey<Registry<AbilityUpgrade>> ABILITY_UPGRADE_REGISTRY = ResourceKey
            .createRegistryKey(WanderersOfTheRift.id("ability_upgrade"));
    public static final ResourceKey<Registry<EffectMarker>> EFFECT_MARKER_REGISTRY = ResourceKey
            .createRegistryKey(WanderersOfTheRift.id("effect_marker"));

    @SubscribeEvent
    static void registerRegistries(NewRegistryEvent event) {
        event.register(ModModifierEffects.MODIFIER_TYPE_REGISTRY);
        event.register(ModInputBlockStateTypes.INPUT_BLOCKSTATE_TYPE_REGISTRY);
        event.register(ModOutputBlockStateTypes.OUTPUT_BLOCKSTATE_TYPE_REGISTRY);
        event.register(ModOngoingObjectiveTypes.ONGOING_OBJECTIVE_TYPE_REGISTRY);
        event.register(ModAbilityTypes.ABILITY_TYPES_REGISTRY);
        event.register(ModEffects.EFFECTS_REGISTRY);
        event.register(ModTargetingTypes.EFFECT_TARGETING_REGISTRY);
    }

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModModifierEffects.MODIFIER_EFFECT_KEY, AbstractModifierEffect.DIRECT_CODEC,
                AbstractModifierEffect.DIRECT_CODEC);
        event.dataPackRegistry(ModDatapackRegistries.MODIFIER_KEY, Modifier.DIRECT_CODEC, Modifier.DIRECT_CODEC);
        event.dataPackRegistry(ModRiftThemes.RIFT_THEME_KEY, RiftTheme.DIRECT_CODEC, RiftTheme.DIRECT_SYNC_CODEC);
        event.dataPackRegistry(ModDatapackRegistries.RUNEGEM_DATA_KEY, RunegemData.CODEC, RunegemData.CODEC);
        event.dataPackRegistry(ModDatapackRegistries.GEAR_IMPLICITS_CONFIG, ImplicitConfig.CODEC, ImplicitConfig.CODEC);
        event.dataPackRegistry(ABILITY_UPGRADE_REGISTRY, AbilityUpgrade.CODEC, AbilityUpgrade.CODEC);
        event.dataPackRegistry(EFFECT_MARKER_REGISTRY, EffectMarker.CODEC, EffectMarker.CODEC);
        event.dataPackRegistry(ABILITY_REGISTRY, AbstractAbility.DIRECT_CODEC, AbstractAbility.DIRECT_CODEC);
    }
}
