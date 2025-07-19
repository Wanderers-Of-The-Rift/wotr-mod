package com.wanderersoftherift.wotr.world.level.levelgen.layout.layers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public record BoxedLayer(Vec3i start, Vec3i size, LayeredRiftLayout.LayoutLayer... sublayers)
        implements LayeredRiftLayout.LayoutLayer {
    @Override
    public void generateSection(
            LayeredRiftLayout.LayoutSection section,
            RandomSource source,
            ArrayList<RiftSpace> allSpaces) {
        var sectionShape = section.sectionShape();
        var sectionStart = sectionShape.getBoxStart();
        var sectionEnd = sectionStart.offset(sectionShape.getBoxSize());
        var layerStart = start;
        var layerEnd = layerStart.offset(size);
        var overlapStart = new Vec3i(Integer.max(layerStart.getX(), sectionStart.getX()),
                Integer.max(layerStart.getY(), sectionStart.getY()),
                Integer.max(layerStart.getZ(), sectionStart.getZ()));
        var overlapEnd = new Vec3i(Integer.min(layerEnd.getX(), sectionEnd.getX()),
                Integer.min(layerEnd.getY(), sectionEnd.getY()), Integer.min(layerEnd.getZ(), sectionEnd.getZ()));
        if (overlapStart.getX() < overlapEnd.getX() && overlapStart.getY() < overlapEnd.getY()
                && overlapStart.getZ() < overlapEnd.getZ()) {
            for (var sub : sublayers) {
                sub.generateSection(section, source, allSpaces);
            }
        }
    }

    public record Factory(Vec3i start, Vec3i size, List<LayeredRiftLayout.LayoutLayer.Factory> sublayers)
            implements LayeredRiftLayout.LayoutLayer.Factory {

        public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
                Codec.INT.fieldOf("start_x").forGetter(it2 -> it2.start.getX()),
                Codec.INT.fieldOf("start_y").forGetter(it2 -> it2.start.getY()),
                Codec.INT.fieldOf("start_z").forGetter(it2 -> it2.start.getZ()),
                Codec.INT.fieldOf("size_x").forGetter(it2 -> it2.size.getX()),
                Codec.INT.fieldOf("size_y").forGetter(it2 -> it2.size.getY()),
                Codec.INT.fieldOf("size_z").forGetter(it2 -> it2.size.getZ()),
                LayeredRiftLayout.LayoutLayer.Factory.CODEC.listOf().fieldOf("layers").forGetter(it2 -> it2.sublayers)
        )
                .apply(it, (x1, y1, z1, x2, y2, z2, sublayers) -> new Factory(new Vec3i(x1, y1, z1),
                        new Vec3i(x2, y2, z2), sublayers)));

        @Override
        public LayeredRiftLayout.LayoutLayer createLayer(MinecraftServer server) {
            return new BoxedLayer(start, size,
                    sublayers.stream().map(it -> it.createLayer(server)).toArray(LayeredRiftLayout.LayoutLayer[]::new));
        }

        @Override
        public MapCodec<? extends LayeredRiftLayout.LayoutLayer.Factory> codec() {
            return CODEC;
        }
    }
}
