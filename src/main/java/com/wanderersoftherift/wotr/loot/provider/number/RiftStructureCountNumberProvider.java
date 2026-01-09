package com.wanderersoftherift.wotr.loot.provider.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrNumberProviders;
import com.wanderersoftherift.wotr.world.level.ConnectedRoomIterator;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratableId;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

public record RiftStructureCountNumberProvider(String structurePrefix, NumberProvider roomDepth)
        implements NumberProvider {

    public static final MapCodec<RiftStructureCountNumberProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("structure_prefix")
                            .forGetter(RiftStructureCountNumberProvider::structurePrefix),
                    NumberProviders.CODEC.optionalFieldOf("room_depth", ConstantValue.exactly(5))
                            .forGetter(RiftStructureCountNumberProvider::roomDepth)
            ).apply(instance, RiftStructureCountNumberProvider::new));

    @Override
    public float getFloat(LootContext lootContext) {
        ServerLevel level = lootContext.getLevel();
        if (!(level.getChunkSource().getGenerator() instanceof FastRiftGenerator generator)) {
            return 0;
        }

        var roomIterator = ConnectedRoomIterator.create(level, roomDepth.getInt(lootContext));
        int count = 0;
        while (roomIterator.hasNext()) {
            var room = roomIterator.next();
            Object2IntMap<RiftGeneratableId> jigsawCounts = generator.getGeneratableCounts(room, level);
            count += jigsawCounts.object2IntEntrySet()
                    .stream()
                    .filter(entry -> entry.getKey().path().startsWith(structurePrefix))
                    .mapToInt(Object2IntMap.Entry::getIntValue)
                    .sum();
        }
        return count;
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return WotrNumberProviders.RIFT_STRUCTURE_COUNT.get();
    }
}
