package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.FiniteRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public interface LayeredRiftLayout extends RiftLayout {

    interface Factory extends RiftLayout.Factory {
        List<LayoutLayer.Factory> layers();

        Factory withLayers(List<LayoutLayer.Factory> layers);
    }

    interface LayoutLayer {
        void generateSection(LayoutSection section, RandomSource source, ArrayList<RiftSpace> allSpaces);

        interface Factory {
            Codec<Factory> CODEC = WotrRegistries.LAYOUT_LAYER_TYPES.byNameCodec()
                    .dispatch(fac -> fac.codec(), codec -> codec);

            LayoutLayer createLayer(MinecraftServer server, RiftConfig riftConfig);

            MapCodec<? extends Factory> codec();
        }
    }

    interface LayoutSection {
        FiniteRiftShape sectionShape();

        boolean tryPlaceSpace(RiftSpace space);

        long[] getEmptySpaces();
    }

}
