package com.wanderersoftherift.wotr.item.socket;

import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

import static com.wanderersoftherift.wotr.WanderersOfTheRift.MODID;
import static net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD, modid = MODID)
public class GearSocketModEvents {

    @SubscribeEvent
    public static void modifyComponents(ModifyDefaultComponentsEvent event) {
        event.modify(Items.IRON_SWORD,
                builder -> builder.set(ModDataComponentType.GEAR_SOCKETS.get(), new GearSockets(getExampleSockets1())));

        event.modify(Items.DIAMOND_SWORD,
                builder -> builder.set(ModDataComponentType.GEAR_SOCKETS.get(), new GearSockets(getExampleSockets2())));

        event.modify(Items.NETHERITE_SWORD,
                builder -> builder.set(ModDataComponentType.GEAR_SOCKETS.get(), new GearSockets(getExampleSockets3())));
    }

    private static @NotNull ArrayList<GearSocket> getExampleSockets1() {
        ArrayList<GearSocket> objects = new ArrayList<>();
        objects.add(new GearSocket(RunegemShape.HEART, Optional.empty(), Optional.empty()));
        objects.add(new GearSocket(RunegemShape.CIRCLE, Optional.empty(), Optional.empty()));
        return objects;
    }

    private static @NotNull ArrayList<GearSocket> getExampleSockets2() {
        ArrayList<GearSocket> objects = new ArrayList<>();
        objects.add(new GearSocket(RunegemShape.TRIANGLE, Optional.empty(), Optional.empty()));
        objects.add(new GearSocket(RunegemShape.TRIANGLE, Optional.empty(), Optional.empty()));
        objects.add(new GearSocket(RunegemShape.CIRCLE, Optional.empty(), Optional.empty()));
        objects.add(new GearSocket(RunegemShape.SQUARE, Optional.empty(), Optional.empty()));
        return objects;
    }

    private static @NotNull ArrayList<GearSocket> getExampleSockets3() {
        ArrayList<GearSocket> objects = new ArrayList<>();
        objects.add(new GearSocket(RunegemShape.PENTAGON, Optional.empty(), Optional.empty()));
        objects.add(new GearSocket(RunegemShape.HEART, Optional.empty(), Optional.empty()));
        objects.add(new GearSocket(RunegemShape.DIAMOND, Optional.empty(), Optional.empty()));
        objects.add(new GearSocket(RunegemShape.TRIANGLE, Optional.empty(), Optional.empty()));
        objects.add(new GearSocket(RunegemShape.SQUARE, Optional.empty(), Optional.empty()));
        objects.add(new GearSocket(RunegemShape.CIRCLE, Optional.empty(), Optional.empty()));
        return objects;
    }
}
