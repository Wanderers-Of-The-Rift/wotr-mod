package com.wanderersoftherift.wotr.item.socket;

import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.item.runegem.RuneGemShape;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.wanderersoftherift.wotr.WanderersOfTheRift.MODID;
import static net.neoforged.fml.common.EventBusSubscriber.*;

@EventBusSubscriber(bus = Bus.MOD, modid = MODID)
public class GearSocketModEvents {

    @SubscribeEvent
    public static void modifyComponents(ModifyDefaultComponentsEvent event) {
        event.modify(Items.IRON_SWORD, builder ->
                builder.set(ModDataComponentType.GEAR_SOCKETS.get(), new GearSockets(getExampleSockets1()))
        );

        event.modify(Items.GOLDEN_SWORD, builder ->
                builder.set(ModDataComponentType.GEAR_SOCKETS.get(), new GearSockets(getExampleSockets2()))
        );

        event.modify(Items.DIAMOND_SWORD, builder ->
                builder.set(ModDataComponentType.GEAR_SOCKETS.get(), new GearSockets(getExampleSockets3()))
        );

        event.modify(Items.NETHERITE_SWORD, builder ->
                builder.set(ModDataComponentType.GEAR_SOCKETS.get(), new GearSockets(getExampleSockets4()))
        );
    }

    private static @NotNull ArrayList<GearSocket> getExampleSockets1() {
        ArrayList<GearSocket> objects = new ArrayList<>();
        objects.add(new GearSocket(RuneGemShape.CIRCLE, null, null));
        objects.add(new GearSocket(RuneGemShape.SQUARE, null, null));
        return objects;
    }

    private static @NotNull ArrayList<GearSocket> getExampleSockets2() {
        ArrayList<GearSocket> objects = new ArrayList<>();
        objects.add(new GearSocket(RuneGemShape.CIRCLE, null, null));
        objects.add(new GearSocket(RuneGemShape.SQUARE, null, null));
        objects.add(new GearSocket(RuneGemShape.TRIANGLE, null, null));
        return objects;
    }

    private static @NotNull ArrayList<GearSocket> getExampleSockets3() {
        ArrayList<GearSocket> objects = new ArrayList<>();
        objects.add(new GearSocket(RuneGemShape.CIRCLE, null, null));
        objects.add(new GearSocket(RuneGemShape.SQUARE, null, null));
        objects.add(new GearSocket(RuneGemShape.TRIANGLE, null, null));
        objects.add(new GearSocket(RuneGemShape.DIAMOND, null, null));
        objects.add(new GearSocket(RuneGemShape.HEART, null, null));
        return objects;
    }

    private static @NotNull ArrayList<GearSocket> getExampleSockets4() {
        ArrayList<GearSocket> objects = new ArrayList<>();
        objects.add(new GearSocket(RuneGemShape.PENTAGON, null, null));
        objects.add(new GearSocket(RuneGemShape.HEART, null, null));
        objects.add(new GearSocket(RuneGemShape.DIAMOND, null, null));
        objects.add(new GearSocket(RuneGemShape.TRIANGLE, null, null));
        objects.add(new GearSocket(RuneGemShape.SQUARE, null, null));
        objects.add(new GearSocket(RuneGemShape.CIRCLE, null, null));
        return objects;
    }
}
