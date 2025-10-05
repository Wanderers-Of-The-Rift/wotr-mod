package com.wanderersoftherift.wotr.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class RiftZombie extends Zombie {
    private static final EntityDataAccessor<String> DATA_VARIANT = SynchedEntityData.defineId(RiftZombie.class,
            EntityDataSerializers.STRING);
    private boolean initialSpawn = true; // To set initial health to max_health on first spawn

    public RiftZombie(EntityType<? extends RiftZombie> type, Level level) {
        super(type, level);
    }

    public String getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    // Set Attributes from MobVariantData on spawn(+sets hp to max) / load
    private void applyVariantStats(boolean isInitialSpawn) {
        // TODO: Mix this into Mob so its not defined for each type of mob.
        RegistryAccess access = this.level().registryAccess();
        Registry<MobVariantData> registry = access.lookupOrThrow(WotrRegistries.Keys.MOB_VARIANTS);
        ResourceLocation variantId = WanderersOfTheRift.id(getVariant());
        Optional<Holder.Reference<MobVariantData>> holder = registry.get(variantId);
        if (holder.isPresent()) {
            MobVariantData data = holder.get().value();
            data.applyTo(this, isInitialSpawn);
            initialSpawn = false;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, "default");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("variant", getVariant());
        tag.putBoolean("initialSpawn", initialSpawn);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("initialSpawn")) {
            initialSpawn = tag.getBoolean("initialSpawn");
        } else {
            initialSpawn = true;
        }
        if (tag.contains("variant")) {
            this.entityData.set(DATA_VARIANT, tag.getString("variant"));
        }
        applyVariantStats(initialSpawn);
    }

    // todo delete after #344
    public void setVariant(String variant) {
        this.entityData.set(DATA_VARIANT, variant);
        applyVariantStats(initialSpawn);
    }
}