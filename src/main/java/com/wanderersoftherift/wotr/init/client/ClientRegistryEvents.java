package com.wanderersoftherift.wotr.init.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.ModShaders;
import com.wanderersoftherift.wotr.client.map.Direction;
import com.wanderersoftherift.wotr.client.map.MapCell;
import com.wanderersoftherift.wotr.client.map.MapData;
import com.wanderersoftherift.wotr.client.map.MapRoom;
import com.wanderersoftherift.wotr.client.render.blockentity.JigsawBlockEntityRenderer;
import com.wanderersoftherift.wotr.client.render.entity.RiftEntranceRenderer;
import com.wanderersoftherift.wotr.client.render.item.properties.select.SelectRuneGemShape;
import com.wanderersoftherift.wotr.client.tooltip.GearSocketTooltipRenderer;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.client.tooltip.ImageTooltipRenderer;
import com.wanderersoftherift.wotr.gui.layer.objective.ObjectiveLayer;
import com.wanderersoftherift.wotr.gui.screen.KeyForgeScreen;
import com.wanderersoftherift.wotr.gui.screen.RuneAnvilScreen;
import com.wanderersoftherift.wotr.init.ModEntityTypes;
import com.wanderersoftherift.wotr.init.ModMenuTypes;
import com.wanderersoftherift.wotr.world.level.RiftDimensionSpecialEffects;
import com.wanderersoftherift.wotr.world.level.RiftDimensionType;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.EnumSet;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientRegistryEvents {

    private ClientRegistryEvents() {
        initDemoMap();
    }

    public static void initDemoMap() {
        int cnt = 5;
        for (int x = -cnt/2; x <= cnt/2; x++) {
            //for (int y = -cnt/2; y <= cnt/2; y++) {
            for (int z = -cnt/2; z <= cnt/2; z++) {
                MapCell cell = new MapCell(new Vector3f(x, 0, z), 1f, 0, EnumSet.noneOf(Direction.class), EnumSet.of(Direction.NORTH, Direction.EAST));
                ArrayList<MapCell> cells = new ArrayList<>();
                cells.add(cell);
                MapData.addRoom(new MapRoom(x, 0, z, 1, 1, 1, cells));
            }
            //}
        }
        MapData.addCell( new MapCell(new Vector3f(0, 0,0), 1f, 0));
        MapData.addCell( new MapCell(new Vector3f(2, 0,0), 1f, 0));

        MapCell cell = new MapCell(new Vector3f(5, 0, 4), 1f, 0);
        MapCell cell2 = new MapCell(new Vector3f(5, 0, 5), 1f, 0, EnumSet.noneOf(Direction.class), EnumSet.of(Direction.NORTH, Direction.EAST));
        ArrayList<MapCell> cells = new ArrayList<>();
        cells.add(cell);
        cells.add(cell2);
        MapData.addRoom(new MapRoom(4, 0, 4, 2, 1, 2, cells));
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.RUNE_ANVIL_MENU.get(), RuneAnvilScreen::new);
        event.register(ModMenuTypes.KEY_FORGE_MENU.get(), KeyForgeScreen::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.RIFT_ENTRANCE.get(), RiftEntranceRenderer::new);
        event.registerBlockEntityRenderer(
                BlockEntityType.JIGSAW,
                JigsawBlockEntityRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerShaderPrograms(RegisterShadersEvent event) {
        event.registerShader(ModShaders.RIFT_PORTAL);
        event.registerShader(ModShaders.RIFT_MAPPER);
    }
    @SubscribeEvent
    public static void registerSelectItemModelProperties(RegisterSelectItemModelPropertyEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "runegem_shape"), SelectRuneGemShape.TYPE);
    }

    @SubscribeEvent
    public static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ImageComponent.class, ImageTooltipRenderer::new);
        event.register(GearSocketTooltipRenderer.GearSocketComponent.class, GearSocketTooltipRenderer::new);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(WanderersOfTheRift.id("objective"), new ObjectiveLayer());
    }

    public static final KeyMapping JIGSAW_NAME_TOGGLE_KEY = new KeyMapping(
            "key." + WanderersOfTheRift.id("jigsaw_name_toggle"),
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.misc"
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(JIGSAW_NAME_TOGGLE_KEY);
    }

    @SubscribeEvent
    private static void registerClientDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(RiftDimensionType.RIFT_DIMENSION_RENDERER_KEY, new RiftDimensionSpecialEffects());
    }
}
