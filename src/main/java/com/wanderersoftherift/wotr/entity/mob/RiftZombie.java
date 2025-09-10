package com.wanderersoftherift.wotr.entity.mob;

import com.wanderersoftherift.wotr.init.WotrEntityDataSerializers;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

// Requires: RiftZombie.java, RiftZombieRenderer.java, RiftZombieRenderState.java
// Append to: WotrEntityAttributes.java, WotrEntities.java, WotrEntityRenderers.java
// Uses variant_zombie.json for stats, or default attributes for missing stats.
public class RiftZombie extends Zombie implements MobVariantInterface {
    private static final EntityDataAccessor<Holder<MobVariantData>> DATA_VARIANT = SynchedEntityData
            .defineId(RiftZombie.class, WotrEntityDataSerializers.MOB_VARIANT_HOLDER.get());

    public RiftZombie(EntityType<? extends RiftZombie> type, Level level) {
        super(type, level);
    }

    public Holder<MobVariantData> getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(Holder<MobVariantData> variant) {
        this.entityData.set(DATA_VARIANT, variant);
    }

    public String getMobType() {
        return "zombie";
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