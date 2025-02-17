package com.wanderersoftherift.wotr;

import com.mojang.logging.LogUtils;
import com.wanderersoftherift.wotr.commands.DebugCommands;
import com.wanderersoftherift.wotr.commands.InventorySnapshotCommands;
import com.wanderersoftherift.wotr.commands.RiftMapCommands;
import com.wanderersoftherift.wotr.commands.SpawnPieceCommand;
import com.wanderersoftherift.wotr.config.ClientConfig;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.init.ModBlockEntities;
import com.wanderersoftherift.wotr.init.ModBlocks;
import com.wanderersoftherift.wotr.init.ModChunkGenerators;
import com.wanderersoftherift.wotr.init.ModCreativeTabs;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModEntityTypes;
import com.wanderersoftherift.wotr.init.ModInputBlockStateTypes;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.init.ModLootItemFunctionTypes;
import com.wanderersoftherift.wotr.init.ModLootModifiers;
import com.wanderersoftherift.wotr.init.ModMenuTypes;
import com.wanderersoftherift.wotr.init.ModModifierEffects;
import com.wanderersoftherift.wotr.init.ModOngoingObjectiveTypes;
import com.wanderersoftherift.wotr.init.ModOutputBlockStateTypes;
import com.wanderersoftherift.wotr.init.ModProcessors;
import com.wanderersoftherift.wotr.interop.sophisticatedbackpacks.SophisticatedBackpackInterop;
import com.wanderersoftherift.wotr.init.ModSoundEvents;
import com.wanderersoftherift.wotr.server.inventorySnapshot.InventorySnapshotSystem;
import com.wanderersoftherift.wotr.world.level.RiftDimensionSpecialEffects;
import com.wanderersoftherift.wotr.world.level.RiftDimensionType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(WanderersOfTheRift.MODID)
public class WanderersOfTheRift {
    public static final String MODID = "wotr";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WanderersOfTheRift(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        // Register things
        ModDataComponentType.DATA_COMPONENTS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModProcessors.PROCESSORS.register(modEventBus);
        ModInputBlockStateTypes.INPUT_BLOCKSTATE_TYPES.register(modEventBus);
        ModOutputBlockStateTypes.OUTPUT_BLOCKSTATE_TYPES.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModLootModifiers.GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        ModModifierEffects.MODIFIER_EFFECT_TYPES.register(modEventBus);
        ModOngoingObjectiveTypes.ONGOING_OBJECTIVE_TYPES.register(modEventBus);
        ModEntityTypes.ENTITIES.register(modEventBus);
        ModLootItemFunctionTypes.LOOT_ITEM_FUNCTION_TYPES.register(modEventBus);
        ModSoundEvents.SOUND_EVENTS.register(modEventBus);
        ModChunkGenerators.CHUNK_GENERATORS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Wotr) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative); // Register the item to a creative tab
        modEventBus.addListener(this::modInterop);
        modEventBus.addListener(this::registerDimEffects); // Register the dimension special effects

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
     * Helper method to get a {@code TagKey} with our Mod Id and a passed in name
     *
     * @param name the name to create the {@code TagKey} with
     * @return A {@code TagKey} with the given name
     */
    public static <T> TagKey<T> tagId(ResourceKey<? extends Registry<T>> registry, String name) {
        return TagKey.create(registry, id(name));
    }

    private void registerDimEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(RiftDimensionType.RIFT_DIMENSION_RENDERER_KEY, new RiftDimensionSpecialEffects());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info("Reticulating splines");

        // Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void modInterop(final FMLCommonSetupEvent event) {
        ModList.get().getModContainerById("sophisticatedbackpacks").ifPresent(x -> SophisticatedBackpackInterop.load());
    }

    @SubscribeEvent
    private void registerCommands(RegisterCommandsEvent event) {
        InventorySnapshotCommands.register(event.getDispatcher(), event.getBuildContext());
        SpawnPieceCommand.register(event.getDispatcher(), event.getBuildContext());
        if (FMLEnvironment.dist.isClient()) {
            RiftMapCommands.register(event.getDispatcher(), event.getBuildContext());
        }
        new DebugCommands().registerCommand(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    private void onDropsFromDeath(LivingDropsEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            InventorySnapshotSystem.getInstance().retainSnapshotItemsOnDeath(player, event);
        }
    }

    @SubscribeEvent
    private void onPlayerDeath(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.isEndConquered() && event.getEntity() instanceof ServerPlayer player) {
            InventorySnapshotSystem.getInstance().restoreItemsOnRespawn(player);
        }
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        //if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(ModBlocks.EXAMPLE_BLOCK);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting"); // Do something when the server starts
    }

}
