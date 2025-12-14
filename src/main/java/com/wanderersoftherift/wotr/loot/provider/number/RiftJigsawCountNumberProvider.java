package com.wanderersoftherift.wotr.loot.provider.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation;
import com.wanderersoftherift.wotr.init.WotrNumberProviders;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratableId;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
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

public record RiftJigsawCountNumberProvider(String jigsawPrefix, NumberProvider roomDistance)
        implements NumberProvider {

    public static final MapCodec<RiftJigsawCountNumberProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("structure_prefix").forGetter(RiftJigsawCountNumberProvider::jigsawPrefix),
                    NumberProviders.CODEC.optionalFieldOf("room_distance", ConstantValue.exactly(5))
                            .forGetter(RiftJigsawCountNumberProvider::roomDistance)
            ).apply(instance, RiftJigsawCountNumberProvider::new));

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
        int result = 0;
        for (int layer = 0; !layerRooms.isEmpty() && layer < distance - 1; layer++) {
            List<RiftSpace> nextLayerRooms = new ArrayList<>(layerRooms.size());
            for (RiftSpace space : layerRooms) {
                if (space instanceof RoomRiftSpace room) {
                    Object2IntMap<RiftGeneratableId> jigsawCounts = generator.getGeneratableCounts(room, level);
                    result += jigsawCounts.object2IntEntrySet()
                            .stream()
                            .filter(entry -> entry.getKey().path().startsWith(jigsawPrefix))
                            .mapToInt(Object2IntMap.Entry::getIntValue)
                            .sum();
                    room.corridors().forEach(corridor -> {
                        RiftSpace adjSpace = layout.getChunkSpace(corridor.getConnectingPos(room));
                        if (connected.add(adjSpace.origin())) {
                            nextLayerRooms.add(adjSpace);
                        }
                    });
                }
            }
            layerRooms = nextLayerRooms;
        }
        // Unrolled the last loop to skip corridor processing
        for (RiftSpace space : layerRooms) {
            if (space instanceof RoomRiftSpace room) {
                Object2IntMap<RiftGeneratableId> jigsawCounts = generator.getGeneratableCounts(room, level);
                result += jigsawCounts.object2IntEntrySet()
                        .stream()
                        .filter(entry -> entry.getKey().path().startsWith(jigsawPrefix))
                        .mapToInt(Object2IntMap.Entry::getIntValue)
                        .sum();
            }
        }

        return result;
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return WotrNumberProviders.RIFT_STRUCTURE_COUNT.get();
    }
}
