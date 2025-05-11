package com.wanderersoftherift.wotr.entity.projectile;

import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.init.ModEntities;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.init.ModSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownExitPearl extends ThrowableItemProjectile {
    public ThrownExitPearl(EntityType<? extends ThrownExitPearl> type, Level level) {
        super(type, level);
    }

    public ThrownExitPearl(Level level, LivingEntity owner, ItemStack item) {
        super(ModEntities.THROWN_EXIT_PEARL.get(), owner, level, item);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.EXIT_PEARL.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level() instanceof ServerLevel serverLevel) {
            RiftLevelManager.spawnRiftExit(serverLevel, this.position());
            serverLevel.playSound(null, this.blockPosition(), ModSoundEvents.RIFT_OPEN.value(), this.getSoundSource(),
                    1.0F, 1.0F);

            this.discard();
        }
    }
}
