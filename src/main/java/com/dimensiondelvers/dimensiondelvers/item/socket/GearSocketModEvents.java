package com.dimensiondelvers.dimensiondelvers.item.socket;

import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RuneGemShape;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

import static com.dimensiondelvers.dimensiondelvers.DimensionDelvers.MODID;
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
        objects.add(new GearSocket(RuneGemShape.CIRCLE, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.SQUARE, Optional.empty(), ItemStack.EMPTY));
        return objects;
    }

    private static @NotNull ArrayList<GearSocket> getExampleSockets2() {
        ArrayList<GearSocket> objects = new ArrayList<>();
        objects.add(new GearSocket(RuneGemShape.CIRCLE, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.SQUARE, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.TRIANGLE, Optional.empty(), ItemStack.EMPTY));
        return objects;
    }

    private static @NotNull ArrayList<GearSocket> getExampleSockets3() {
        ArrayList<GearSocket> objects = new ArrayList<>();
        objects.add(new GearSocket(RuneGemShape.CIRCLE, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.SQUARE, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.TRIANGLE, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.DIAMOND, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.HEART, Optional.empty(), ItemStack.EMPTY));
        return objects;
    }

    private static @NotNull ArrayList<GearSocket> getExampleSockets4() {
        ArrayList<GearSocket> objects = new ArrayList<>();
        objects.add(new GearSocket(RuneGemShape.PENTAGON, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.HEART, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.DIAMOND, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.TRIANGLE, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.SQUARE, Optional.empty(), ItemStack.EMPTY));
        objects.add(new GearSocket(RuneGemShape.CIRCLE, Optional.empty(), ItemStack.EMPTY));
        return objects;
    }
}
