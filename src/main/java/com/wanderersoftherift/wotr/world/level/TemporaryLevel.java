package com.wanderersoftherift.wotr.world.level;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class TemporaryLevel extends ServerLevel {
    private final ResourceLocation id;

    public TemporaryLevel(ResourceLocation id) {
        super(
                ServerLifecycleHooks.getCurrentServer(),
                // ???
        );

        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }
}
