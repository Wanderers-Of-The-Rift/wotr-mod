package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.map.Direction;
import com.wanderersoftherift.wotr.client.map.MapCell;
import com.wanderersoftherift.wotr.client.map.MapData;
import com.wanderersoftherift.wotr.client.map.MapRoom;
import com.wanderersoftherift.wotr.client.render.item.properties.select.SelectRuneGemShape;
import com.wanderersoftherift.wotr.client.tooltip.GearSocketTooltipRenderer;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.client.tooltip.ImageTooltipRenderer;
import com.wanderersoftherift.wotr.gui.config.preset.HudPresetManager;
import com.wanderersoftherift.wotr.world.level.RiftDimensionSpecialEffects;
import com.wanderersoftherift.wotr.world.level.RiftDimensionType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.EnumSet;

// TODO: Most things in here should have other homes
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientInitEvents {

    private ClientInitEvents() {
        initDemoMap();
    }

    public static void initDemoMap() {
        int cnt = 5;
        for (int x = -cnt / 2; x <= cnt / 2; x++) {
            // for (int y = -cnt/2; y <= cnt/2; y++) {
            for (int z = -cnt / 2; z <= cnt / 2; z++) {
                MapCell cell = new MapCell(new Vector3f(x, 0, z), 1f, 0, EnumSet.noneOf(Direction.class),
                        EnumSet.of(Direction.NORTH, Direction.EAST));
                ArrayList<MapCell> cells = new ArrayList<>();
                cells.add(cell);
                MapData.updateRoom(new MapRoom(x, 0, z, 1, 1, 1, cells));
            }
            // }
        }
        MapData.addCell(new MapCell(new Vector3f(0, 0, 0), 1f, 0));
        MapData.addCell(new MapCell(new Vector3f(2, 0, 0), 1f, 0));

        MapCell cell = new MapCell(new Vector3f(5, 0, 4), 1f, 0);
        MapCell cell2 = new MapCell(new Vector3f(5, 0, 5), 1f, 0, EnumSet.noneOf(Direction.class),
                EnumSet.of(Direction.NORTH, Direction.EAST));
        ArrayList<MapCell> cells = new ArrayList<>();
        cells.add(cell);
        cells.add(cell2);
        MapData.updateRoom(new MapRoom(4, 0, 4, 2, 1, 2, cells));
    }

    @SubscribeEvent
    public static void registerSelectItemModelProperties(RegisterSelectItemModelPropertyEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "runegem_shape"),
                SelectRuneGemShape.TYPE);
    }

    @SubscribeEvent
    public static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ImageComponent.class, ImageTooltipRenderer::new);
        event.register(GearSocketTooltipRenderer.GearSocketComponent.class, GearSocketTooltipRenderer::new);
    }

    @SubscribeEvent
    private static void registerClientDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(RiftDimensionType.RIFT_DIMENSION_RENDERER_KEY, new RiftDimensionSpecialEffects());
    }

    @SubscribeEvent
    public static void resourceReload(AddClientReloadListenersEvent event) {
        event.addListener(WanderersOfTheRift.id("hud_preset"), HudPresetManager.getInstance());
    }

}
