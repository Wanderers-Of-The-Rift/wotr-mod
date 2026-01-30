package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

public record FindSuitableSpawnLocationSpawnFunction(float playerDistanceWeight, float spawnerDistanceWeight,
        float playerDistanceOffset, float spawnerDistanceOffset, boolean targetMobsToPlayer) implements SpawnFunction {

    public static final MapCodec<FindSuitableSpawnLocationSpawnFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("player_distance_weight")
                            .forGetter(FindSuitableSpawnLocationSpawnFunction::playerDistanceWeight),
                    Codec.FLOAT.fieldOf("spawner_distance_weight")
                            .forGetter(FindSuitableSpawnLocationSpawnFunction::spawnerDistanceWeight),
                    Codec.FLOAT.fieldOf("player_distance_offset")
                            .forGetter(FindSuitableSpawnLocationSpawnFunction::playerDistanceOffset),
                    Codec.FLOAT.fieldOf("spawner_distance_offser")
                            .forGetter(FindSuitableSpawnLocationSpawnFunction::spawnerDistanceOffset),
                    Codec.BOOL.fieldOf("target_mobs_to_player")
                            .forGetter(FindSuitableSpawnLocationSpawnFunction::targetMobsToPlayer)
            ).apply(instance, FindSuitableSpawnLocationSpawnFunction::new));

    @Override
    public MapCodec<? extends SpawnFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public void applyToMob(Mob mob, BlockEntity spawner, RandomSource random) {
        var pos = mob.position();
        var score = scoreSpawnPosition(pos, mob);
        for (int i = 0; (i < 20 && score < 10) || (i < 50 && score < 0); i++) {
            var newPos = new Vec3(pos.x() + (random.nextFloat() - random.nextFloat()) * 3.0,
                    pos.y() + (random.nextFloat() - random.nextFloat()) * 3.0,
                    pos.z() + (random.nextFloat() - random.nextFloat()) * 3.0);
            var newScore = scoreSpawnPosition(newPos, mob);
            if (newScore > score) {
                score = newScore;
                pos = newPos;
            }
        }
        mob.moveTo(pos);
    }

    private float scoreSpawnPosition(Vec3 pos, Mob mob) {
        var score = 0f;
        var posSnapshot = mob.position();
        mob.moveTo(pos);
        mob.setOnGround(true);
        var nav = mob.getNavigation();
        var players = mob.level().players();
        var path = nav.createPath(players.stream().map(it -> it.blockPosition()), 2);
        if (path != null) {
            var unreachableDist = path.getDistToTarget();
            var pathDist = path.getNodeCount();
            var player = players.stream()
                    .min(Comparator.comparingDouble(it -> it.position().distanceTo(Vec3.atCenterOf(path.getTarget()))));
            if (player.isPresent()) {
                score += 1;
                var error = player.get().position().distanceTo(Vec3.atCenterOf(path.getTarget()));
                score += playerDistanceWeight
                        * scoreByDistance(error + pathDist + unreachableDist - playerDistanceOffset);
                if (targetMobsToPlayer) {
                    mob.setTarget(player.get());
                }
            }
        }

        score += spawnerDistanceWeight * scoreByDistance(posSnapshot.distanceTo(pos) - spawnerDistanceOffset);

        if (!mob.level().noCollision(mob)) {
            score -= 20;
        }
        mob.moveTo(posSnapshot);
        return score;
    }

    private float scoreByDistance(double v) {
        return (float) Math.exp(-Math.abs(v));
    }
}
