package com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public interface RoomRandomizer {

    RoomRiftSpace randomSpace(RandomSource source, Vec3i maximumSize);

    interface Factory {
        RoomRandomizer createRandomizer(MinecraftServer server);

        Holder<StructureTemplatePool> pool();
    }
}
