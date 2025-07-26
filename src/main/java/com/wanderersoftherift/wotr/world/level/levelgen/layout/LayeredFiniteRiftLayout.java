package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.BoxedRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.FiniteRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public final class LayeredFiniteRiftLayout implements LayeredRiftLayout, LayeredRiftLayout.LayoutSection {

    private static final RiftSpace VOID_SPACE = VoidRiftSpace.INSTANCE;

    private final FiniteRiftShape riftShape;
    private final int layerCount;
    private final int seed;
    private final RiftSpace[] spaces;

    private final long[] emptySpaces;
    private final AtomicReference<WeakReference<Thread>> generatorThread = new AtomicReference<>(null);
    private final CompletableFuture<Unit> generationCompletion = new CompletableFuture<>();
    private final List<LayoutLayer> layers;

    public LayeredFiniteRiftLayout(FiniteRiftShape riftShape, int seed, List<LayoutLayer> layers) {
        layerCount = riftShape.levelCount();
        this.riftShape = riftShape;
        this.layers = layers;
        this.seed = seed;
        var origin = riftShape.getBoxStart();
        var width = riftShape.getBoxSize().getX();
        var length = riftShape.getBoxSize().getZ();
        spaces = new RiftSpace[width * length * this.layerCount];
        emptySpaces = new long[width * length];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                var idx = (z * width) + x;

                for (int y = 0; y < layerCount; y++) {
                    if (riftShape.isPositionValid(x + origin.getX(), y + origin.getY(), z + origin.getZ())) {
                        emptySpaces[idx] |= 1L << y;
                    }
                }
            }
        }
    }

    @Override
    public RiftSpace getChunkSpace(Vec3i chunkPos) {
        return getChunkSpace(chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
    }

    public RiftSpace getChunkSpace(int x, int y, int z) {
        var origin = riftShape.getBoxStart();
        var rand = ProcessorUtil.createRandom(
                ProcessorUtil.getRandomSeed(new BlockPos(origin.getX(), 0, origin.getZ()), seed));
        tryGenerate(rand);
        return getSpaceAt(x, y, z);
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

    public void generate(RandomSource randomSource) {
        var allSpaces = new ArrayList<RiftSpace>();
        for (var layer : layers) {
            layer.generateSection(this, randomSource, allSpaces);
        }
        generationCompletion.complete(Unit.INSTANCE);
    }

    public RiftSpace getSpaceAt(int x, int y, int z) {
        if (!riftShape.isPositionValid(x, y, z)) {
            return VOID_SPACE;
        }
        var origin = riftShape.getBoxStart();
        var size = riftShape.getBoxSize();
        return spaces[(x - origin.getX()) + (z - origin.getZ()) * size.getX()
                + (y - origin.getY()) * size.getX() * size.getZ()];
    }

    public void setSpaceAt(int x, int y, int z, RiftSpace space) {
        if (!riftShape.isPositionValid(x, y, z)) {
            return;
        }
        var origin = riftShape.getBoxStart();
        var size = riftShape.getBoxSize();
        emptySpaces[(x - origin.getX()) + (z - origin.getZ()) * size.getX()] &= ~(1L << (y - origin.getY()));
        spaces[(x - origin.getX()) + (z - origin.getZ()) * size.getX()
                + (y - origin.getY()) * size.getX() * size.getZ()] = space;
    }

    private boolean canPlaceSpace(RiftSpace space) {
        for (int x = 0; x < space.size().getX(); x++) {
            for (int y = 0; y < space.size().getY(); y++) {
                for (int z = 0; z < space.size().getZ(); z++) {
                    var positionX = space.origin().getX() + x;
                    var positionY = space.origin().getY() + y;
                    var positionZ = space.origin().getZ() + z;
                    if (getSpaceAt(positionX, positionY, positionZ) != null) {
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
                    var positionX = space.origin().getX() + x;
                    var positionY = space.origin().getY() + y;
                    var positionZ = space.origin().getZ() + z;
                    setSpaceAt(positionX, positionY, positionZ, space);
                }
            }
        }
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

    @Override
    public FiniteRiftShape sectionShape() {
        return riftShape;
    }

    public static record Factory(BoxedRiftShape riftShape, Optional<Integer> seed,
            List<LayeredRiftLayout.LayoutLayer.Factory> layers) implements LayeredRiftLayout.Factory {

        public static final MapCodec<LayeredFiniteRiftLayout.Factory> CODEC = RecordCodecBuilder
                .mapCodec(it -> it.group(
                        BoxedRiftShape.CODEC.fieldOf("shape").forGetter(LayeredFiniteRiftLayout.Factory::riftShape),
                        Codec.INT.optionalFieldOf("seed").forGetter(LayeredFiniteRiftLayout.Factory::seed),
                        LayoutLayer.Factory.CODEC.listOf()
                                .fieldOf("layers")
                                .forGetter(LayeredFiniteRiftLayout.Factory::layers)
                ).apply(it, Factory::new));

        @Override
        public MapCodec<? extends RiftLayout.Factory> codec() {
            return CODEC;
        }

        @Override
        public RiftLayout createLayout(MinecraftServer server, int seed, RiftConfig riftConfig) {
            return new LayeredFiniteRiftLayout(riftShape, this.seed.orElse(seed),
                    layers.stream().map(it -> it.createLayer(server, riftConfig)).toList());
        }

        @Override
        public LayeredRiftLayout.Factory withLayers(List<LayoutLayer.Factory> layers) {
            return new Factory(riftShape, seed, layers);
        }
    }
}
