package com.wanderersoftherift.wotr.core.rift.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Tracks the map state of a rift
 * <p>
 * This is done by tracking the room origins, which are chunk section positions (16x16x16 offsets).
 * </p>
 */
public class RiftMapData {
    private final @NotNull Level holder;
    private final @NotNull Set<Vec3i> discovered;
    private final @NotNull Set<Vec3i> visited;

    public RiftMapData(IAttachmentHolder holder) {
        this(holder, null);
    }

    private RiftMapData(IAttachmentHolder holder, Data data) {
        if (!(holder instanceof Level level)) {
            throw new IllegalArgumentException("Holder must be level");
        }
        this.holder = level;
        if (data != null) {
            this.discovered = new LinkedHashSet<>(data.discovered);
            this.visited = new LinkedHashSet<>(data.visited);
        } else {
            this.discovered = new LinkedHashSet<>();
            this.visited = new LinkedHashSet<>();
        }
    }

    public boolean isDiscovered(SectionPos roomOrigin) {
        return discovered.contains(roomOrigin);
    }

    public boolean isDiscovered(RiftSpace space) {
        return discovered.contains(space.origin());
    }

    public boolean isVisited(SectionPos sectionPos) {
        return visited.contains(sectionPos);
    }

    public boolean isVisited(RiftSpace space) {
        return visited.contains(space.origin());
    }

    public void enterRoom(RoomRiftSpace room, Player player) {
        if (discovered.add(room.origin())) {
            // TODO: uncomment if/when it is possible to discover rooms without visiting
            // NeoForge.EVENT_BUS.post(new RiftMapEvent.RoomDiscovered(holder, room, player));
        }
        if (visited.add(room.origin())) {
            NeoForge.EVENT_BUS.post(new RiftMapEvent.RoomFirstVisited(holder, room, player));
        }
    }

    public static IAttachmentSerializer<Tag, RiftMapData> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(RiftMapData.Data.CODEC, RiftMapData::new,
                riftMapData -> new Data(List.copyOf(riftMapData.discovered), List.copyOf(riftMapData.visited)));
    }

    private record Data(List<Vec3i> discovered, List<Vec3i> visited) {
        public static final Codec<RiftMapData.Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Vec3i.CODEC.listOf().fieldOf("discovered").forGetter(Data::discovered),
                Vec3i.CODEC.listOf().fieldOf("visited").forGetter(Data::visited)
        ).apply(instance, Data::new));
    }

}
