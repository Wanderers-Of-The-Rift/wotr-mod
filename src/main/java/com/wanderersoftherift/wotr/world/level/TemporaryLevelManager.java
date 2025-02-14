package com.wanderersoftherift.wotr.world.level;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.network.S2CLevelListUpdatePacket;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TemporaryLevelManager {
    private static final List<TemporaryLevel> levels = new ArrayList<>();

    public static TemporaryLevel createRiftLevel() {
        ResourceLocation id = WanderersOfTheRift.id("rift_" + UUID.randomUUID());

        TemporaryLevel level = new TemporaryLevel(id);
        registerLevel(level);
        return level;
    }

    private static void registerLevel(TemporaryLevel level) {
        Optional<Registry<Level>> dimensionRegistry = level.registryAccess().lookup(Registries.DIMENSION);
        if (dimensionRegistry.isEmpty()) {
            return;
        }
        ResourceLocation id = level.getId();
        Registry<Level> registry = dimensionRegistry.get();
        Registry.register(registry, id, level);
        PacketDistributor.sendToAllPlayers(new S2CLevelListUpdatePacket(id, false));
        TemporaryLevelManager.levels.add(level);
    }

    public static void unregisterLevel(TemporaryLevel level) {
        // idk how to deregister stuff
        ResourceLocation id = level.getId();
        PacketDistributor.sendToAllPlayers(new S2CLevelListUpdatePacket(id, true));
        TemporaryLevelManager.levels.remove(level);
    }
}
