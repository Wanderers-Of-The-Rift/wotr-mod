package com.wanderersoftherift.wotr.loot.provider.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrNumberProviders;
import com.wanderersoftherift.wotr.world.level.ConnectedRoomIterator;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

public record RiftRoomCountNumberProvider(NumberProvider roomDepth) implements NumberProvider {

    public static final MapCodec<RiftRoomCountNumberProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    NumberProviders.CODEC.optionalFieldOf("room_depth", ConstantValue.exactly(100))
                            .forGetter(RiftRoomCountNumberProvider::roomDepth)
            ).apply(instance, RiftRoomCountNumberProvider::new));

    @Override
    public float getFloat(LootContext lootContext) {
        var roomIterator = ConnectedRoomIterator.create(lootContext.getLevel(), roomDepth.getInt(lootContext));
        int count = 0;
        while (roomIterator.hasNext()) {
            // TODO: add room predicate
            roomIterator.next();
            count++;
        }
        return count - 1; // Excluding the portal room
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return WotrNumberProviders.RIFT_ROOM_COUNT.get();
    }
}
