package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftTemplates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public interface RoomTemplatePoolProvider {
    @Deprecated
    ResourceLocation getPool(RoomRiftSpace.RoomType type);

    default List<RiftGeneratable> getTemplates(MinecraftServer server, RoomRiftSpace.RoomType type) {
        return RiftTemplates.all(server, getPool(type));
    }
}
