package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

///
///
/// | state     | [#mobs]    | [#player] |
/// |-----------|------------|-----------|
/// | waiting   | -> empty   | empty     |
/// | battling  | -> present | present   |
/// | rewarding | -> empty   | present   |
/// | illegal   | -> present | empty     |
///
/// @param mobs which need to be killed
/// @param player which activated the anomaly
///
public record BattleTaskState(HashSet<UUID> mobs, Optional<UUID> player) {
    public static final Codec<BattleTaskState> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    UUIDUtil.STRING_CODEC.listOf()
                            .xmap(HashSet::new, List::copyOf)
                            .fieldOf("mobs")
                            .forGetter(BattleTaskState::mobs),
                    UUIDUtil.STRING_CODEC.optionalFieldOf("player_uuid").forGetter(BattleTaskState::player)
            ).apply(instance, BattleTaskState::new)
    );

    public boolean isPreparing() {
        return mobs.isEmpty() && player.isEmpty();
    }

    public boolean isInProgress() {
        return !mobs.isEmpty() && player.isPresent();
    }

    public boolean isRewarding() {
        return mobs.isEmpty() && player.isPresent();
    }
}
