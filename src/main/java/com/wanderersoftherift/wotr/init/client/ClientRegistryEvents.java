package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.ModShaders;
import com.wanderersoftherift.wotr.client.map.Direction;
import com.wanderersoftherift.wotr.client.map.MapCell;
import com.wanderersoftherift.wotr.client.map.MapData;
import com.wanderersoftherift.wotr.client.map.MapRoom;
import com.wanderersoftherift.wotr.client.render.entity.RiftEntranceRenderer;
import com.wanderersoftherift.wotr.gui.screen.RuneAnvilScreen;
import com.wanderersoftherift.wotr.init.ModEntityTypes;
import com.wanderersoftherift.wotr.init.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.joml.Vector3f;

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
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.RIFT_ENTRANCE.get(), RiftEntranceRenderer::new);
    }

    @SubscribeEvent
    public static void registerShaderPrograms(RegisterShadersEvent event) {
        event.registerShader(ModShaders.RIFT_PORTAL);
        event.registerShader(ModShaders.RIFT_MAPPER);
    }
}
