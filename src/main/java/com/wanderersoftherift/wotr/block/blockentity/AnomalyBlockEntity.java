package com.wanderersoftherift.wotr.block.blockentity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyReward;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyTask;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.BattleTaskState;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import com.wanderersoftherift.wotr.serialization.DispatchedPairOptionalValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class AnomalyBlockEntity extends BlockEntity {

    private Holder<AnomalyReward> reward;
    private AnomalyState<?> state;

    public AnomalyBlockEntity(BlockPos pos, BlockState state) {
        super(WotrBlockEntities.ANOMALY_BLOCK_ENTITY.get(), pos, state);
    }

    public void tick(ClientLevel clientLevel, BlockPos pos, BlockState state1) {
        // todo spawn particles
    }

    public void scheduledTick(ServerLevel serverLevel, BlockPos pos, BlockState state1) {
        var taskState = state.state();
        if (taskState.isPresent() && taskState.get() instanceof BattleTaskState battleTask) {
            if (battleTask.isRewarding()) {
                var player = serverLevel.getPlayerByUUID(battleTask.player().get());
                if (player != null) {
                    closeAndReward(player);
                    return;
                }
            }
        }
        serverLevel.scheduleTick(pos, state1.getBlock(), 1);

    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (state != null) {
            return state.handleInteraction(player, hand, this);
        }
        return InteractionResult.PASS;
    }

    public <T> void updateTask(T state) {
        this.state = new AnomalyState<T>((Holder<AnomalyTask<T>>) (Object) this.state.task(), Optional.of(state));
    }

    public void closeAndReward(Player player) {
        state = null;
        if (reward != null && reward.isBound()) {
            reward.value().grantReward(player);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        if (reward != null) {
            AnomalyReward.HOLDER_CODEC.encodeStart(ops, reward).ifSuccess(value -> tag.put("reward", value));
        }
        if (state != null) {
            AnomalyState.CODEC.encodeStart(ops, (AnomalyState<Object>) state)
                    .ifSuccess(value -> tag.put("task", value));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        if (tag.contains("reward")) {
            AnomalyReward.HOLDER_CODEC.decode(ops, tag.get("reward")).ifSuccess(value -> reward = value.getFirst());
        }
        if (tag.contains("task")) {
            AnomalyState.CODEC.decode(ops, tag.get("task")).ifSuccess(value -> state = value.getFirst());
        }
    }

    public record AnomalyState<T>(Holder<AnomalyTask<T>> task, Optional<T> state) {
        private static final Codec<AnomalyState<Object>> CODEC = createCodec().xmap(
                pair -> new AnomalyState<>(pair.getFirst(), pair.getSecond()),
                state -> new Pair<>(state.task(), state.state()));

        private static <T> DispatchedPairOptionalValue<Holder<AnomalyTask<T>>, T> createCodec() {
            return new DispatchedPairOptionalValue<>(
                    (Codec<Holder<AnomalyTask<T>>>) (Object) AnomalyTask.HOLDER_CODEC.fieldOf("task").codec(), "state",
                    it -> it.value().type().stateCodec());
        }

        public static <T> Codec<AnomalyState<T>> codec() {
            return (Codec<AnomalyState<T>>) (Object) CODEC;
        }

        public InteractionResult handleInteraction(
                Player player,
                InteractionHand hand,
                AnomalyBlockEntity anomalyBlockEntity) {
            return task().value().interact(player, hand, anomalyBlockEntity, state.orElseThrow());
        }
    }
}
