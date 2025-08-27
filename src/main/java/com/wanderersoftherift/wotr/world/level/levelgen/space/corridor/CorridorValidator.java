package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

import java.util.function.Function;

/**
 * Used to determine whether a corridor exists. Can be used both before the corridor is generated and after the corridor
 * is generated.
 */

public interface CorridorValidator {

    Codec<CorridorValidator> CODEC = WotrRegistries.RIFT_CORRIDOR_VALIDATORS.byNameCodec()
            .dispatch(CorridorValidator::codec, Function.identity());

    MapCodec<? extends CorridorValidator> codec();

    boolean validateCorridor(int x, int y, int z, Direction d, FastRiftGenerator generator, MinecraftServer server);
}
