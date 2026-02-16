package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftConfigDataTypes;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.RiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.UnlimitedRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizer;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A rift layout that grows branches of rooms out from the origin
 */
public class DungeonLayout implements RiftLayout {
    private static final ResourceKey<RiftParameter> BRANCH_RATE_PARAM = ResourceKey
            .create(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, WanderersOfTheRift.id("dungeon_rift/branch_rate"));
    private static final ResourceKey<RiftParameter> MAIN_ROOM_INTERVAL_PARAM = ResourceKey.create(
            WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, WanderersOfTheRift.id("dungeon_rift/main_room_interval"));
    private static final ResourceKey<RiftParameter> MAX_DEPTH_PARAM = ResourceKey
            .create(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, WanderersOfTheRift.id("dungeon_rift/max_depth"));

    private static final Vec3i MAX_ROOM_SIZE = new Vec3i(3, 3, 3);

    private final long seed;
    private final int maxDepth;
    private final float branchRate;
    private final int mainRoomInterval;
    private final Map<Vec3i, RiftSpace> spaces = new LinkedHashMap<>();
    private final AtomicReference<WeakReference<Thread>> generatorThread = new AtomicReference<>(null);
    private final CompletableFuture<Unit> generationCompletion = new CompletableFuture<>();
    private final RoomRandomizer portalRoomRandomizer;
    private final RoomRandomizer mainRoomRandomizer;
    private final RoomRandomizer connectingRoomRandomizer;
    private final RiftShape shape;

    public DungeonLayout(long seed, RiftShape shape, RoomRandomizer portalRoomRandomizer,
            RoomRandomizer mainRoomRandomizer, RoomRandomizer connectingRoomRandomizer, int maxDepth, float branchRate,
            int mainRoomInterval) {
        this.seed = seed;
        this.shape = shape;
        this.portalRoomRandomizer = portalRoomRandomizer;
        this.mainRoomRandomizer = mainRoomRandomizer;
        this.connectingRoomRandomizer = connectingRoomRandomizer;
        this.maxDepth = maxDepth;
        this.branchRate = branchRate;
        this.mainRoomInterval = mainRoomInterval;
    }

    @Override
    public RiftSpace getChunkSpace(int x, int y, int z) {
        var rand = ProcessorUtil.createRandom(
                ProcessorUtil.getRandomSeed(new BlockPos(0, 0, 0), seed));
        tryGenerate(rand);
        return getSpaceAt(x, y, z);
    }

    private RiftSpace getSpaceAt(int x, int y, int z) {
        return spaces.getOrDefault(new Vec3i(x, y, z), VoidRiftSpace.INSTANCE);
    }

    private void tryGenerate(RandomSource random) {
        if (generatorThread.get() == null && random != null
                && generatorThread.compareAndSet(null, new WeakReference<>(Thread.currentThread()))) {
            generate(random);
        }
        try {
            generationCompletion.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void generate(RandomSource random) {
        RoomRiftSpace originRoom = portalRoomRandomizer.randomSpace(random, MAX_ROOM_SIZE).offset(-1, -1, -1);
        originRoom.forEachSection((pos) -> spaces.put(pos, originRoom));

        Deque<Branch> openBranches = new ArrayDeque<>();
        openBranches.add((new Branch(originRoom, 4, 0)));

        int baseBranchCount = (int) Math.floor(branchRate);
        float additionalBranchChance = branchRate % 1;

        while (!openBranches.isEmpty()) {
            Branch branch = openBranches.pop();
            RoomRandomizer randomizer;

            boolean mainRoom = branch.depth % mainRoomInterval == 0;
            if (mainRoom) {
                randomizer = mainRoomRandomizer;
            } else {
                randomizer = connectingRoomRandomizer;
            }
            for (int i = 0; i < branch.count; i++) {
                RoomRiftSpace unpositionedRoom = randomizer.randomSpace(random, MAX_ROOM_SIZE);
                List<RoomRiftSpace> possibleRoomPlacements = findPossibleRoomPlacements(branch, unpositionedRoom);
                if (possibleRoomPlacements.isEmpty()) {
                    // No way to connect room
                    // TODO: Remove logging if satisfied this is rare enough
                    WanderersOfTheRift.LOGGER.info("Unable to find possible room location");
                    continue;
                }
                RoomRiftSpace newRoom = possibleRoomPlacements.get(random.nextInt(possibleRoomPlacements.size()));
                newRoom.forEachSection(pos -> spaces.put(pos, newRoom));
                if (branch.depth >= maxDepth - 1) {
                    continue;
                }

                int newBranches;
                if (mainRoom) {
                    newBranches = baseBranchCount + ((random.nextFloat() < additionalBranchChance) ? 1 : 0);
                } else {
                    newBranches = 1;
                }

                openBranches.add(new Branch(newRoom, newBranches, branch.depth + 1));
            }
        }

        generationCompletion.complete(Unit.INSTANCE);
    }

    /**
     * Finds possible placements a new room off of a branch, by linking opposing corridors and checking for available
     * space
     * 
     * @param branch
     * @param unpositionedRoom
     * @return A list of possible placements for the new room off of the given branch
     */
    private @NotNull List<RoomRiftSpace> findPossibleRoomPlacements(Branch branch, RoomRiftSpace unpositionedRoom) {
        return branch.origin.corridors()
                .stream()
                .filter(corridor -> !spaces.containsKey(corridor.getConnectingPos(branch.origin)))
                .flatMap(outCorridor -> {
                    Direction connectingDir = outCorridor.direction().getOpposite();
                    Vec3i connectingPos = outCorridor.getConnectingPos(branch.origin);
                    return unpositionedRoom.corridors()
                            .stream()
                            .filter(inCorridor -> inCorridor.direction() == connectingDir)
                            .map(inCorridor -> unpositionedRoom.offset(connectingPos.subtract(inCorridor.position())));
                })
                .filter(this::canPlace)
                .toList();
    }

    /**
     * @param room
     * @return Whether the chunk sections occupied by the room are currently empty
     */
    private boolean canPlace(RoomRiftSpace room) {

        for (Vec3i loc : room.sections()) {
            if (!shape.isPositionValid(loc.getX(), loc.getY(), loc.getZ()) || spaces.containsKey(loc)) {
                return false;
            }
        }
        return true;
    }

    private record Branch(RoomRiftSpace origin, int count, int depth) {
    }

    public record Factory(Optional<Long> seed) implements RiftLayout.Factory {

        public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
                Codec.LONG.optionalFieldOf("seed").forGetter(Factory::seed)
        ).apply(it, Factory::new));

        @Override
        public MapCodec<? extends RiftLayout.Factory> codec() {
            return CODEC;
        }

        @Override
        public RiftLayout createLayout(MinecraftServer server, RiftConfig riftConfig) {

            RegistryAccess registryAccess = server.registryAccess();
            RoomRandomizer portalRandomizer = new RoomRandomizerImpl.Factory(
                    registryAccess.holderOrThrow(
                            ResourceKey.create(Registries.TEMPLATE_POOL, WanderersOfTheRift.id("rift/room_portal"))),
                    RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY).createRandomizer(server);
            RoomRandomizer mainRandomizer = new RoomRandomizerImpl.Factory(
                    registryAccess.holderOrThrow(
                            ResourceKey.create(Registries.TEMPLATE_POOL, WanderersOfTheRift.id("rift/dungeon/main"))),
                    RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY).createRandomizer(server);
            RoomRandomizer connectorRandomizer = new RoomRandomizerImpl.Factory(
                    registryAccess.holderOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL,
                            WanderersOfTheRift.id("rift/dungeon/connector"))),
                    RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY).createRandomizer(server);

            int maxDepth = (int) Math.round(riftConfig.getCustomData(WotrRiftConfigDataTypes.INITIAL_RIFT_PARAMETERS)
                    .getParameter(MAX_DEPTH_PARAM)
                    .get());
            float branchRate = (float) riftConfig.getCustomData(WotrRiftConfigDataTypes.INITIAL_RIFT_PARAMETERS)
                    .getParameter(BRANCH_RATE_PARAM)
                    .get();
            int mainRoomInterval = (int) Math
                    .round(riftConfig.getCustomData(WotrRiftConfigDataTypes.INITIAL_RIFT_PARAMETERS)
                            .getParameter(MAIN_ROOM_INTERVAL_PARAM)
                            .get());

            return new DungeonLayout(this.seed.orElse(riftConfig.seed()), riftShape(riftConfig), portalRandomizer,
                    mainRandomizer, connectorRandomizer, maxDepth, branchRate, mainRoomInterval);
        }

        @Override
        public RiftShape riftShape(RiftConfig config) {
            return new UnlimitedRiftShape(24 - FastRiftGenerator.MARGIN_LAYERS);
        }
    }
}
