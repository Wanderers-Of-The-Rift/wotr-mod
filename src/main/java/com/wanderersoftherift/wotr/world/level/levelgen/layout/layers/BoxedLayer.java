package com.wanderersoftherift.wotr.world.level.levelgen.layout.layers;

import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;

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
}
