package com.wanderersoftherift.wotr.rift.anomaly.handler;

import com.wanderersoftherift.wotr.block.blockentity.riftmobspawner.RiftMobSpawner;
import com.wanderersoftherift.wotr.block.blockentity.riftmobspawner.RiftMobSpawnerData;
import com.wanderersoftherift.wotr.block.blockentity.riftmobspawner.RiftMobSpawnerState;
import com.wanderersoftherift.wotr.rift.anomaly.state.BattleTaskState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BattleTaskHandler {
    public RiftMobSpawner mobSpawner;
    private final Set<UUID> spawnedMobs = new HashSet<>();
    private boolean battleActive = false;

    public boolean startBattle(BattleTaskState battleState, ServerLevel level, BlockPos pos) {
        if (battleActive || battleState.isStarted()) {
            return false;
        }

        if (mobSpawner == null) {
            mobSpawner = createMobSpawnerFromConfig(battleState.trialSpawnerConfig(), level);
        }

        mobSpawner.setState(level, RiftMobSpawnerState.WAITING_FOR_PLAYERS);

        int mobCount = getMobCountFromConfig(battleState.trialSpawnerConfig(), level);
        for (int i = 0; i < mobCount; i++) {
            mobSpawner.spawnMob(level, pos).ifPresent(spawnedMobs::add);
        }

        battleActive = true;
        return true;
    }

    public boolean checkBattleCompletion(ServerLevel level) {
        if (!battleActive || spawnedMobs.isEmpty()) {
            return false;
        }

        spawnedMobs.removeIf(uuid -> {
            var entity = level.getEntity(uuid);
            return entity == null || !entity.isAlive();
        });

        if (spawnedMobs.isEmpty()) {
            battleActive = false;
            return true;
        }

        return false;
    }

    public void reset() {
        spawnedMobs.clear();
        mobSpawner = null;
        battleActive = false;
    }

    public boolean isBattleActive() {
        return battleActive;
    }

    public int getSpawnedMobCount() {
        return spawnedMobs.size();
    }

    private RiftMobSpawner createMobSpawnerFromConfig(ResourceLocation configLocation, ServerLevel level) {
        ResourceKey<TrialSpawnerConfig> configKey = ResourceKey.create(Registries.TRIAL_SPAWNER_CONFIG, configLocation);
        Holder<TrialSpawnerConfig> configHolder = level.registryAccess()
                .lookupOrThrow(Registries.TRIAL_SPAWNER_CONFIG)
                .getOrThrow(configKey);

        PlayerDetector playerDetector = PlayerDetector.NO_CREATIVE_PLAYERS;
        PlayerDetector.EntitySelector entitySelector = PlayerDetector.EntitySelector.SELECT_FROM_LEVEL;
        SimpleStateAccessor stateAccessor = new SimpleStateAccessor();

        return new RiftMobSpawner(
                configHolder, configHolder, new RiftMobSpawnerData(), 36_000, 14, stateAccessor, playerDetector,
                entitySelector
        );

    }

    private int getMobCountFromConfig(ResourceLocation configLocation, ServerLevel level) {
        try {
            ResourceKey<TrialSpawnerConfig> configKey = ResourceKey.create(Registries.TRIAL_SPAWNER_CONFIG,
                    configLocation);
            Holder<TrialSpawnerConfig> configHolder = level.registryAccess()
                    .lookupOrThrow(Registries.TRIAL_SPAWNER_CONFIG)
                    .getOrThrow(configKey);
            return Math.round(configHolder.value().simultaneousMobs());
        } catch (Exception e) {
            return 3;
        }
    }

    private static class SimpleStateAccessor implements RiftMobSpawner.StateAccessor {
        private RiftMobSpawnerState state = RiftMobSpawnerState.INACTIVE;

        @Override
        public void setState(Level level, RiftMobSpawnerState state) {
            this.state = state;
        }

        @Override
        public RiftMobSpawnerState getState() {
            return state;
        }

        @Override
        public void markUpdated() {
        }
    }
}
