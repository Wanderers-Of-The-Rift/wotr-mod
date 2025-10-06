package com.wanderersoftherift.wotr.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface VariedRiftMob {
    Holder<RiftMobVariantData> getVariant();

    void setVariant(Holder<RiftMobVariantData> variant);

    String getMobType();

    Level level();

    // Default implementation for applying variant stats
    default void applyVariantStats() {
        Holder<RiftMobVariantData> variant = getVariant();
        if (variant.isBound()) {
            RiftMobVariantData data = variant.value();
            data.applyTo((LivingEntity) this);
        }
    }

    // Default implementation for handling string variants from /summon commands
    default void handleStringVariant(CompoundTag tag) {
        String variantName = tag.getString("variant");
        if (!variantName.contains(":")) {
            tag.putString("variant", WanderersOfTheRift.id(getMobType() + "/" + variantName).toString());
        }
    }

    // Default implementation for saving variant data
    default void saveVariantData(CompoundTag tag) {
        RiftMobVariantData.VARIANT_HOLDER_CODEC
                .encode(getVariant(), level().registryAccess().createSerializationContext(NbtOps.INSTANCE),
                        NbtOps.INSTANCE.empty())
                .ifSuccess(encodedTag -> tag.put("variant", encodedTag));
    }

    // Default implementation for loading variant data
    default void loadVariantData(CompoundTag tag) {
        if (tag.contains("variant")) {
            if (tag.get("variant") instanceof StringTag) {
                handleStringVariant(tag);
            }
            RiftMobVariantData.VARIANT_HOLDER_CODEC
                    .decode(level().registryAccess().createSerializationContext(NbtOps.INSTANCE), tag.get("variant"))
                    .ifSuccess(pair -> {
                        setVariant(pair.getFirst());
                    });
        }
    }

    default void initializeVariantData(
            SynchedEntityData.Builder builder,
            EntityDataAccessor<Holder<RiftMobVariantData>> dataVariantAccessor) {
        ResourceLocation defaultId = WanderersOfTheRift.id(getMobType() + "/default_" + getMobType());
        Registry<RiftMobVariantData> registry = level().registryAccess()
                .lookupOrThrow(WotrRegistries.Keys.MOB_VARIANTS);

        Holder<RiftMobVariantData> defaultVariant = registry.get(defaultId)
                .orElseGet(() -> registry.getAny()
                        .orElseThrow(() -> new IllegalStateException("No mob variants registered")));

        builder.define(dataVariantAccessor, defaultVariant);
    }
}