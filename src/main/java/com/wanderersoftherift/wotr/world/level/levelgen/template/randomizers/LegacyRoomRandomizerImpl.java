package com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers;

import com.wanderersoftherift.wotr.world.level.levelgen.RoomTemplatePoolProvider;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;

import java.util.EnumMap;

@Deprecated
public class LegacyRoomRandomizerImpl implements LegacyRoomRandomizer {
    private final EnumMap<RoomRiftSpace.RoomType, RoomRandomizerImpl> randomizerMap;

    public LegacyRoomRandomizerImpl(MinecraftServer server, RoomTemplatePoolProvider roomTemplatePoolProvider) {
        randomizerMap = new EnumMap<>(RoomRiftSpace.RoomType.class);
        for (var type : RoomRiftSpace.RoomType.values()) {
            randomizerMap.computeIfAbsent(type,
                    (key) -> new RoomRandomizerImpl(server, roomTemplatePoolProvider.getPool(type),
                            type == RoomRiftSpace.RoomType.CHAOS ? RoomRandomizerImpl.MULTI_SIZE_SPACE_HOLDER_FACTORY
                                    : RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY));
        }
    }

    @Override
    public RoomRiftSpace randomSpace(RoomRiftSpace.RoomType roomType, RandomSource randomSource, Vec3i maximumSize) {
        return randomizerMap.get(roomType).randomSpace(randomSource, maximumSize);
    }
}
