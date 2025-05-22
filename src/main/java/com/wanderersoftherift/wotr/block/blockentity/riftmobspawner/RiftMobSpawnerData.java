package com.wanderersoftherift.wotr.block.blockentity.riftmobspawner;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class RiftMobSpawnerData {
    public static final String TAG_SPAWN_DATA = "spawn_data";
    public static final MapCodec<RiftMobSpawnerData> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    UUIDUtil.CODEC_SET.lenientOptionalFieldOf("registered_players", Sets.newHashSet())
                            .forGetter(spawnerData -> spawnerData.detectedPlayers),
                    UUIDUtil.CODEC_SET.lenientOptionalFieldOf("current_mobs", Sets.newHashSet())
                            .forGetter(spawnerData -> spawnerData.currentMobs),
                    Codec.LONG.lenientOptionalFieldOf("cooldown_ends_at", Long.valueOf(0L))
                            .forGetter(spawnerData -> spawnerData.cooldownEndsAt),
                    Codec.LONG.lenientOptionalFieldOf("next_mob_spawns_at", Long.valueOf(0L))
                            .forGetter(spawnerData -> spawnerData.nextMobSpawnsAt),
                    Codec.intRange(0, Integer.MAX_VALUE)
                            .lenientOptionalFieldOf("total_mobs_spawned", 0)
                            .forGetter(spawnerData -> spawnerData.totalMobsSpawned),
                    SpawnData.CODEC.lenientOptionalFieldOf("spawn_data")
                            .forGetter(spawnerData -> spawnerData.nextSpawnData),
                    ResourceKey.codec(Registries.LOOT_TABLE)
                            .lenientOptionalFieldOf("ejecting_loot_table")
                            .forGetter(spawnerData -> spawnerData.ejectingLootTable)
            ).apply(instance, RiftMobSpawnerData::new)
    );
    private static final String TAG_NEXT_MOB_SPAWNS_AT = "next_mob_spawns_at";
    private static final int DELAY_BETWEEN_PLAYER_SCANS = 20;
    private static final int TRIAL_OMEN_PER_BAD_OMEN_LEVEL = 18_000;
    protected final Set<UUID> detectedPlayers = new HashSet<>();
    protected final Set<UUID> currentMobs = new HashSet<>();
    protected long cooldownEndsAt;
    protected long nextMobSpawnsAt;
    protected int totalMobsSpawned;
    protected Optional<SpawnData> nextSpawnData;
    protected Optional<ResourceKey<LootTable>> ejectingLootTable;
    @Nullable protected Entity displayEntity;
    protected double spin;
    protected double oSpin;
    @Nullable private SimpleWeightedRandomList<ItemStack> dispensing;

    public RiftMobSpawnerData() {
        this(Collections.emptySet(), Collections.emptySet(), 0L, 0L, 0, Optional.empty(), Optional.empty());
    }

    public RiftMobSpawnerData(Set<UUID> detectedPlayers, Set<UUID> currentMobs, long cooldownEndsAt,
            long nextMobSpawnsAt, int totalMobsSpawned, Optional<SpawnData> nextSpawnData,
            Optional<ResourceKey<LootTable>> ejectingLootTable) {
        this.detectedPlayers.addAll(detectedPlayers);
        this.currentMobs.addAll(currentMobs);
        this.cooldownEndsAt = cooldownEndsAt;
        this.nextMobSpawnsAt = nextMobSpawnsAt;
        this.totalMobsSpawned = totalMobsSpawned;
        this.nextSpawnData = nextSpawnData;
        this.ejectingLootTable = ejectingLootTable;
    }

    public void reset() {
        this.currentMobs.clear();
        this.nextSpawnData = Optional.empty();
        this.resetStatistics();
    }

    public void resetStatistics() {
        this.detectedPlayers.clear();
        this.totalMobsSpawned = 0;
        this.nextMobSpawnsAt = 0L;
        this.cooldownEndsAt = 0L;
    }

    public boolean hasMobToSpawn(RiftMobSpawner riftMobSpawner, RandomSource random) {
        boolean flag = this.getOrCreateNextSpawnData(riftMobSpawner, random).getEntityToSpawn().contains("id", 8);
        return flag || !riftMobSpawner.getConfig().spawnPotentialsDefinition().isEmpty();
    }

    public boolean hasFinishedSpawningAllMobs(TrialSpawnerConfig config, int players) {
        return this.totalMobsSpawned >= config.calculateTargetTotalMobs(players);
    }

    public boolean haveAllCurrentMobsDied() {
        return this.currentMobs.isEmpty();
    }

    public boolean isReadyToSpawnNextMob(ServerLevel level, TrialSpawnerConfig config, int players) {
        return level.getGameTime() >= this.nextMobSpawnsAt
                && this.currentMobs.size() < config.calculateTargetSimultaneousMobs(players);
    }

    public int countAdditionalPlayers(BlockPos pos) {
        if (this.detectedPlayers.isEmpty()) {
            Util.logAndPauseIfInIde("Trial Spawner at " + pos + " has no detected players");
        }

        return Math.max(0, this.detectedPlayers.size() - 1);
    }

    public void tryDetectPlayers(ServerLevel level, BlockPos pos, RiftMobSpawner spawner) {
        boolean flag = (pos.asLong() + level.getGameTime()) % 20L != 0L;
        if (!flag) {
            if (!RiftMobSpawnerState.COOLDOWN.equals(spawner.getState()) || !spawner.isOminous()) {
                List<UUID> list = spawner.getPlayerDetector()
                        .detect(level, spawner.getEntitySelector(), pos, (double) spawner.getRequiredPlayerRange(),
                                true);
                boolean flag1;
                if (!spawner.isOminous() && !list.isEmpty()) {
                    Optional<Pair<Player, Holder<MobEffect>>> optional = findPlayerWithOminousEffect(level, list);
                    optional.ifPresent(playerEffectPair -> {
                        Player player = playerEffectPair.getFirst();
                        if (playerEffectPair.getSecond() == MobEffects.BAD_OMEN) {
                            transformBadOmenIntoTrialOmen(player);
                        }

                        level.levelEvent(3020, BlockPos.containing(player.getEyePosition()), 0);
                        spawner.applyOminous(level, pos);
                    });
                    flag1 = optional.isPresent();
                } else {
                    flag1 = false;
                }

                if (!RiftMobSpawnerState.COOLDOWN.equals(spawner.getState()) || flag1) {
                    boolean flag2 = spawner.getData().detectedPlayers.isEmpty();
                    List<UUID> list1;
                    if (flag2) {
                        list1 = list;
                    } else {
                        list1 = spawner.getPlayerDetector()
                                .detect(level, spawner.getEntitySelector(), pos,
                                        (double) spawner.getRequiredPlayerRange(), false);
                    }
                    if (this.detectedPlayers.addAll(list1)) {
                        this.nextMobSpawnsAt = Math.max(level.getGameTime() + 40L, this.nextMobSpawnsAt);
                        if (!flag1) {
                            int i;
                            if (spawner.isOminous()) {
                                i = 3019;
                            } else {
                                i = 3013;
                            }
                            level.levelEvent(i, pos, this.detectedPlayers.size());
                        }
                    }
                }
            }
        }
    }

    private static Optional<Pair<Player, Holder<MobEffect>>> findPlayerWithOminousEffect(
            ServerLevel level,
            List<UUID> players) {
        Player player = null;

        for (UUID uuid : players) {
            Player player1 = level.getPlayerByUUID(uuid);
            if (player1 != null) {
                Holder<MobEffect> holder = MobEffects.TRIAL_OMEN;
                if (player1.hasEffect(holder)) {
                    return Optional.of(Pair.of(player1, holder));
                }

                if (player1.hasEffect(MobEffects.BAD_OMEN)) {
                    player = player1;
                }
            }
        }

        return Optional.ofNullable(player).map(player1 -> Pair.of(player1, MobEffects.BAD_OMEN));
    }

    public void resetAfterBecomingOminous(RiftMobSpawner spawner, ServerLevel level) {
        this.currentMobs.stream().map(level::getEntity).forEach(entity -> {
            if (entity != null) {
                level.levelEvent(3012, entity.blockPosition(), RiftMobSpawner.FlameParticle.NORMAL.encode());
                if (entity instanceof Mob mob) {
                    mob.dropPreservedEquipment(level);
                }

                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        });
        if (!spawner.getOminousConfig().spawnPotentialsDefinition().isEmpty()) {
            this.nextSpawnData = Optional.empty();
        }

        this.totalMobsSpawned = 0;
        this.currentMobs.clear();
        this.nextMobSpawnsAt = level.getGameTime() + (long) spawner.getOminousConfig().ticksBetweenSpawn();
        spawner.markUpdated();
        this.cooldownEndsAt = level.getGameTime() + spawner.getOminousConfig().ticksBetweenItemSpawners();
    }

    private static void transformBadOmenIntoTrialOmen(Player player) {
        MobEffectInstance mobeffectinstance = player.getEffect(MobEffects.BAD_OMEN);
        if (mobeffectinstance != null) {
            int i = mobeffectinstance.getAmplifier() + 1;
            int j = 18_000 * i;
            player.removeEffect(MobEffects.BAD_OMEN);
            player.addEffect(new MobEffectInstance(MobEffects.TRIAL_OMEN, j, 0));
        }
    }

    public boolean isReadyToOpenShutter(ServerLevel level, float delay, int targetCooldownLength) {
        long i = this.cooldownEndsAt - (long) targetCooldownLength;
        return (float) level.getGameTime() >= (float) i + delay;
    }

    public boolean isReadyToEjectItems(ServerLevel level, float delay, int targetCooldownLength) {
        long i = this.cooldownEndsAt - (long) targetCooldownLength;
        return (float) (level.getGameTime() - i) % delay == 0.0F;
    }

    public boolean isCooldownFinished(ServerLevel level) {
        return level.getGameTime() >= this.cooldownEndsAt;
    }

    protected SpawnData getOrCreateNextSpawnData(RiftMobSpawner spawner, RandomSource random) {
        if (this.nextSpawnData.isPresent()) {
            return this.nextSpawnData.get();
        } else {
            SimpleWeightedRandomList<SpawnData> simpleweightedrandomlist = spawner.getConfig()
                    .spawnPotentialsDefinition();
            Optional<SpawnData> optional;
            if (simpleweightedrandomlist.isEmpty()) {
                optional = this.nextSpawnData;
            } else {
                optional = simpleweightedrandomlist.getRandom(random).map(WeightedEntry.Wrapper::data);
            }
            this.nextSpawnData = Optional.of(optional.orElseGet(SpawnData::new));
            spawner.markUpdated();
            return this.nextSpawnData.get();
        }
    }

    @Nullable public Entity getOrCreateDisplayEntity(RiftMobSpawner spawner, Level level, RiftMobSpawnerState spawnerState) {
        if (!spawnerState.hasSpinningMob()) {
            return null;
        } else {
            if (this.displayEntity == null) {
                CompoundTag compoundtag = this.getOrCreateNextSpawnData(spawner, level.getRandom()).getEntityToSpawn();
                if (compoundtag.contains("id", 8)) {
                    this.displayEntity = EntityType.loadEntityRecursive(compoundtag, level,
                            EntitySpawnReason.TRIAL_SPAWNER, Function.identity());
                }
            }

            return this.displayEntity;
        }
    }

    public CompoundTag getUpdateTag(RiftMobSpawnerState spawnerState) {
        CompoundTag compoundtag = new CompoundTag();
        if (spawnerState == RiftMobSpawnerState.ACTIVE) {
            compoundtag.putLong("next_mob_spawns_at", this.nextMobSpawnsAt);
        }

        this.nextSpawnData.ifPresent(
                spawnData -> compoundtag.put(
                        "spawn_data",
                        SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, spawnData)
                                .result()
                                .orElseThrow(() -> new IllegalStateException("Invalid SpawnData"))
                )
        );
        return compoundtag;
    }

    public double getSpin() {
        return this.spin;
    }

    public double getOSpin() {
        return this.oSpin;
    }

    SimpleWeightedRandomList<ItemStack> getDispensingItems(ServerLevel level, TrialSpawnerConfig config, BlockPos pos) {
        if (this.dispensing != null) {
            return this.dispensing;
        } else {
            LootTable loottable = level.getServer()
                    .reloadableRegistries()
                    .getLootTable(config.itemsToDropWhenOminous());
            LootParams lootparams = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
            long i = lowResolutionPosition(level, pos);
            ObjectArrayList<ItemStack> objectarraylist = loottable.getRandomItems(lootparams, i);
            if (objectarraylist.isEmpty()) {
                return SimpleWeightedRandomList.empty();
            } else {
                SimpleWeightedRandomList.Builder<ItemStack> builder = new SimpleWeightedRandomList.Builder<>();

                for (ItemStack itemstack : objectarraylist) {
                    builder.add(itemstack.copyWithCount(1), itemstack.getCount());
                }

                this.dispensing = builder.build();
                return this.dispensing;
            }
        }
    }

    private static long lowResolutionPosition(ServerLevel level, BlockPos pos) {
        BlockPos blockpos = new BlockPos(
                Mth.floor((float) pos.getX() / 30.0F), Mth.floor((float) pos.getY() / 20.0F),
                Mth.floor((float) pos.getZ() / 30.0F)
        );
        return level.getSeed() + blockpos.asLong();
    }
}
