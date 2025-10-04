package com.wanderersoftherift.wotr.entity.mob;

import com.wanderersoftherift.wotr.init.WotrEntityDataSerializers;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

public class RiftSkeleton extends Skeleton implements VariedRiftMob {
    private static final EntityDataAccessor<Holder<RiftMobVariantData>> DATA_VARIANT = SynchedEntityData
            .defineId(RiftSkeleton.class, WotrEntityDataSerializers.MOB_VARIANT_HOLDER.get());

    public RiftSkeleton(EntityType<? extends RiftSkeleton> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSkeleton.createAttributes();
    }

    public Holder<RiftMobVariantData> getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(Holder<RiftMobVariantData> variant) {
        this.entityData.set(DATA_VARIANT, variant);
    }

    public String getMobType() {
        return "skeleton";
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        initializeVariantData(builder, DATA_VARIANT);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        saveVariantData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        loadVariantData(tag);
    }
}