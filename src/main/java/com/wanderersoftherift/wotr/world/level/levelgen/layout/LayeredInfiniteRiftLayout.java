package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.BoxedRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.FiniteRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.RiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import org.joml.Vector2i;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class LayeredInfiniteRiftLayout implements LayeredRiftLayout {

    private final ConcurrentHashMap<Vector2i, Region> regions = new ConcurrentHashMap<>();

    private final int levelCount;
    private final int seed;
    private final RiftShape riftShape;
    private final List<LayoutLayer> layers;

    public LayeredInfiniteRiftLayout(int levelCount, RiftShape riftShape, int seed, List<LayoutLayer> layers) {
        this.layers = layers;
        this.levelCount = levelCount;
        this.seed = seed;
        this.riftShape = riftShape;
    }

    private Region getOrCreateRegion(int x, int z) {
        var regionX = Math.floorDiv(x + 7, 15);
        var regionZ = Math.floorDiv(z + 7, 15);

        return regions.computeIfAbsent(new Vector2i(regionX, regionZ),
                (unused) -> new Region(new Vec3i(regionX * 15 - 7, -levelCount / 2, regionZ * 15 - 7)));
    }

    @Override
    public RiftSpace getChunkSpace(Vec3i chunkPos) {
        return getChunkSpace(chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
    }

    public RiftSpace getChunkSpace(int x, int y, int z) {
        var region = getOrCreateRegion(x, z);
        var rand = ProcessorUtil.createRandom(
                ProcessorUtil.getRandomSeed(new BlockPos(region.origin.getX(), 0, region.origin.getZ()), seed));
        region.tryGenerate(rand);
        return region.getSpaceAt(x, y, z);
    }

    private boolean hasCorridorSingle(int x, int y, int z, Direction d) {
        var space = getChunkSpace(x, y, z);
        if (space == null || space instanceof VoidRiftSpace) {
            return false;
        }
        var spaceOrigin = space.origin();
        var dx = x - spaceOrigin.getX();
        var dy = y - spaceOrigin.getY();
        var dz = z - spaceOrigin.getZ();
        for (var corridor : space.corridors()) {
            if (corridor.direction() == d && corridor.position().getX() == dx && corridor.position().getY() == dy
                    && corridor.position().getZ() == dz) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean validateCorridor(int x, int y, int z, Direction d) {
        return hasCorridorSingle(x, y, z, d)
                || hasCorridorSingle(x + d.getStepX(), y + d.getStepY(), z + d.getStepZ(), d.getOpposite());
    }

    public record Factory(RiftShape riftShape, int seed, List<LayoutLayer.Factory> layers)
            implements RiftLayout.Factory {

        public static final MapCodec<LayeredInfiniteRiftLayout.Factory> CODEC = RecordCodecBuilder
                .mapCodec(it -> it.group(
                        RiftShape.CODEC.fieldOf("shape").forGetter(LayeredInfiniteRiftLayout.Factory::riftShape),
                        Codec.INT.fieldOf("seed").forGetter(LayeredInfiniteRiftLayout.Factory::seed),
                        LayoutLayer.Factory.CODEC.listOf()
                                .fieldOf("layers")
                                .forGetter(LayeredInfiniteRiftLayout.Factory::layers)
                ).apply(it, Factory::new));

        @Override
        public MapCodec<? extends RiftLayout.Factory> codec() {
            return CODEC;
        }

        @Override
        public RiftLayout createLayout(MinecraftServer server, int levelCount) {
            return new LayeredInfiniteRiftLayout(levelCount, riftShape, seed,
                    layers.stream().map(it -> it.create(server)).toList());
        }
    }

    private class Region implements LayeredRiftLayout.LayoutSection {

        private static final RiftSpace VOID_SPACE = VoidRiftSpace.INSTANCE;

        public final Vec3i origin;

        private final RiftSpace[] spaces = new RiftSpace[15 * 15 * levelCount];
        private final long[] emptySpaces = new long[15 * 15];
        private final AtomicReference<WeakReference<Thread>> generatorThread = new AtomicReference<>(null);
        private final CompletableFuture<Unit> generationCompletion = new CompletableFuture<>();
        private final FiniteRiftShape sectionShape;

        public Region(Vec3i origin) {
            this.origin = origin;
            for (int x = 0; x < 15; x++) {
                for (int z = 0; z < 15; z++) {
                    var idx = (z * 15) + x;

                    for (int y = 0; y < levelCount; y++) {
                        if (riftShape.isPositionValid(x + origin.getX(), y + origin.getY(), z + origin.getZ())) {
                            emptySpaces[idx] |= 1L << y;
                        }
                    }
                }
            }
            this.sectionShape = new BoxedRiftShape(riftShape, origin, new Vec3i(15, levelCount, 15));
        }

        public void generate(RandomSource randomSource) {
            var allSpaces = new ArrayList<RiftSpace>();
            for (var layer : layers) {
                layer.generateSection(this, randomSource, allSpaces);
            }
            generationCompletion.complete(Unit.INSTANCE);
        }

        public RiftSpace getSpaceAt(Vec3i position) {
            if (riftShape.chaosiveness(position.getX(), position.getZ()) < Math.abs(position.getY())
                    || isOutsideThisRegion(position.getX(), position.getY(), position.getZ())) {
                return VOID_SPACE;
            }
            return spaces[(position.getX() - origin.getX()) + (position.getZ() - origin.getZ()) * 15
                    + (position.getY() - origin.getY()) * 225];
        }

        public RiftSpace getSpaceAt(int x, int y, int z) {
            if (riftShape.chaosiveness(x, z) < Math.abs(y) || isOutsideThisRegion(x, y, z)) {
                return VOID_SPACE;
            }
            return spaces[(x - origin.getX()) + (z - origin.getZ()) * 15 + (y - origin.getY()) * 225];
        }

        public void setSpaceAt(Vec3i position, RiftSpace space) {
            if (isOutsideThisRegion(position.getX(), position.getY(), position.getZ())) {
                return;
            }
            emptySpaces[(position.getX() - origin.getX())
                    + (position.getZ() - origin.getZ()) * 15] &= ~(1L << (position.getY() - origin.getY()));
            spaces[(position.getX() - origin.getX()) + (position.getZ() - origin.getZ()) * 15
                    + (position.getY() - origin.getY()) * 225] = space;
        }

        private boolean isOutsideThisRegion(int x, int y, int z) {
            return x < origin.getX() || x >= origin.getX() + 15 || y < origin.getY() || y >= origin.getY() + levelCount
                    || z < origin.getZ() || z >= origin.getZ() + 15;
        }

        private boolean canPlaceSpace(RiftSpace space) {
            for (int x = 0; x < space.size().getX(); x++) {
                for (int y = 0; y < space.size().getY(); y++) {
                    for (int z = 0; z < space.size().getZ(); z++) {
                        var position = space.origin().offset(x, y, z);
                        if (getSpaceAt(position) != null) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private void placeSpace(RiftSpace space) {
            for (int x = 0; x < space.size().getX(); x++) {
                for (int y = 0; y < space.size().getY(); y++) {
                    for (int z = 0; z < space.size().getZ(); z++) {
                        var position = space.origin().offset(x, y, z);
                        setSpaceAt(position, space);
                    }
                }
            }
        }

        @Override
        public FiniteRiftShape sectionShape() {
            return sectionShape;
        }

        @Override
        public boolean tryPlaceSpace(RiftSpace space) {
            if (!canPlaceSpace(space)) {
                return false;
            }
            placeSpace(space);
            return true;
        }

        @Override
        public long[] getEmptySpaces() {
            return emptySpaces;
        }

        public void tryGenerate(RandomSource random) {
            if (generatorThread.get() == null && random != null
                    && generatorThread.compareAndSet(null, new WeakReference(Thread.currentThread()))) {
                generate(random);
            }
            try {
                generationCompletion.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
