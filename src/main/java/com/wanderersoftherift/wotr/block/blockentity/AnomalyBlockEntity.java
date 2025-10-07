package com.wanderersoftherift.wotr.block.blockentity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyReward;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyTask;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import com.wanderersoftherift.wotr.serialization.DispatchedPairOptionalValue;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AnomalyBlockEntity extends BlockEntity implements MobDeathNotifiable {

    private static final float COMPLETED_STATE = 0.1f;
    private static final float INCOMPLETE_STATE = 1f;
    private long seed = 0L;
    private Holder<AnomalyReward> reward;
    private AnomalyState<?> state;
    private ResourceLocation panorama;

    public AnomalyBlockEntity(BlockPos pos, BlockState state) {
        super(WotrBlockEntities.ANOMALY_BLOCK_ENTITY.get(), pos, state);
    }

    public void clientTick(ClientLevel clientLevel, BlockPos pos, BlockState state1) {
        if (state != null) {
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY();
            double centerZ = pos.getZ() + 0.5;

            var count = 6;
            for (int i = 0; i < count; i++) {
                double angle = (level.getGameTime() - 25.0 * Math.sin(0.03 * level.getGameTime() + 1.2)) * 0.2
                        + i * Math.PI * 2 / count;
                double radius = 0.4 + Math.sin(level.getGameTime() * 0.03) * 0.25;

                double x = centerX + Math.cos(angle) * radius;
                double z = centerZ + Math.sin(angle) * radius;
                double y = centerY + 0.5 + Math.sin(level.getGameTime() * 0.5 + i * Math.PI * 2 / count) * 0.1
                        * (1 + Math.sin(0.03 * level.getGameTime()));

                var particleColor = state.task.value().particleColor();

                level.addParticle(new DustParticleOptions(particleColor, 0.4f), true, false, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }

    public void scheduledTick(ServerLevel serverLevel, BlockPos pos, BlockState state1) {
        if (state == null) {
            return;
        }
        var nextTick = state.scheduledTick(serverLevel, this);
        if (nextTick >= 0) {
            serverLevel.scheduleTick(pos, state1.getBlock(), nextTick);
        }

    }

    @Override
    public void notifyOfDeath(LivingEntity entity) {
        if (state == null) {
            return;
        }
        state.handleMobDeath(entity, this);
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (getLevel() instanceof ClientLevel) {
            return InteractionResult.SUCCESS;
        }
        if (state == null) {
            return InteractionResult.PASS;
        }
        return state.handleInteraction(player, hand, this);
    }

    public void closeAndReward(Player player) {
        setAnomalyState(null);
        if (reward != null && reward.isBound()) {
            reward.value().grantReward(player);
        }
    }

    public <T> void updateTask(T state) {
        this.setAnomalyState(
                new AnomalyState<T>((Holder<AnomalyTask<T>>) (Object) this.state.task(), Optional.of(state)));
        sendUpdateToPlayers();
    }

    public <T> void setAnomalyState(AnomalyState<T> state) {
        if (state != null && state.state().isEmpty()) {
            var rng = new RandomSourceFromJavaRandom(RandomSourceFromJavaRandom.get(0), seed);
            state = new AnomalyState<T>(state.task(), Optional.of(state.task().value().createState(rng)));
        }
        this.state = state;
        sendUpdateToPlayers();
    }

    public void sendUpdateToPlayers() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.players().forEach(player -> player.connection.send(getUpdatePacket()));
        }
    }

    public void setAnomalyReward(Holder<AnomalyReward> reward) {
        this.reward = reward;
    }

    public float getScale() {
        if (state == null) {
            return COMPLETED_STATE;
        } else {
            return INCOMPLETE_STATE;
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
        if (panorama != null) {
            ResourceLocation.CODEC.encodeStart(ops, panorama).ifSuccess(value -> tag.put("panorama", value));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        if (tag.contains("reward")) {
            var decode = AnomalyReward.HOLDER_CODEC.decode(ops, tag.get("reward"));
            decode.ifSuccess(value -> reward = value.getFirst());
            decode.ifError(it -> WanderersOfTheRift.LOGGER.debug(it.messageSupplier().get()));
        } else {
            reward = null;
        }
        if (tag.contains("task")) {
            var decode = AnomalyState.CODEC.decode(ops, tag.get("task"));
            decode.ifSuccess(value -> setAnomalyState(value.getFirst()));
            decode.ifError(it -> WanderersOfTheRift.LOGGER.debug(it.messageSupplier().get()));
        } else {
            setAnomalyState(null);
        }
        if (tag.contains("panorama")) {
            var decode = ResourceLocation.CODEC.decode(ops, tag.get("panorama"));
            decode.ifSuccess(value -> panorama = value.getFirst());
            decode.ifError(it -> WanderersOfTheRift.LOGGER.debug(it.messageSupplier().get()));
        } else {
            panorama = null;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public AnomalyState<?> getAnomalyState() {
        return state;
    }

    public ResourceLocation getPanorama() {
        return panorama;
    }

    public void setPanorama(ResourceLocation resourceLocation) {
        this.panorama = resourceLocation;
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

        public AnomalyTask.AnomalyTaskDisplay display() {
            if (state.isEmpty()) {
                return null;
            }
            return task.value().taskDisplay(state.get());
        }

        public void handleMobDeath(LivingEntity entity, AnomalyBlockEntity anomalyBlockEntity) {
            if (state.isEmpty()) {
                return;
            }
            task().value().handleMobDeath(entity, anomalyBlockEntity, state().get());
        }

        public int scheduledTick(ServerLevel serverLevel, AnomalyBlockEntity anomalyBlockEntity) {
            if (state.isEmpty()) {
                return -1;
            }
            return task().value().scheduledTick(serverLevel, anomalyBlockEntity, state().get());
        }
    }
}
