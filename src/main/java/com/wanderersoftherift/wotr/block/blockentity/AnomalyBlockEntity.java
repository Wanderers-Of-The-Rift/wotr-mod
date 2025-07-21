package com.wanderersoftherift.wotr.block.blockentity;

import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AnomalyBlockEntity extends BlockEntity {
    private boolean isShutDown = false; // Flag to indicate if the anomaly is shut down
//    private int shutdownTimer = 0; // Timer for shutdown effect
//    private static final int SHUTDOWN_DURATION = 100; // Duration of shutdown effect in ticks (5 seconds)

    public AnomalyBlockEntity(BlockPos pos, BlockState blockState) {
        super(WotrBlockEntities.ANOMALY_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, AnomalyBlockEntity blockEntity) {
//        if (blockEntity.isShutDown){
//            blockEntity.shutdownTimer--;
//            if (blockEntity.shutdownTimer <= 0) {
//                blockEntity.isShutDown = false; // Reset shutdown state
//                blockEntity.setChanged();
//                level.getChunkSource().blockChanged(pos);
//            }
//        }

        // Spawn particles around the anomaly
        if (!blockEntity.isShutDown && level.getGameTime() % 4 == 0) { // Every 4 ticks (5 times per second)
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY();
            double centerZ = pos.getZ() + 0.5;

            // Create swirling particles around the anomaly
            for (int i = 0; i < 3; i++) {
                double angle = (level.getGameTime() + i * 120) * 0.05; // Rotating angle
                double radius = 0.4 + Math.sin(level.getGameTime() * 0.02 + i) * 0.07; // Varying radius

                double x = centerX + Math.cos(angle) * radius;
                double z = centerZ + Math.sin(angle) * radius;
                double y = centerY + Math.sin(level.getGameTime() * 0.03 + i) * 0.1;

                level.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL, x, y, z, 1, 0, 0, 0, 0.01);
            }
        }
    }

//    desired datapackable json functioning
//{
//  "effects": [
//    {
//      "weight": 3,
//      "trigger": "on_click",
//      "actions": [
//        { "type": "mob_effect", "effect": "minecraft:regeneration", "duration": 400, "amplifier": 1 },
//        { "type": "spawn_entity", "entity": "minecraft:zombie", "count": 1 }
//      ]
//    },
//    {
//      "weight": 2,
//      "trigger": "on_item",
//      "item": "minecraft:diamond",
//      "actions": [
//        { "type": "mob_effect", "effect": "minecraft:levitation", "duration": 200, "amplifier": 2 }
//      ]
//    }
//  ]
//}


    public InteractionResult onAnomalyClick(Player player, InteractionHand hand) {
        if (!level.isClientSide && !isShutDown) {
            isShutDown = true;
//            shutdownTimer = SHUTDOWN_DURATION; // Set shutdown duration
            setChanged();

            // Force client synchronization
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().blockChanged(getBlockPos());
            }

            // Handle click logic here
            player.displayClientMessage(Component.literal("Anomaly shut down"), false);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1));
            // Spawn zombie at random nearby location (like spawners)
            Zombie zombie = new Zombie(EntityType.ZOMBIE, level);

            // Random offset similar to spawner logic
            double offsetX = (level.random.nextDouble() - level.random.nextDouble()) * 2.0;
            double offsetZ = (level.random.nextDouble() - level.random.nextDouble()) * 2.0;
            double offsetY = level.random.nextInt(3) - 1; // -1, 0, or 1

            double spawnX = getBlockPos().getX() + 0.5 + offsetX;
            double spawnY = getBlockPos().getY() + offsetY;
            double spawnZ = getBlockPos().getZ() + 0.5 + offsetZ;

            zombie.setPos(spawnX, spawnY, spawnZ);
            level.addFreshEntity(zombie);
        }
        return InteractionResult.SUCCESS;
    }

//    public boolean isShutDown(){
//        return isShutDown;
//    }

    public float getScale() {
        return isShutDown ? 0.1f : 1.0f; // 10% scale when shut down, 100% otherwise
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        // Load any data you need
        isShutDown = tag.getBoolean("isShutDown");
//        shutdownTimer = tag.getInt("shutdownTimer");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        // Save any data you need
        tag.putBoolean("isShutDown", isShutDown);
//        tag.putInt("shutdownTimer", shutdownTimer);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        loadAdditional(tag, lookupProvider);
    }

    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(
            net.minecraft.network.Connection net,
            net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt,
            HolderLookup.Provider lookupProvider) {
        handleUpdateTag(pkt.getTag(), lookupProvider);
    }
}