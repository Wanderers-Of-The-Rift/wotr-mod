package com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpaceCorridor;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftTemplates;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.lang.ref.PhantomReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class RoomRandomizerImpl implements RoomRandomizer {

    public static final PreparableReloadListener RELOAD_LISTENER = (barrier, manager, executor1, executor2) -> {
        POOL_CACHE = null;
        return barrier.wait(null);
    };
    public static final RiftSpaceHolderFactory MULTI_SIZE_SPACE_HOLDER_FACTORY = MultiSizeRiftSpaceRandomList::new;
    public static final RiftSpaceHolderFactory SINGLE_SIZE_SPACE_HOLDER_FACTORY = MonoSizeRiftSpaceRandomList::new;
    @SuppressWarnings("StaticVariableName")
    static Pair<PhantomReference<MinecraftServer>, Map<ResourceLocation, RiftSpaceHolder>> POOL_CACHE;
    private final MinecraftServer server;
    private final ResourceLocation pool;
    private final RiftSpaceHolderFactory factory;

    public RoomRandomizerImpl(MinecraftServer server, ResourceLocation pool, RiftSpaceHolderFactory factory) {
        this.server = server;
        this.pool = pool;
        this.factory = factory;
    }

    @Override
    public RoomRiftSpace randomSpace(RandomSource randomSource, Vec3i maximumSize) {
        return getOrCreateSpaceHolder().random(maximumSize, randomSource);
    }

    private RiftSpaceHolder getOrCreateSpaceHolder() {
        var lastCache = POOL_CACHE;
        if (lastCache != null && lastCache.getA().refersTo(server)) {
            return lastCache.getB().computeIfAbsent(pool, (arg) -> createSpaceHolder());
        }
        var map = new ConcurrentHashMap<ResourceLocation, RiftSpaceHolder>();
        map.computeIfAbsent(pool, (arg) -> createSpaceHolder());
        POOL_CACHE = new Pair<>(new PhantomReference<>(server, null), map);
        return map.get(pool);
    }

    private RiftSpaceHolder createSpaceHolder() {
        return factory.create(RiftTemplates.all(server, pool), RoomRandomizerImpl::convertRoom);
    }

    // todo maybe double weight if only one diagonal mirror is applicable
    private static Stream<RoomRiftSpace> convertRoom(RiftGeneratable generatable, @Nullable Vec3i desiredTemplateSize) {
        var sizeBlocks = generatable.size();
        var sizeChunks = new Vec3i(Math.ceilDiv(sizeBlocks.getX(), 16), Math.ceilDiv(sizeBlocks.getY(), 16),
                Math.ceilDiv(sizeBlocks.getZ(), 16));
        var baseStream = TripleMirror.PERMUTATIONS.stream().map((mirror) -> {
            var modifiedSize = mirror.onlyDiagonal().applyToPosition(sizeChunks, 0, 0);
            return new RoomRiftSpace(modifiedSize,
                    new Vec3i(modifiedSize.getX() / 2, modifiedSize.getY() / 2, modifiedSize.getZ() / 2),
                    computeCorridors(generatable.jigsaws(), mirror, sizeChunks), generatable, mirror
            );
        });
        if (desiredTemplateSize != null) {
            return baseStream.filter((it) -> desiredTemplateSize.getX() >= it.size().getX()
                    && desiredTemplateSize.getY() >= it.size().getY() && desiredTemplateSize.getZ() >= it.size().getZ()
            );
        } else {
            return baseStream;
        }
    }

    private static List<RiftSpaceCorridor> computeCorridors(
            Collection<StructureTemplate.JigsawBlockInfo> jigsaws,
            TripleMirror mirror,
            Vec3i sizeChunks) {
        return jigsaws.stream()
                .filter((it) -> it.pool().getNamespace().endsWith("wotr") && it.pool().getPath().contains("rift/ring"))
                .map(jigsaw -> new RiftSpaceCorridor(
                        mirror.applyToPosition(
                                new Vec3i(jigsaw.info().pos().getX() >> 4, jigsaw.info().pos().getY() >> 4,
                                        jigsaw.info().pos().getZ() >> 4),
                                sizeChunks.getX() - 1, sizeChunks.getZ() - 1
                        ), mirror.applyToDirection(JigsawBlock.getFrontFacing(jigsaw.info().state())))
                )
                .toList();
    }

    record RoomKey(String identifier, TripleMirror t) implements Comparable<RoomKey> {

        @Override
        public int compareTo(@NotNull RoomRandomizerImpl.RoomKey o) {
            var strc = identifier.compareTo(o.identifier);
            if (strc == 0) {
                return Integer.compare(t.toInt(), o.t.toInt());
            }
            return strc;
        }
    }

    public record Factory(ResourceLocation pool, RiftSpaceHolderFactory spaceHolderFactory)
            implements RoomRandomizer.Factory {

        public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
                ResourceLocation.CODEC.fieldOf("template_pool").forGetter(Factory::pool),
                Codec.BOOL.fieldOf("is_single_size")
                        .forGetter(
                                it2 -> it2.spaceHolderFactory == RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY))
                .apply(it, (a, b) -> new Factory(a,
                        b ? SINGLE_SIZE_SPACE_HOLDER_FACTORY : MULTI_SIZE_SPACE_HOLDER_FACTORY)));

        @Override
        public RoomRandomizer createRandomizer(MinecraftServer server) {
            return new RoomRandomizerImpl(server, pool, spaceHolderFactory);
        }
    }

    public interface RiftSpaceHolderFactory {
        RiftSpaceHolder create(List<RiftGeneratable> templates, RoomConverter converter);
    }

    private interface RoomConverter {
        Stream<RoomRiftSpace> convertRoom(RiftGeneratable generatable, @Nullable Vec3i desiredTemplateSize);
    }

    interface RiftSpaceHolder {
        RoomRiftSpace random(Vec3i maxSize, RandomSource random);
    }

    private static class MultiSizeRiftSpaceRandomList implements RiftSpaceHolder {
        private final FastWeightedList<RoomRiftSpace>[] weightedListForSize = new FastWeightedList[64];

        public MultiSizeRiftSpaceRandomList(List<RiftGeneratable> templates, RoomConverter converter) {
            for (int i = 0; i < 64; i++) {
                var desiredTemplateSize = new Vec3i((i & 0b11) + 1, ((i >> 2) & 0b11) + 1, ((i >> 4) & 0b11) + 1);
                weightedListForSize[i] = FastWeightedList.byCountingDuplicates(
                        templates.stream()
                                .flatMap(template -> converter.convertRoom(template, desiredTemplateSize))
                                .toList(),
                        space -> new RoomKey(space.template().identifier(), space.templateTransform()));
            }
        }

        public RoomRiftSpace random(Vec3i maxSize, RandomSource random) {
            var i = (maxSize.getX() - 1) + 4 * (maxSize.getY() - 1) + 16 * (maxSize.getZ() - 1);
            return weightedListForSize[i].random(random);
        }
    }

    private static class MonoSizeRiftSpaceRandomList implements RiftSpaceHolder {
        private final FastWeightedList<RoomRiftSpace> weightedList;

        public MonoSizeRiftSpaceRandomList(List<RiftGeneratable> templates, RoomConverter converter) {
            weightedList = FastWeightedList.byCountingDuplicates(
                    templates.stream().flatMap(template -> converter.convertRoom(template, null)).toList(),
                    space -> new RoomKey(space.template().identifier(), space.templateTransform()));
        }

        public RoomRiftSpace random(Vec3i maxSize, RandomSource random) {
            return weightedList.random(random);
        }
    }
}
