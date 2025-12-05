package com.wanderersoftherift.wotr.loot.provider.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation;
import com.wanderersoftherift.wotr.init.WotrNumberProviders;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record RiftRoomCountNumberProvider(NumberProvider roomDistance) implements NumberProvider {

    public static final MapCodec<RiftRoomCountNumberProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    NumberProviders.CODEC.optionalFieldOf("max_distance", ConstantValue.exactly(100))
                            .forGetter(RiftRoomCountNumberProvider::roomDistance)
            ).apply(instance, RiftRoomCountNumberProvider::new));

    @Override
    public float getFloat(LootContext lootContext) {
        ServerLevel level = lootContext.getLevel();
        if (!(level.getChunkSource().getGenerator() instanceof FastRiftGenerator generator)) {
            return 0;
        }
        RiftLayout layout = generator.getOrCreateLayout(level.getServer());

        RiftSpace startRoom = layout.getChunkSpace(SectionPos.of(PortalSpawnLocation.DEFAULT_RIFT_EXIT_POSITION));
        Set<Vec3i> connected = new HashSet<>();
        connected.add(startRoom.origin());
        List<RiftSpace> layerRooms = new ArrayList<>();
        layerRooms.add(startRoom);

        int distance = Math.max(1, roomDistance().getInt(lootContext));
        long result = 0;
        for (int layer = 0; !layerRooms.isEmpty() && layer < distance - 1; layer++) {
            List<RiftSpace> nextLayerRooms = new ArrayList<>(layerRooms.size());
            for (RiftSpace space : layerRooms) {
                result++;
                if (space instanceof RoomRiftSpace room) {
                    room.corridors().forEach(corridor -> {
                        RiftSpace adjSpace = layout.getChunkSpace(corridor.getConnectingPos(room));
                        if (adjSpace instanceof RoomRiftSpace && connected.add(adjSpace.origin())) {
                            nextLayerRooms.add(adjSpace);
                        }
                    });
                }
            }
            result += nextLayerRooms.size();
            layerRooms = nextLayerRooms;
        }
        return result;
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return WotrNumberProviders.RIFT_ROOM_COUNT.get();
    }
}
