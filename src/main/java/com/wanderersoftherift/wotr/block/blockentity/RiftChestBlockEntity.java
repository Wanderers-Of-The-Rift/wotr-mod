package com.wanderersoftherift.wotr.block.blockentity;

import com.wanderersoftherift.wotr.block.AttackableBlock;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RiftChestBlockEntity extends ChestBlockEntity implements AttackableBlock {

    private float hitpoints = 60;

    public RiftChestBlockEntity(BlockPos pos, BlockState blockState) {
        super(WotrBlockEntities.RIFT_CHEST.get(), pos, blockState);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.wotr.rift_chest");
    }

    @Override
    public boolean isAttackable(BlockState state, BlockGetter level, BlockPos position) {
        return true;
    }

    @Override
    public void attack(BlockState state, LevelAccessor level, BlockPos position, float damage, DamageSource source) {
        hitpoints -= damage;
        level.playSound(null, position, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.BLOCKS);
        ((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state, position),
                position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, 48, 0.75, 0.75, 0.75, 1.0);
        if (hitpoints < 0) {
            level.destroyBlock(position, true, source.getEntity());
        }
    }
}