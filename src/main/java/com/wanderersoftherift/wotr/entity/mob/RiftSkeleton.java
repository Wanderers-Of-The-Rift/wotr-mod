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
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class RiftSkeleton extends Skeleton {
    private static final EntityDataAccessor<String> DATA_VARIANT = SynchedEntityData.defineId(RiftSkeleton.class,
            EntityDataSerializers.STRING);
    private boolean initialSpawn = true; // To set initial health to max_health on first spawn

    public RiftSkeleton(EntityType<? extends RiftSkeleton> type, Level level) {
        super(type, level);
    }

    public ResourceLocation getVariant() {
        return WanderersOfTheRift.id(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(ResourceLocation variant) {
        this.entityData.set(DATA_VARIANT, variant.getPath());
    }

    public String getMobType() {
        return "skeleton";
    }

    // Set Attributes from MobVariantData on spawn(+sets hp to max) / load
    private void applyVariantStats(boolean isInitialSpawn) {
        // TODO: Mix this into Mob so its not defined for each type of mob.
        RegistryAccess access = this.level().registryAccess();
        Registry<MobVariantData> registry = access.lookupOrThrow(WotrRegistries.Keys.MOB_VARIANTS);
        ResourceLocation prefixedVariantId = WanderersOfTheRift.id(getMobType() + "/" + getVariant().getPath());
        Optional<Holder.Reference<MobVariantData>> holder = registry.get(prefixedVariantId);
        if (holder.isPresent()) {
            MobVariantData data = holder.get().value();
            data.applyTo(this, isInitialSpawn);
            initialSpawn = false;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, "default_skeleton");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ResourceLocation variant = getVariant();
        tag.putString("variant", variant.getPath());
        tag.putBoolean("initialSpawn", initialSpawn);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        initialSpawn = (!tag.contains("initialSpawn")) || tag.getBoolean("initialSpawn");
        if (tag.contains("variant")) {
            this.entityData.set(DATA_VARIANT, tag.getString("variant"));
        }
        applyVariantStats(initialSpawn);
    }
}