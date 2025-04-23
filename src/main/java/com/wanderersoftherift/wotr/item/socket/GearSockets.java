package com.wanderersoftherift.wotr.item.socket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import static com.wanderersoftherift.wotr.init.ModTags.Items.*;

public record GearSockets(List<GearSocket> sockets) {
    public static final Codec<GearSockets> CODEC = RecordCodecBuilder
            .create(inst -> inst.group(GearSocket.CODEC.listOf().fieldOf("sockets").forGetter(GearSockets::sockets))
                    .apply(inst, GearSockets::new));

    public boolean isEmpty() {
        return sockets.isEmpty();
    }

    public static GearSockets randomSockets(int maxSockets, RandomSource random) {
        List<GearSocket> sockets = new ArrayList<>();
        // randomly generate maxSockets or maxSockets - 1 sockets
        int mod = Math.abs(random.nextInt() % 2);
        int actualSockets = maxSockets - mod;
        for (int i = 0; i < actualSockets; i++) {
            GearSocket socket = GearSocket.getRandomSocket(random);
            sockets.add(socket);
        }
        return new GearSockets(sockets);
    }

    public static GearSockets emptySockets() {
        return new GearSockets(new ArrayList<>());
    }

    public static void generateForItem(ItemStack itemStack){

    }

    public static void generateForItem(ItemStack itemStack, Level level) {
        if (level.isClientSide() || !itemStack.is(SOCKETABLE)) {
            return;
        }

        GearSockets sockets = emptySockets();

        if(itemStack.is(ITEM_TIER_ONE)) {
            GearSocket socket = GearSocket.getRandomSocket(level.random);
            sockets = new GearSockets(List.of(socket));
        }

        if(itemStack.is(ITEM_TIER_TWO)) {
            sockets = GearSockets.randomSockets(2, level.random);
        }

        if(itemStack.is(ITEM_TIER_THREE)) {
            sockets = GearSockets.randomSockets(3, level.random);
        }

        if(itemStack.is(ITEM_TIER_FOUR)) {
            sockets = GearSockets.randomSockets(4, level.random);
        }

        if(itemStack.is(ITEM_TIER_FIVE)) {
            sockets = GearSockets.randomSockets(5, level.random);
        }

        if(itemStack.is(ITEM_TIER_SIX)) {
            sockets = GearSockets.randomSockets(6, level.random);
        }

        if(itemStack.is(UPGRADEABLE_ITEM_TIER_SIX)) {
            // Check if the item already has sockets
            sockets = (GearSockets) itemStack.get(ModDataComponentType.GEAR_SOCKETS);
            if (sockets == null || sockets.isEmpty()) {
                // If not, generate new sockets
                sockets = GearSockets.randomSockets(6, level.random);
            } else {
                if(sockets.sockets().size() >= 6) {
                    // If it already has 6 sockets, do not add more
                    return;
                }
                // If it does but only has 5, add a new socket
                GearSocket socket = GearSocket.getRandomSocket(level.random);
                List<GearSocket> newSockets = new ArrayList<>(sockets.sockets());
                newSockets.add(socket);
                sockets = new GearSockets(newSockets);
            }
        }

        itemStack.set(ModDataComponentType.GEAR_SOCKETS, sockets);
    }
}
