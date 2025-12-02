package com.wanderersoftherift.wotr.loot.provider.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation;
import com.wanderersoftherift.wotr.init.WotrNumberProviders;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record RiftJigsawCountNumberProvider(String jigsawPrefix, int roomDistance) implements NumberProvider {

    public static final MapCodec<RiftJigsawCountNumberProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("jigsaw_prefix").forGetter(RiftJigsawCountNumberProvider::jigsawPrefix),
                    Codec.intRange(1, Integer.MAX_VALUE)
                            .optionalFieldOf("room_distance", 5)
                            .forGetter(RiftJigsawCountNumberProvider::roomDistance)
            ).apply(instance, RiftJigsawCountNumberProvider::new));

    @Override
    public float getFloat(LootContext lootContext) {
        ServerLevel level = lootContext.getLevel();
        if (!(level.getChunkSource().getGenerator() instanceof FastRiftGenerator generator)) {
            return 0;
        }
        RiftLayout layout = generator.getOrCreateLayout(level.getServer());

        Vec3i start = SectionPos.of(PortalSpawnLocation.DEFAULT_RIFT_EXIT_POSITION);
        Set<Vec3i> connected = new HashSet<>();
        connected.add(start);
        List<Vec3i> layerRooms = new ArrayList<>();
        layerRooms.add(start);

        int result = 0;
        for (int layer = 0; !layerRooms.isEmpty() && layer < roomDistance - 1; layer++) {
            List<Vec3i> nextLayerRooms = new ArrayList<>(layerRooms.size());
            for (Vec3i roomPos : layerRooms) {
                if (layout.getChunkSpace(roomPos) instanceof RoomRiftSpace room) {
                    Object2IntMap<String> jigsawCounts = generator.getJigsawCounts(room, level);
                    result += jigsawCounts.object2IntEntrySet()
                            .stream()
                            .filter(entry -> entry.getKey().startsWith(jigsawPrefix))
                            .mapToInt(Object2IntMap.Entry::getIntValue)
                            .sum();
                    room.corridors().forEach(corridor -> {
                        Vec3i adjRoom = corridor.getConnectingPos(room);
                        if (connected.add(adjRoom)) {
                            nextLayerRooms.add(adjRoom);
                        }
                    });
                }
            }
            layerRooms = nextLayerRooms;
        }
        // Unrolled the last loop to skip corridor processing
        for (Vec3i roomPos : layerRooms) {
            if (layout.getChunkSpace(roomPos) instanceof RoomRiftSpace room) {
                Object2IntMap<String> jigsawCounts = generator.getJigsawCounts(room, level);
                result += jigsawCounts.object2IntEntrySet()
                        .stream()
                        .filter(entry -> entry.getKey().startsWith(jigsawPrefix))
                        .mapToInt(Object2IntMap.Entry::getIntValue)
                        .sum();
            }
        }

        return result;
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return WotrNumberProviders.RIFT_JIGSAW_COUNT.get();
    }
}
