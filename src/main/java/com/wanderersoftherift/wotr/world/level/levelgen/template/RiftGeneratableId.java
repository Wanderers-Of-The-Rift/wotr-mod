package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Identifier for a {@link RiftGeneratable}. This is composed of a resource location which is either a pool or the
 * identifier of a built-in generatable, followed by the index of the item in the pool or "builtin" for built-in
 * generatables.
 */
public final class RiftGeneratableId implements Comparable<RiftGeneratableId> {

    public static final Codec<RiftGeneratableId> CODEC = Codec.STRING.xmap(s -> {
        int indexStart = s.indexOf('[');
        if (indexStart == -1 || !s.endsWith("]") || indexStart + 1 > s.length() - 1) {
            throw new RuntimeException("Invalid RiftElementId: " + s);
        }
        ResourceLocation location = ResourceLocation.tryParse(s.substring(0, indexStart));
        if (location == null) {
            throw new RuntimeException("Invalid RiftElementId: " + s);
        }
        String index = s.substring(indexStart + 1, s.length() - 1);
        return new RiftGeneratableId(location, index);
    }, RiftGeneratableId::toString);

    private final ResourceLocation resourceLocation;
    private final String index;
    // Precalcuated for performance
    private final transient String identifierString;

    public RiftGeneratableId(ResourceLocation resourceLocation, String index) {
        this.resourceLocation = resourceLocation;
        this.index = index;
        this.identifierString = resourceLocation.toString() + "[" + index + "]";
    }

    public String namespace() {
        return resourceLocation.getNamespace();
    }

    public String path() {
        return resourceLocation.getPath();
    }

    @Override
    public @NotNull String toString() {
        return identifierString;
    }

    public ResourceLocation resourceLocation() {
        return resourceLocation;
    }

    public String index() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RiftGeneratableId other) {
            return Objects.equals(this.resourceLocation, other.resourceLocation)
                    && Objects.equals(this.index, other.index);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierString);
    }

    @Override
    public int compareTo(@NotNull RiftGeneratableId o) {
        return identifierString.compareTo(o.identifierString);
    }
}
