package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import com.wanderersoftherift.wotr.abilities.AbilityResource;
import com.wanderersoftherift.wotr.abilities.EffectMarker;
import com.wanderersoftherift.wotr.abilities.effects.AbilityEffect;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.shape.TargetAreaShape;
import com.wanderersoftherift.wotr.abilities.triggers.TrackableTrigger;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgrade;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyReward;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyTask;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.BattleTask;
import com.wanderersoftherift.wotr.core.guild.GuildInfo;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerType;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.core.quest.Goal;
import com.wanderersoftherift.wotr.core.quest.GoalProvider;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import com.wanderersoftherift.wotr.core.rift.RiftConfigDataType;
import com.wanderersoftherift.wotr.core.rift.RiftGenerationConfig;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RegisteredRiftParameter;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.entity.mob.RiftMobVariantData;
import com.wanderersoftherift.wotr.entity.npc.MobInteraction;
import com.wanderersoftherift.wotr.entity.player.PrimaryStatistic;
import com.wanderersoftherift.wotr.gui.menu.character.CharacterMenuItem;
import com.wanderersoftherift.wotr.item.implicit.ImplicitConfig;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import com.wanderersoftherift.wotr.util.listedit.EditType;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftPostProcessingStep;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.RiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.input.InputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.OutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.theme.ThemeSource;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.RiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.CorridorValidator;
import com.wanderersoftherift.wotr.world.level.levelgen.template.SerializableRiftGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD)
public class WotrRegistries {

    public static final Registry<MapCodec<? extends AbilityRequirement>> ABILITY_REQUIREMENT_TYPES = new RegistryBuilder<>(
            Keys.ABILITY_REQUIREMENT_TYPES).create();
    public static final Registry<MapCodec<? extends Ability>> ABILITY_TYPES = new RegistryBuilder<>(
            Keys.ABILITY_TYPES).create();
    public static final Registry<MapCodec<? extends AbilityEffect>> EFFECTS = new RegistryBuilder<>(
            Keys.EFFECTS).create();
    public static final Registry<DualCodec<? extends WotrEquipmentSlot>> EQUIPMENT_SLOTS = new RegistryBuilder<>(
            Keys.EQUIPMENT_SLOTS).sync(true).create();
    public static final Registry<MapCodec<? extends InputBlockState>> INPUT_BLOCKSTATE_TYPES = new RegistryBuilder<>(
            Keys.INPUT_BLOCKSTATE_TYPES).create();
    public static final Registry<MapCodec<? extends ModifierEffect>> MODIFIER_TYPES = new RegistryBuilder<>(
            Keys.MODIFIER_EFFECT_TYPES).create();
    public static final Registry<MapCodec<? extends ObjectiveType>> OBJECTIVE_TYPES = new RegistryBuilder<>(
            Keys.OBJECTIVE_TYPES).create();
    public static final Registry<MapCodec<? extends OngoingObjective>> ONGOING_OBJECTIVE_TYPES = new RegistryBuilder<>(
            Keys.ONGOING_OBJECTIVE_TYPES).create();
    public static final Registry<MapCodec<? extends OutputBlockState>> OUTPUT_BLOCKSTATE_TYPES = new RegistryBuilder<>(
            Keys.OUTPUT_BLOCKSTATE_TYPES).create();
    public static final Registry<MapCodec<? extends AbilityTargeting>> EFFECT_TARGETING_TYPES = new RegistryBuilder<>(
            Keys.EFFECT_TARGETING_TYPES).create();
    public static final Registry<ContainerType> CONTAINER_TYPE = new RegistryBuilder<>(
            Keys.CONTAINER_TYPES).create();
    public static final Registry<CharacterMenuItem> CHARACTER_MENU_ITEMS = new RegistryBuilder<>(
            Keys.CHARACTER_MENU_ITEMS
    ).sync(true).create();
    public static final Registry<MapCodec<? extends GoalProvider>> GOAL_PROVIDER_TYPES = new RegistryBuilder<>(
            Keys.GOAL_PROVIDER_TYPES).create();
    public static final Registry<DualCodec<? extends Goal>> GOAL_TYPES = new RegistryBuilder<>(Keys.GOAL_TYPES)
            .sync(true)
            .create();
    public static final Registry<MapCodec<? extends RewardProvider>> REWARD_PROVIDER_TYPES = new RegistryBuilder<>(
            Keys.REWARD_PROVIDER_TYPES).create();
    public static final Registry<DualCodec<? extends Reward>> REWARD_TYPES = new RegistryBuilder<>(Keys.REWARD_TYPES)
            .sync(true)
            .create();
    public static final Registry<MapCodec<? extends MobInteraction>> MOB_INTERACTIONS = new RegistryBuilder<>(
            Keys.MOB_INTERACTIONS).create();
    public static final Registry<MapCodec<? extends RiftLayout.Factory>> LAYOUT_TYPES = new RegistryBuilder<>(
            Keys.LAYOUT_TYPES).create();
    public static final Registry<MapCodec<? extends LayeredRiftLayout.LayoutLayer.Factory>> LAYOUT_LAYER_TYPES = new RegistryBuilder<>(
            Keys.LAYOUT_LAYER_TYPES).create();
    public static final Registry<MapCodec<? extends RiftShape>> RIFT_SHAPE_TYPES = new RegistryBuilder<>(
            Keys.RIFT_SHAPE_TYPES).create();
    public static final Registry<MapCodec<? extends RegisteredRiftParameter>> RIFT_PARAMETER_TYPES = new RegistryBuilder<>(
            Keys.RIFT_PARAMETER_TYPES).create();
    public static final Registry<MapCodec<? extends RiftRoomGenerator.Factory>> RIFT_ROOM_GENERATOR_FACTORY_TYPES = new RegistryBuilder<>(
            Keys.RIFT_ROOM_GENERATOR_FACTORY_TYPES).create();
    public static final Registry<MapCodec<? extends JigsawListProcessor>> JIGSAW_LIST_PROCESSOR_TYPES = new RegistryBuilder<>(
            Keys.JIGSAW_LIST_PROCESSOR_TYPES).create();
    public static final Registry<MapCodec<? extends SerializableRiftGeneratable>> RIFT_BUILTIN_GENERATABLE_TYPES = new RegistryBuilder<>(
            Keys.RIFT_BUILTIN_GENERATABLE_TYPES).create();
    public static final Registry<RiftConfigDataType<?>> RIFT_CONFIG_DATA_TYPES = new RegistryBuilder<>(
            Keys.RIFT_CONFIG_DATA_TYPES).create();
    public static final Registry<MapCodec<? extends CorridorValidator>> RIFT_CORRIDOR_VALIDATORS = new RegistryBuilder<>(
            Keys.RIFT_CORRIDOR_VALIDATORS).create();
    public static final Registry<MapCodec<? extends RiftPostProcessingStep>> RIFT_POST_STEPS = new RegistryBuilder<>(
            Keys.RIFT_POST_STEPS).create();
    public static final Registry<MapCodec<? extends ThemeSource>> THEME_SOURCE_TYPE = new RegistryBuilder<>(
            Keys.THEME_SOURCE_TYPES).create();
    public static final Registry<EditType<?>> EDIT_TYPES = new RegistryBuilder<>(
            Keys.EDIT_TYPES).create();
    public static final Registry<TrackableTrigger.TriggerType<?>> TRACKABLE_TRIGGERS = new RegistryBuilder<>(
            Keys.TRACKED_ABILITY_TRIGGERS).sync(true).create();
    public static final Registry<DualCodec<? extends AbilitySource>> ABILITY_SOURCES = new RegistryBuilder<>(
            Keys.ABILITY_SOURCES).sync(true).create();
    public static final Registry<DualCodec<? extends ModifierSource>> MODIFIER_SOURCES = new RegistryBuilder<>(
            Keys.MODIFIER_SOURCES).sync(true).create();
    public static final Registry<MapCodec<? extends TargetAreaShape>> TARGET_AREA_SHAPES = new RegistryBuilder<>(
            Keys.TARGET_AREA_SHAPES).create();
    public static final Registry<AnomalyTask.AnomalyTaskType<?>> ANOMALY_TASK_TYPE = new RegistryBuilder<>(
            Keys.ANOMALY_TASK_TYPE).sync(true).create();
    public static final Registry<MapCodec<? extends BattleTask.SpawnFunction>> SPAWN_FUNCTION_TYPES = new RegistryBuilder<>(
            Keys.SPAWN_FUNCTION_TYPES).sync(true).create();

    public static final class Keys {

        public static final ResourceKey<Registry<ContainerType>> CONTAINER_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("container_type"));
        public static final ResourceKey<Registry<Currency>> CURRENCIES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("currency"));
        public static final ResourceKey<Registry<DualCodec<? extends WotrEquipmentSlot>>> EQUIPMENT_SLOTS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("equipment_slot_type"));
        public static final ResourceKey<Registry<ImplicitConfig>> GEAR_IMPLICITS_CONFIG = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("implicit_config"));
        public static final ResourceKey<Registry<Modifier>> MODIFIERS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("modifier"));
        public static final ResourceKey<Registry<ObjectiveType>> OBJECTIVES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("objective"));
        public static final ResourceKey<Registry<RunegemData>> RUNEGEM_DATA = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("runegem_data"));

        // Riftgen
        public static final ResourceKey<Registry<MapCodec<? extends InputBlockState>>> INPUT_BLOCKSTATE_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("input_blockstate_type"));
        public static final ResourceKey<Registry<MapCodec<? extends ModifierEffect>>> MODIFIER_EFFECT_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("modifier_effect_type"));
        public static final ResourceKey<Registry<ModifierEffect>> MODIFIER_EFFECTS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("modifier_effect"));
        public static final ResourceKey<Registry<MapCodec<? extends ObjectiveType>>> OBJECTIVE_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("objective_type"));
        public static final ResourceKey<Registry<MapCodec<? extends OngoingObjective>>> ONGOING_OBJECTIVE_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("ongoing_objective_type"));
        public static final ResourceKey<Registry<MapCodec<? extends OutputBlockState>>> OUTPUT_BLOCKSTATE_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("output_blockstate_type"));
        public static final ResourceKey<Registry<PrimaryStatistic>> PRIMARY_STATISTICS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("primary_statistic"));
        public static final ResourceKey<Registry<RiftTheme>> RIFT_THEMES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("rift_theme"));
        public static final ResourceKey<Registry<RiftParameter>> RIFT_PARAMETER_CONFIGS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("rift_parameter"));
        public static final ResourceKey<Registry<MapCodec<? extends RegisteredRiftParameter>>> RIFT_PARAMETER_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("rift_parameter_type"));
        public static final ResourceKey<Registry<RiftMobVariantData>> MOB_VARIANTS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("mob_variant"));

        public static final ResourceKey<Registry<GuildInfo>> GUILDS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("guild"));
        public static final ResourceKey<Registry<CharacterMenuItem>> CHARACTER_MENU_ITEMS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("character_menu_item"));

        // Abilities
        public static final ResourceKey<Registry<Ability>> ABILITIES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("abilities"));
        public static final ResourceKey<Registry<MapCodec<? extends AbilityRequirement>>> ABILITY_REQUIREMENT_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("ability_requirement_type"));
        public static final ResourceKey<Registry<MapCodec<? extends Ability>>> ABILITY_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("ability_types"));
        public static final ResourceKey<Registry<AbilityUpgrade>> ABILITY_UPGRADES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("ability_upgrade"));
        public static final ResourceKey<Registry<TrackableTrigger.TriggerType<?>>> TRACKED_ABILITY_TRIGGERS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("trackable_trigger"));
        public static final ResourceKey<Registry<MapCodec<? extends AbilityTargeting>>> EFFECT_TARGETING_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("effect_targeting"));
        public static final ResourceKey<Registry<MapCodec<? extends AbilityEffect>>> EFFECTS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("effects"));
        public static final ResourceKey<Registry<EffectMarker>> EFFECT_MARKERS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("effect_marker"));
        public static final ResourceKey<Registry<MapCodec<? extends TargetAreaShape>>> TARGET_AREA_SHAPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("shape"));

        // Quests
        public static final ResourceKey<Registry<Quest>> QUESTS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("quest"));
        public static final ResourceKey<Registry<MapCodec<? extends GoalProvider>>> GOAL_PROVIDER_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("goal_provider_type"));
        public static final ResourceKey<Registry<DualCodec<? extends Goal>>> GOAL_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("goal_type"));
        public static final ResourceKey<Registry<MapCodec<? extends RewardProvider>>> REWARD_PROVIDER_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("reward_provider_type"));
        public static final ResourceKey<Registry<DualCodec<? extends Reward>>> REWARD_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("reward_type"));
        public static final ResourceKey<Registry<MapCodec<? extends RiftLayout.Factory>>> LAYOUT_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("worldgen/layout"));
        public static final ResourceKey<Registry<MapCodec<? extends LayeredRiftLayout.LayoutLayer.Factory>>> LAYOUT_LAYER_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("worldgen/layout_layer"));
        public static final ResourceKey<Registry<MapCodec<? extends RiftShape>>> RIFT_SHAPE_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("worldgen/rift_shape"));
        public static final ResourceKey<Registry<MapCodec<? extends RiftRoomGenerator.Factory>>> RIFT_ROOM_GENERATOR_FACTORY_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("worldgen/rift_room_generator_factory"));
        public static final ResourceKey<Registry<MapCodec<? extends JigsawListProcessor>>> JIGSAW_LIST_PROCESSOR_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("worldgen/jigsaw_list_processor"));
        public static final ResourceKey<Registry<MapCodec<? extends SerializableRiftGeneratable>>> RIFT_BUILTIN_GENERATABLE_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("worldgen/rift_builtin_generatable"));
        public static final ResourceKey<Registry<RiftConfigDataType<?>>> RIFT_CONFIG_DATA_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("worldgen/rift_config_custom_data"));
        public static final ResourceKey<Registry<MapCodec<? extends CorridorValidator>>> RIFT_CORRIDOR_VALIDATORS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("worldgen/rift_corridor_validator"));
        public static final ResourceKey<Registry<MapCodec<? extends RiftPostProcessingStep>>> RIFT_POST_STEPS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("worldgen/rift_post_steps"));
        public static final ResourceKey<Registry<MapCodec<? extends ThemeSource>>> THEME_SOURCE_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("worldgen/rift_theme_source"));
        public static final ResourceKey<Registry<RiftGenerationConfig>> GENERATOR_PRESETS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("worldgen/rift_generator_preset"));
        public static final ResourceKey<Registry<EditType<?>>> EDIT_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("list_edits"));
        public static final ResourceKey<Registry<DualCodec<? extends AbilitySource>>> ABILITY_SOURCES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("ability_source"));
        public static final ResourceKey<Registry<DualCodec<? extends ModifierSource>>> MODIFIER_SOURCES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("modifier_source"));
        public static final ResourceKey<Registry<AbilityResource>> ABILITY_RESOURCES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("ability_resources"));

        // Mobs
        public static final ResourceKey<Registry<MapCodec<? extends MobInteraction>>> MOB_INTERACTIONS = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("mob_interactions"));
        public static final ResourceKey<Registry<MapCodec<? extends BattleTask.SpawnFunction>>> SPAWN_FUNCTION_TYPES = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("spawn_function_type"));
        public static final ResourceKey<Registry<AnomalyTask<?>>> ANOMALY_TASK = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("anomaly_task"));
        public static final ResourceKey<Registry<AnomalyTask.AnomalyTaskType<?>>> ANOMALY_TASK_TYPE = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("anomaly_tasks_type"));
        public static final ResourceKey<Registry<AnomalyReward>> ANOMALY_REWARD = ResourceKey
                .createRegistryKey(WanderersOfTheRift.id("anomaly_reward"));

        private Keys() {
        }
    }

    @SubscribeEvent
    static void registerRegistries(NewRegistryEvent event) {
        event.register(MODIFIER_TYPES);
        event.register(INPUT_BLOCKSTATE_TYPES);
        event.register(OUTPUT_BLOCKSTATE_TYPES);
        event.register(OBJECTIVE_TYPES);
        event.register(ONGOING_OBJECTIVE_TYPES);
        event.register(ABILITY_REQUIREMENT_TYPES);
        event.register(ABILITY_TYPES);
        event.register(EFFECTS);
        event.register(EFFECT_TARGETING_TYPES);
        event.register(EQUIPMENT_SLOTS);
        event.register(CONTAINER_TYPE);
        event.register(CHARACTER_MENU_ITEMS);
        event.register(GOAL_PROVIDER_TYPES);
        event.register(GOAL_TYPES);
        event.register(REWARD_PROVIDER_TYPES);
        event.register(REWARD_TYPES);
        event.register(MOB_INTERACTIONS);
        event.register(EDIT_TYPES);
        event.register(TRACKABLE_TRIGGERS);
        event.register(ABILITY_SOURCES);
        event.register(MODIFIER_SOURCES);
        event.register(TARGET_AREA_SHAPES);
        event.register(RIFT_PARAMETER_TYPES);

        // worldgen registries
        event.register(THEME_SOURCE_TYPE);
        event.register(LAYOUT_TYPES);
        event.register(LAYOUT_LAYER_TYPES);
        event.register(RIFT_SHAPE_TYPES);
        event.register(RIFT_ROOM_GENERATOR_FACTORY_TYPES);
        event.register(JIGSAW_LIST_PROCESSOR_TYPES);
        event.register(RIFT_BUILTIN_GENERATABLE_TYPES);
        event.register(RIFT_CONFIG_DATA_TYPES);
        event.register(RIFT_CORRIDOR_VALIDATORS);
        event.register(RIFT_POST_STEPS);
        event.register(ANOMALY_TASK_TYPE);
        event.register(SPAWN_FUNCTION_TYPES);
    }

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(Keys.MODIFIER_EFFECTS, ModifierEffect.DIRECT_CODEC, ModifierEffect.DIRECT_CODEC);
        event.dataPackRegistry(Keys.MODIFIERS, Modifier.DIRECT_CODEC, Modifier.DIRECT_CODEC);
        event.dataPackRegistry(Keys.RIFT_THEMES, RiftTheme.DIRECT_CODEC, RiftTheme.DIRECT_SYNC_CODEC);
        event.dataPackRegistry(Keys.RIFT_PARAMETER_CONFIGS, RiftParameter.DEFINITION_CODEC,
                RiftParameter.DEFINITION_CODEC);
        event.dataPackRegistry(Keys.RUNEGEM_DATA, RunegemData.CODEC, RunegemData.CODEC);
        event.dataPackRegistry(Keys.GEAR_IMPLICITS_CONFIG, ImplicitConfig.CODEC, ImplicitConfig.CODEC);
        event.dataPackRegistry(Keys.ABILITY_UPGRADES, AbilityUpgrade.CODEC, AbilityUpgrade.CODEC);
        event.dataPackRegistry(Keys.EFFECT_MARKERS, EffectMarker.DIRECT_CODEC, EffectMarker.DIRECT_CODEC);
        event.dataPackRegistry(Keys.ABILITIES, Ability.DIRECT_CODEC, Ability.DIRECT_CODEC);
        event.dataPackRegistry(Keys.OBJECTIVES, ObjectiveType.DIRECT_CODEC, ObjectiveType.DIRECT_CODEC);
        event.dataPackRegistry(Keys.MOB_VARIANTS, RiftMobVariantData.CODEC, RiftMobVariantData.CODEC);
        event.dataPackRegistry(Keys.CURRENCIES, Currency.DIRECT_CODEC, Currency.DIRECT_CODEC);
        event.dataPackRegistry(Keys.GUILDS, GuildInfo.DIRECT_CODEC, GuildInfo.DIRECT_CODEC);
        event.dataPackRegistry(Keys.QUESTS, Quest.DIRECT_CODEC, Quest.DIRECT_CODEC);
        event.dataPackRegistry(Keys.PRIMARY_STATISTICS, PrimaryStatistic.DIRECT_CODEC, PrimaryStatistic.DIRECT_CODEC);
        event.dataPackRegistry(Keys.GENERATOR_PRESETS, RiftGenerationConfig.CODEC, RiftGenerationConfig.CODEC);
        event.dataPackRegistry(Keys.ANOMALY_TASK, AnomalyTask.DIRECT_CODEC, AnomalyTask.DIRECT_CODEC);
        event.dataPackRegistry(Keys.ANOMALY_REWARD, AnomalyReward.DIRECT_CODEC, AnomalyReward.DIRECT_CODEC);
        event.dataPackRegistry(Keys.ABILITY_RESOURCES, AbilityResource.DIRECT_CODEC, AbilityResource.DIRECT_CODEC);
    }
}
