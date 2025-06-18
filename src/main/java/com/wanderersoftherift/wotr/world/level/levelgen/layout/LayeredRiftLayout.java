package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.FiniteRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;

public interface LayeredRiftLayout extends RiftLayout {

    interface LayoutLayer {
        void generateSection(LayoutSection section, RandomSource source, ArrayList<RiftSpace> allSpaces);

        interface Factory {
            Codec<Factory> CODEC = WotrRegistries.LAYOUT_LAYER_TYPES.byNameCodec()
                    .dispatch(fac -> fac.codec(), codec -> codec);

            LayoutLayer create(MinecraftServer server);

            MapCodec<? extends Factory> codec();
        }
    }

    interface LayoutSection {
        FiniteRiftShape sectionShape();

        boolean tryPlaceSpace(RiftSpace space);

        long[] getEmptySpaces();
    }

}
