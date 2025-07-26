package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.RiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.space.CorridorValidator;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;

/**
 * decides and remembers room placement in a rift, each space should be provided by RoomRandomizer (or it should be
 * VoidSpace or null)
 */
public interface RiftLayout extends CorridorValidator {
    RiftSpace getChunkSpace(Vec3i pos);

    interface Factory {

        Codec<RiftLayout.Factory> CODEC = WotrRegistries.LAYOUT_TYPES.byNameCodec()
                .dispatch(fac -> fac.codec(), codec -> codec);

        MapCodec<? extends Factory> codec();

        RiftLayout createLayout(MinecraftServer server, int seed, RiftConfig riftConfig);

        RiftShape riftShape();
    }
}
