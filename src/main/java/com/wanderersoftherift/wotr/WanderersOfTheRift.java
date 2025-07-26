package com.wanderersoftherift.wotr;

import com.mojang.logging.LogUtils;
import com.wanderersoftherift.wotr.config.ClientConfig;
import com.wanderersoftherift.wotr.gui.widget.lookup.GoalDisplays;
import com.wanderersoftherift.wotr.gui.widget.lookup.RewardDisplays;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrCharacterMenuItems;
import com.wanderersoftherift.wotr.init.WotrContainerTypes;
import com.wanderersoftherift.wotr.init.WotrCreativeTabs;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrEntities;
import com.wanderersoftherift.wotr.init.WotrEntityDataSerializers;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.init.WotrMobEffects;
import com.wanderersoftherift.wotr.init.WotrModifierEffectTypes;
import com.wanderersoftherift.wotr.init.WotrObjectiveTypes;
import com.wanderersoftherift.wotr.init.WotrOngoingObjectiveTypes;
import com.wanderersoftherift.wotr.init.WotrPayloadHandlers;
import com.wanderersoftherift.wotr.init.WotrSoundEvents;
import com.wanderersoftherift.wotr.init.ability.WotrAbilityTypes;
import com.wanderersoftherift.wotr.init.ability.WotrEffects;
import com.wanderersoftherift.wotr.init.ability.WotrTargetingTypes;
import com.wanderersoftherift.wotr.init.client.WotrConfigurableLayers;
import com.wanderersoftherift.wotr.init.client.WotrEmblemProviders;
import com.wanderersoftherift.wotr.init.loot.WotrLootItemConditionTypes;
import com.wanderersoftherift.wotr.init.loot.WotrLootItemFunctionTypes;
import com.wanderersoftherift.wotr.init.loot.WotrLootModifiers;
import com.wanderersoftherift.wotr.init.quest.WotrGoalTypes;
import com.wanderersoftherift.wotr.init.quest.WotrRewardTypes;
import com.wanderersoftherift.wotr.init.recipe.WotrRecipeCategories;
import com.wanderersoftherift.wotr.init.recipe.WotrRecipeDisplayTypes;
import com.wanderersoftherift.wotr.init.recipe.WotrRecipeSerializers;
import com.wanderersoftherift.wotr.init.recipe.WotrRecipeTypes;
import com.wanderersoftherift.wotr.init.recipe.WotrSlotDisplayTypes;
import com.wanderersoftherift.wotr.init.worldgen.WotrChunkGenerators;
import com.wanderersoftherift.wotr.init.worldgen.WotrInputBlockStateTypes;
import com.wanderersoftherift.wotr.init.worldgen.WotrOutputBlockStateTypes;
import com.wanderersoftherift.wotr.init.worldgen.WotrProcessors;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftLayoutLayers;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftLayouts;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftShapes;
import com.wanderersoftherift.wotr.interop.sophisticatedbackpacks.SophisticatedBackpackInterop;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftTemplates;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(WanderersOfTheRift.MODID)
public class WanderersOfTheRift {
    public static final String MODID = "wotr";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WanderersOfTheRift(IEventBus modEventBus, ModContainer modContainer) {
        // Vanilla elements
        WotrAttributes.WOTR_ATTRIBUTES.register(modEventBus);
        WotrBlocks.BLOCKS.register(modEventBus);
        WotrBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        WotrContainerTypes.CONTAINER_TYPES.register(modEventBus);
        WotrCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        WotrEntities.ENTITIES.register(modEventBus);
        WotrEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(modEventBus);
        WotrItems.ITEMS.register(modEventBus);
        WotrMenuTypes.MENUS.register(modEventBus);
        WotrMobEffects.MOB_EFFECTS.register(modEventBus);
        WotrSoundEvents.SOUND_EVENTS.register(modEventBus);

        // Loot
        WotrLootModifiers.GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        WotrLootItemFunctionTypes.LOOT_ITEM_FUNCTION_TYPES.register(modEventBus);
        WotrLootItemConditionTypes.LOOT_ITEM_CONDITION_TYPES.register(modEventBus);

        // Attachments and components
        WotrAttachments.ATTACHMENT_TYPES.register(modEventBus);
        WotrDataComponentType.DATA_COMPONENTS.register(modEventBus);

        // Rift generation
        WotrInputBlockStateTypes.INPUT_BLOCKSTATE_TYPES.register(modEventBus);
        WotrOutputBlockStateTypes.OUTPUT_BLOCKSTATE_TYPES.register(modEventBus);
        WotrProcessors.PROCESSORS.register(modEventBus);

        WotrRiftLayoutLayers.LAYOUT_LAYERS.register(modEventBus);
        WotrRiftLayouts.LAYOUTS.register(modEventBus);
        WotrRiftShapes.RIFT_SHAPES.register(modEventBus);

        // Abilities
        WotrAbilityTypes.ABILITY_TYPES.register(modEventBus);
        WotrEffects.EFFECTS.register(modEventBus);
        WotrTargetingTypes.TARGETING_TYPES.register(modEventBus);

        WotrGoalTypes.GOAL_PROVIDER_TYPES.register(modEventBus);
        WotrGoalTypes.GOAL_TYPES.register(modEventBus);
        WotrRewardTypes.REWARD_PROVIDER_TYPES.register(modEventBus);
        WotrRewardTypes.REWARD_TYPES.register(modEventBus);

        WotrModifierEffectTypes.MODIFIER_EFFECT_TYPES.register(modEventBus);
        WotrObjectiveTypes.OBJECTIVE_TYPES.register(modEventBus);
        WotrOngoingObjectiveTypes.ONGOING_OBJECTIVE_TYPES.register(modEventBus);
        WotrChunkGenerators.CHUNK_GENERATORS.register(modEventBus);

        // Recipes
        WotrRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        WotrRecipeTypes.RECIPE_TYPES.register(modEventBus);
        WotrRecipeCategories.RECIPE_BOOK_CATEGORIES.register(modEventBus);
        WotrSlotDisplayTypes.SLOT_DISPLAY_TYPES.register(modEventBus);
        WotrRecipeDisplayTypes.RECIPE_DISPLAY_TYPES.register(modEventBus);

        if (FMLEnvironment.dist.isClient()) {
            WotrConfigurableLayers.LAYERS.register(modEventBus);
            WotrConfigurableLayers.VANILLA_LAYERS.register(modEventBus);
            WotrEmblemProviders.PROVIDERS.register(modEventBus);
            modEventBus.addListener(this::registerWidgetLookups);
        }

        WotrCharacterMenuItems.MENU_ITEMS.register(modEventBus);

        modEventBus.addListener(this::loadInterop);
        modEventBus.addListener(this::registerInterop);
        modEventBus.addListener(WotrPayloadHandlers::registerPayloadHandlers);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /**
     * Helper method to get a {@code ResourceLocation} with our Mod Id and a passed in name
     *
     * @param name the name to create the {@code ResourceLocation} with
     * @return A {@code ResourceLocation} with the given name
     */
    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    /**
     * Helper method to get a translationId string containing our mod id.
     *
     * @param category The category of the translationId (becomes a prefix)
     * @param item     The translationId item
     * @return A combination of category, our mod id and the item. e.g. if category is "item" and item is
     *         "nosering.description" the result is "item.wotr.nosering.description"
     */
    public static String translationId(String category, String item) {
        return category + "." + MODID + "." + item;
    }

    /**
     * Helper method to get a translationId string containing any mod id.
     *
     * @param category The category of the translationId (becomes a prefix)
     * @param item     The ResourceLocation item
     * @return A combination of category, the mod id and the item. e.g. if category is "item" and item is
     *         "wotr:nosering.description" the result is "item.wotr.nosering.description"
     */
    public static String translationId(String category, ResourceLocation item) {
        return category + "." + item.getNamespace() + "." + item.getPath();
    }

    /**
     * Helper method to get a {@code TagKey} with our Mod Id and a passed in name
     *
     * @param name the name to create the {@code TagKey} with
     * @return A {@code TagKey} with the given name
     */
    public static <T> TagKey<T> tagId(ResourceKey<? extends Registry<T>> registry, String name) {
        return TagKey.create(registry, id(name));
    }

    private void loadInterop(final FMLCommonSetupEvent event) {
        ModList.get().getModContainerById("sophisticatedbackpacks").ifPresent(x -> SophisticatedBackpackInterop.load());
    }

    private void registerInterop(RegisterEvent event) {
        ModList.get()
                .getModContainerById("sophisticatedbackpacks")
                .ifPresent(x -> SophisticatedBackpackInterop.register(event));
    }

    @SubscribeEvent
    private void registerServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(id("invalidate_caches/rift_templates"), RiftTemplates.RELOAD_LISTENER);
        event.addListener(id("invalidate_caches/room_randomizer"), RoomRandomizerImpl.RELOAD_LISTENER);
    }

    private void registerWidgetLookups(final FMLClientSetupEvent event) {
        RewardDisplays.init();
        GoalDisplays.init();
    }

}
