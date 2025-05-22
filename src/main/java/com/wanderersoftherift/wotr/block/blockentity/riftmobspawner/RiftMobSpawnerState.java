package com.wanderersoftherift.wotr.block.blockentity.riftmobspawner;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public enum RiftMobSpawnerState implements StringRepresentable {
    INACTIVE("inactive", 0, RiftMobSpawnerState.ParticleEmission.NONE, -1.0, false),
    WAITING_FOR_PLAYERS("waiting_for_players", 4, RiftMobSpawnerState.ParticleEmission.SMALL_FLAMES, 200.0, true),
    ACTIVE("active", 8, RiftMobSpawnerState.ParticleEmission.FLAMES_AND_SMOKE, 1000.0, true),
    WAITING_FOR_REWARD_EJECTION("waiting_for_reward_ejection", 8, RiftMobSpawnerState.ParticleEmission.SMALL_FLAMES,
            -1.0, false),
    EJECTING_REWARD("ejecting_reward", 8, RiftMobSpawnerState.ParticleEmission.SMALL_FLAMES, -1.0, false),
    COOLDOWN("cooldown", 0, RiftMobSpawnerState.ParticleEmission.SMOKE_INSIDE_AND_TOP_FACE, -1.0, false);

    private static final float DELAY_BEFORE_EJECT_AFTER_KILLING_LAST_MOB = 40.0F;
    private static final int TIME_BETWEEN_EACH_EJECTION = Mth.floor(30.0F);
    private final String name;
    private final int lightLevel;
    private final double spinningMobSpeed;
    private final RiftMobSpawnerState.ParticleEmission particleEmission;
    private final boolean isCapableOfSpawning;

    private RiftMobSpawnerState(String name, int lightLevel, RiftMobSpawnerState.ParticleEmission particleEmission,
            double spinningMobSpeed, boolean isCapableOfSpawning) {
        this.name = name;
        this.lightLevel = lightLevel;
        this.particleEmission = particleEmission;
        this.spinningMobSpeed = spinningMobSpeed;
        this.isCapableOfSpawning = isCapableOfSpawning;
    }

    RiftMobSpawnerState tickAndGetNext(BlockPos pos, RiftMobSpawner spawner, ServerLevel level) {
        RiftMobSpawnerData spawnerdata = spawner.getData();
        TrialSpawnerConfig spawnerconfig = spawner.getConfig();

        return switch (this) {
            case INACTIVE -> spawnerdata.getOrCreateDisplayEntity(spawner, level, WAITING_FOR_PLAYERS) == null ? this
                    : WAITING_FOR_PLAYERS;
            case WAITING_FOR_PLAYERS -> {
                if (!spawner.canSpawnInLevel(level)) {
                    spawnerdata.resetStatistics();
                    yield this;
                } else if (!spawnerdata.hasMobToSpawn(spawner, level.random)) {
                    yield INACTIVE;
                } else {
                    spawnerdata.tryDetectPlayers(level, pos, spawner);
                    yield spawnerdata.detectedPlayers.isEmpty() ? this : ACTIVE;
                }
            }
            case ACTIVE -> {
                if (!spawner.canSpawnInLevel(level)) {
                    spawnerdata.resetStatistics();
                    yield WAITING_FOR_PLAYERS;
                } else if (!spawnerdata.hasMobToSpawn(spawner, level.random)) {
                    yield INACTIVE;
                } else {
                    int i = spawnerdata.countAdditionalPlayers(pos);
                    spawnerdata.tryDetectPlayers(level, pos, spawner);
                    if (spawner.isOminous()) {
                        this.spawnOminousOminousItemSpawner(level, pos, spawner);
                    }

                    if (spawnerdata.hasFinishedSpawningAllMobs(spawnerconfig, i)) {
                        if (spawnerdata.haveAllCurrentMobsDied()) {
                            spawnerdata.cooldownEndsAt = level.getGameTime() + (long) spawner.getTargetCooldownLength();
                            spawnerdata.totalMobsSpawned = 0;
                            spawnerdata.nextMobSpawnsAt = 0L;
                            yield WAITING_FOR_REWARD_EJECTION;
                        }
                    } else if (spawnerdata.isReadyToSpawnNextMob(level, spawnerconfig, i)) {
                        spawner.spawnMob(level, pos).ifPresent(mob -> {
                            spawnerdata.currentMobs.add(mob);
                            spawnerdata.totalMobsSpawned++;
                            spawnerdata.nextMobSpawnsAt = level.getGameTime()
                                    + (long) spawnerconfig.ticksBetweenSpawn();
                            spawnerconfig.spawnPotentialsDefinition()
                                    .getRandom(level.getRandom())
                                    .ifPresent(spawnData -> {
                                        spawnerdata.nextSpawnData = Optional.of(spawnData.data());
                                        spawner.markUpdated();
                                    });
                        });
                    }

                    yield this;
                }
            }
            case WAITING_FOR_REWARD_EJECTION -> {
                if (spawnerdata.isReadyToOpenShutter(level, 40.0F, spawner.getTargetCooldownLength())) {
                    level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_OPEN_SHUTTER, SoundSource.BLOCKS);
                    yield EJECTING_REWARD;
                } else {
                    yield this;
                }
            }
            case EJECTING_REWARD -> {
                if (!spawnerdata.isReadyToEjectItems(level, (float) TIME_BETWEEN_EACH_EJECTION,
                        spawner.getTargetCooldownLength())) {
                    yield this;
                } else if (spawnerdata.detectedPlayers.isEmpty()) {
                    level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_CLOSE_SHUTTER, SoundSource.BLOCKS);
                    spawnerdata.ejectingLootTable = Optional.empty();
                    yield COOLDOWN;
                } else {
                    if (spawnerdata.ejectingLootTable.isEmpty()) {
                        spawnerdata.ejectingLootTable = spawnerconfig.lootTablesToEject()
                                .getRandomValue(level.getRandom());
                    }

                    spawnerdata.ejectingLootTable.ifPresent(
                            lootTable -> spawner.ejectReward(level, pos, lootTable));
                    spawnerdata.detectedPlayers.remove(spawnerdata.detectedPlayers.iterator().next());
                    yield this;
                }
            }
            case COOLDOWN -> {
                spawnerdata.tryDetectPlayers(level, pos, spawner);
                if (!spawnerdata.detectedPlayers.isEmpty()) {
                    spawnerdata.totalMobsSpawned = 0;
                    spawnerdata.nextMobSpawnsAt = 0L;
                    yield ACTIVE;
                } else if (spawnerdata.isCooldownFinished(level)) {
                    spawner.removeOminous(level, pos);
                    spawnerdata.reset();
                    yield WAITING_FOR_PLAYERS;
                } else {
                    yield this;
                }
            }
        };
    }

    private void spawnOminousOminousItemSpawner(ServerLevel level, BlockPos pos, RiftMobSpawner spawner) {
        RiftMobSpawnerData riftMobSpawnerdata = spawner.getData();
        TrialSpawnerConfig spawnerconfig = spawner.getConfig();
        ItemStack itemstack = riftMobSpawnerdata.getDispensingItems(level, spawnerconfig, pos)
                .getRandomValue(level.random)
                .orElse(ItemStack.EMPTY);
        if (!itemstack.isEmpty()) {
            if (this.timeToSpawnItemSpawner(level, riftMobSpawnerdata)) {
                calculatePositionToSpawnSpawner(level, pos, spawner, riftMobSpawnerdata).ifPresent(vec3 -> {
                    OminousItemSpawner ominousitemspawner = OminousItemSpawner.create(level, itemstack);
                    ominousitemspawner.moveTo(vec3);
                    level.addFreshEntity(ominousitemspawner);
                    float f = (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F;
                    level.playSound(null, BlockPos.containing(vec3), SoundEvents.TRIAL_SPAWNER_SPAWN_ITEM_BEGIN,
                            SoundSource.BLOCKS, 1.0F, f);
                    riftMobSpawnerdata.cooldownEndsAt = level.getGameTime()
                            + spawner.getOminousConfig().ticksBetweenItemSpawners();
                });
            }
        }
    }

    private static Optional<Vec3> calculatePositionToSpawnSpawner(
            ServerLevel level,
            BlockPos pos,
            RiftMobSpawner spawner,
            RiftMobSpawnerData spawnerData) {
        List<Player> list = spawnerData.detectedPlayers.stream()
                .map(level::getPlayerByUUID)
                .filter(Objects::nonNull)
                .filter(
                        player -> !player.isCreative() && !player.isSpectator() && player.isAlive()
                                && player.distanceToSqr(
                                        pos.getCenter()) <= (double) Mth.square(spawner.getRequiredPlayerRange())
                )
                .toList();
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            Entity entity = selectEntityToSpawnItemAbove(list, spawnerData.currentMobs, spawner, pos, level);
            if (entity == null) {
                return Optional.empty();
            } else {
                return calculatePositionAbove(entity, level);
            }
        }
    }

    private static Optional<Vec3> calculatePositionAbove(Entity entity, ServerLevel level) {
        Vec3 vec3 = entity.position();
        Vec3 vec31 = vec3.relative(Direction.UP,
                (double) (entity.getBbHeight() + 2.0F + (float) level.random.nextInt(4)));
        BlockHitResult blockhitresult = level.clip(new ClipContext(vec3, vec31, ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE, CollisionContext.empty()));
        Vec3 vec32 = blockhitresult.getBlockPos().getCenter().relative(Direction.DOWN, 1.0);
        BlockPos blockpos = BlockPos.containing(vec32);
        if (!level.getBlockState(blockpos).getCollisionShape(level, blockpos).isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(vec32);
        }
    }

    @Nullable private static Entity selectEntityToSpawnItemAbove(
            List<Player> player,
            Set<UUID> currentMobs,
            RiftMobSpawner spawner,
            BlockPos pos,
            ServerLevel level) {
        Stream<Entity> stream = currentMobs.stream()
                .map(level::getEntity)
                .filter(Objects::nonNull)
                .filter(
                        entity -> entity.isAlive() && entity
                                .distanceToSqr(pos.getCenter()) <= (double) Mth.square(spawner.getRequiredPlayerRange())
                );
        List<? extends Entity> list;
        if (level.random.nextBoolean()) {
            list = stream.toList();
        } else {
            list = player;
        }
        if (list.isEmpty()) {
            return null;
        } else {
            if (list.size() == 1) {
                return list.getFirst();
            } else {
                return Util.getRandom(list, level.random);
            }
        }
    }

    private boolean timeToSpawnItemSpawner(ServerLevel level, RiftMobSpawnerData spawnerData) {
        return level.getGameTime() >= spawnerData.cooldownEndsAt;
    }

    public int lightLevel() {
        return this.lightLevel;
    }

    public double spinningMobSpeed() {
        return this.spinningMobSpeed;
    }

    public boolean hasSpinningMob() {
        return this.spinningMobSpeed >= 0.0;
    }

    public boolean isCapableOfSpawning() {
        return this.isCapableOfSpawning;
    }

    public void emitParticles(Level level, BlockPos pos, boolean isOminous) {
        this.particleEmission.emit(level, level.getRandom(), pos, isOminous);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    static class LightLevel {
        private static final int UNLIT = 0;
        private static final int HALF_LIT = 4;
        private static final int LIT = 8;

        private LightLevel() {
        }
    }

    interface ParticleEmission {
        RiftMobSpawnerState.ParticleEmission NONE = (level, randomSource, blockPos, isOminous) -> {
        };
        RiftMobSpawnerState.ParticleEmission SMALL_FLAMES = (level, randomSource, blockPos, isOminous) -> {
            if (randomSource.nextInt(2) == 0) {
                Vec3 vec3 = blockPos.getCenter().offsetRandom(randomSource, 0.9F);
                addParticle(isOminous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME, vec3, level);
            }
        };
        RiftMobSpawnerState.ParticleEmission FLAMES_AND_SMOKE = (level, randomSource, blockPos, isOminous) -> {
            Vec3 vec3 = blockPos.getCenter().offsetRandom(randomSource, 1.0F);
            addParticle(ParticleTypes.SMOKE, vec3, level);
            addParticle(isOminous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, vec3, level);
        };
        RiftMobSpawnerState.ParticleEmission SMOKE_INSIDE_AND_TOP_FACE = (level, randomSource, blockPos, isOminous) -> {
            Vec3 vec3 = blockPos.getCenter().offsetRandom(randomSource, 0.9F);
            if (randomSource.nextInt(3) == 0) {
                addParticle(ParticleTypes.SMOKE, vec3, level);
            }

            if (level.getGameTime() % 20L == 0L) {
                Vec3 vec31 = blockPos.getCenter().add(0.0, 0.5, 0.0);
                int i = level.getRandom().nextInt(4) + 20;

                for (int j = 0; j < i; j++) {
                    addParticle(ParticleTypes.SMOKE, vec31, level);
                }
            }
        };

        private static void addParticle(SimpleParticleType particleType, Vec3 pos, Level level) {
            level.addParticle(particleType, pos.x(), pos.y(), pos.z(), 0.0, 0.0, 0.0);
        }

        void emit(Level level, RandomSource random, BlockPos pos, boolean isOminous);
    }

    static class SpinningMob {
        private static final double NONE = -1.0;
        private static final double SLOW = 200.0;
        private static final double FAST = 1000.0;

        private SpinningMob() {
        }
    }
}
