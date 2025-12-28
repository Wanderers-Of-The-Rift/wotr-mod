package com.wanderersoftherift.wotr.world.level.levelgen.space;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.RiftSpaceCorridor;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Vec3i;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    default void forEachSection(Consumer<Vec3i> consumer) {
        for (int z = 0; z < size().getZ(); z++) {
            for (int y = 0; y < size().getY(); y++) {
                for (int x = 0; x < size().getX(); x++) {
                    consumer.accept(origin().offset(x, y, z));
                }
            }
        }
    }

    default List<Vec3i> sections() {
        List<Vec3i> result = new ArrayList<>();
        for (int z = 0; z < size().getZ(); z++) {
            for (int y = 0; y < size().getY(); y++) {
                for (int x = 0; x < size().getX(); x++) {
                    result.add(origin().offset(x, y, z));
                }
            }
        }
        return result;
    }
}
