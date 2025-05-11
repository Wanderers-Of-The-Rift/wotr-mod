package com.wanderersoftherift.wotr.item.socket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import static com.wanderersoftherift.wotr.init.ModTags.Items.SOCKETABLE;

public record GearSockets(List<GearSocket> sockets) {
    public static final Codec<GearSockets> CODEC = RecordCodecBuilder
            .create(inst -> inst.group(GearSocket.CODEC.listOf().fieldOf("sockets").forGetter(GearSockets::sockets))
                    .apply(inst, GearSockets::new));

    public boolean isEmpty() {
        return sockets.isEmpty();
    }

    public static GearSockets randomSockets(int maxSockets, RandomSource random) {
        List<GearSocket> sockets = new ArrayList<>();
        int actualSockets = random.nextInt(maxSockets) + 1;
        for (int i = 0; i < actualSockets; i++) {
            GearSocket socket = GearSocket.getRandomSocket(random);
            sockets.add(socket);
        }
        return new GearSockets(sockets);
    }

    public static GearSockets randomSockets(int minSockets, int maxSockets, RandomSource random) {
        List<GearSocket> sockets = new ArrayList<>();
        // sort of pulling in random.nextIntBetweenInclusive, but it didn't work exactly the way I needed it to
        int actualSockets = random.nextIntBetweenInclusive(minSockets, maxSockets);
        for (int i = 0; i < actualSockets; i++) {
            GearSocket socket = GearSocket.getRandomSocket(random);
            sockets.add(socket);
        }
        return new GearSockets(sockets);
    }

    public static GearSockets emptySockets() {
        return new GearSockets(new ArrayList<>());
    }

    public static void generateForItem(ItemStack itemStack, Level level, int minSockets, int maxSockets) {
        if (level.isClientSide() || !itemStack.is(SOCKETABLE)) {
            return;
        }
        // check if gear already has sockets
        if (itemStack.get(ModDataComponentType.GEAR_SOCKETS) != null) {
            return;
        }
        GearSockets sockets = GearSockets.randomSockets(minSockets, maxSockets, level.random);
        itemStack.set(ModDataComponentType.GEAR_SOCKETS, sockets);
    }
}
