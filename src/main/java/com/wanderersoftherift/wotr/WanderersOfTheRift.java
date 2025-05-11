package com.wanderersoftherift.wotr;

import com.mojang.logging.LogUtils;
import com.wanderersoftherift.wotr.commands.AbilityCommands;
import com.wanderersoftherift.wotr.commands.DebugCommands;
import com.wanderersoftherift.wotr.commands.EssenceCommands;
import com.wanderersoftherift.wotr.commands.HudCommands;
import com.wanderersoftherift.wotr.commands.InventorySnapshotCommands;
import com.wanderersoftherift.wotr.commands.RiftCommands;
import com.wanderersoftherift.wotr.commands.RiftKeyCommands;
import com.wanderersoftherift.wotr.commands.RiftMapCommands;
import com.wanderersoftherift.wotr.commands.SpawnPieceCommand;
import com.wanderersoftherift.wotr.config.ClientConfig;
import com.wanderersoftherift.wotr.init.ModAbilityTypes;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.init.ModAttributes;
import com.wanderersoftherift.wotr.init.ModBlockEntities;
import com.wanderersoftherift.wotr.init.ModBlocks;
import com.wanderersoftherift.wotr.init.ModChunkGenerators;
import com.wanderersoftherift.wotr.init.ModCommands;
import com.wanderersoftherift.wotr.init.ModCreativeTabs;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModEffects;
import com.wanderersoftherift.wotr.init.ModEntities;
import com.wanderersoftherift.wotr.init.ModEntityDataSerializers;
import com.wanderersoftherift.wotr.init.ModInputBlockStateTypes;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.init.ModLootItemConditionTypes;
import com.wanderersoftherift.wotr.init.ModLootItemFunctionTypes;
import com.wanderersoftherift.wotr.init.ModLootModifiers;
import com.wanderersoftherift.wotr.init.ModMenuTypes;
import com.wanderersoftherift.wotr.init.ModMobEffects;
import com.wanderersoftherift.wotr.init.ModModifierEffects;
import com.wanderersoftherift.wotr.init.ModObjectiveTypes;
import com.wanderersoftherift.wotr.init.ModOngoingObjectiveTypes;
import com.wanderersoftherift.wotr.init.ModOutputBlockStateTypes;
import com.wanderersoftherift.wotr.init.ModPayloadHandlers;
import com.wanderersoftherift.wotr.init.ModProcessors;
import com.wanderersoftherift.wotr.init.ModSoundEvents;
import com.wanderersoftherift.wotr.init.ModTargetingTypes;
import com.wanderersoftherift.wotr.init.client.ModConfigurableLayers;
import com.wanderersoftherift.wotr.interop.sophisticatedbackpacks.SophisticatedBackpackInterop;
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
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(WanderersOfTheRift.MODID)
public class WanderersOfTheRift {
    public static final String MODID = "wotr";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WanderersOfTheRift(IEventBus modEventBus, ModContainer modContainer) {
        // Vanilla elements
        ModAttributes.ATTRIBUTES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCommands.COMMAND_ARGUMENT_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModMobEffects.MOB_EFFECTS.register(modEventBus);
        ModSoundEvents.SOUND_EVENTS.register(modEventBus);

        // Loot
        ModLootModifiers.GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        ModLootItemFunctionTypes.LOOT_ITEM_FUNCTION_TYPES.register(modEventBus);
        ModLootItemConditionTypes.LOOT_ITEM_CONDITION_TYPES.register(modEventBus);

        // Attachments and components
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModDataComponentType.DATA_COMPONENTS.register(modEventBus);

        // Rift generation
        ModInputBlockStateTypes.INPUT_BLOCKSTATE_TYPES.register(modEventBus);
        ModOutputBlockStateTypes.OUTPUT_BLOCKSTATE_TYPES.register(modEventBus);
        ModProcessors.PROCESSORS.register(modEventBus);

        // Abilities
        ModAbilityTypes.ABILITY_TYPES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModTargetingTypes.TARGETING_TYPES.register(modEventBus);

        ModModifierEffects.MODIFIER_EFFECT_TYPES.register(modEventBus);
        ModObjectiveTypes.OBJECTIVE_TYPES.register(modEventBus);
        ModOngoingObjectiveTypes.ONGOING_OBJECTIVE_TYPES.register(modEventBus);
        ModChunkGenerators.CHUNK_GENERATORS.register(modEventBus);

        if (FMLEnvironment.dist.isClient()) {
            ModConfigurableLayers.LAYERS.register(modEventBus);
            ModConfigurableLayers.VANILLA_LAYERS.register(modEventBus);
        }

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Wotr) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like
        // onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::loadInterop);
        modEventBus.addListener(this::registerInterop);
        modEventBus.addListener(ModPayloadHandlers::registerPayloadHandlers);

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

    public void registerInterop(RegisterEvent event) {
        ModList.get()
                .getModContainerById("sophisticatedbackpacks")
                .ifPresent(x -> SophisticatedBackpackInterop.register(event));
    }

    @SubscribeEvent
    private void registerCommands(RegisterCommandsEvent event) {
        InventorySnapshotCommands.register(event.getDispatcher(), event.getBuildContext());
        SpawnPieceCommand.register(event.getDispatcher(), event.getBuildContext());
        if (FMLEnvironment.dist.isClient()) {
            RiftMapCommands.register(event.getDispatcher(), event.getBuildContext());
        }
        new DebugCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        AbilityCommands.register(event.getDispatcher(), event.getBuildContext());
        new RiftKeyCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new EssenceCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new RiftCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
        new HudCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
    }
}
