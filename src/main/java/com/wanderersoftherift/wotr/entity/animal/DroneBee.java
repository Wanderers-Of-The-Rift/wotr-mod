package com.wanderersoftherift.wotr.entity.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DroneBee extends Bee {

    public DroneBee(EntityType<? extends DroneBee> type, Level level) {
        super(type, level);
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 25.0)
                .add(Attributes.FLYING_SPEED, 0.3F)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    // Removes death on sting and poison, add knockback
    @Override
    public boolean doHurtTarget(@NotNull ServerLevel level, Entity target) {
        DamageSource damagesource = this.damageSources().sting(this);
        boolean hasHurt = target.hurtServer(level, damagesource,
                (float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (hasHurt) {
            EnchantmentHelper.doPostAttackEffects(level, target, damagesource);
            this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
            if (target instanceof LivingEntity entity) {
                entity.knockback(0.6, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                        (-Mth.cos(this.getYRot() * (float) (Math.PI / 180.0))));
            }
        }

        return hasHurt;
    }

    @Override
    public boolean isAngryAtAllPlayers(@NotNull ServerLevel level) {
        return this.isAngry() && this.getPersistentAngerTarget() == null;
    }

    @Override
    protected @NotNull Vec3 getPassengerAttachmentPoint(
            @NotNull Entity passenger,
            EntityDimensions passengerDims,
            float partialTick) {
        float forwardsOffset = 0;
        return new Vec3(0.0, passengerDims.height() * 0.5 + getType().getDimensions().height() * 0.5, forwardsOffset)
                .yRot(-this.getYRot() * (float) (Math.PI / 180.0));
    }
}