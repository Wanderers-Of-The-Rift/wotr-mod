package com.wanderersoftherift.wotr.world.level.levelgen.space;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.RiftSpaceCorridor;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Vec3i;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This describes volume allocated for room to generate in
 *
 * size units are in chunks
 */
public interface RiftSpace {

    Vec3i origin();

    Vec3i size();

    List<RiftSpaceCorridor> corridors();

    TripleMirror templateTransform();

    @Nullable RiftGeneratable template();
}
