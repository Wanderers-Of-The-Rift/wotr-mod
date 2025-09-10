package com.wanderersoftherift.wotr.entity.mob;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface MobVariantInterface {
    Holder<MobVariantData> getVariant();

    void setVariant(Holder<MobVariantData> variant);

    String getMobType();

    Level level();

    // Default implementation for applying variant stats
    default void applyVariantStats() {
        Holder<MobVariantData> variant = getVariant();
        if (variant.isBound()) {
            MobVariantData data = variant.value();
            data.applyTo((LivingEntity) this);
        }
    }

    // Default implementation for handling string variants from /summon commands
    default void handleStringVariant(CompoundTag tag) {
        String variantName = tag.getString("variant");
        ResourceLocation fullVariantId;

        if (!variantName.contains(":")) {
            fullVariantId = WanderersOfTheRift.id(getMobType() + "/" + variantName);
        } else {
            fullVariantId = ResourceLocation.parse(variantName);
        }

        ResourceLocation.CODEC.encode(fullVariantId, NbtOps.INSTANCE, NbtOps.INSTANCE.empty())
                .ifSuccess(encodedTag -> tag.put("variant", encodedTag));
    }

    // Default implementation for saving variant data
    default void saveVariantData(CompoundTag tag) {
        new LaxRegistryCodec<>(WotrRegistries.Keys.MOB_VARIANTS,
                RegistryFixedCodec.create(WotrRegistries.Keys.MOB_VARIANTS))
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
            new LaxRegistryCodec<>(WotrRegistries.Keys.MOB_VARIANTS,
                    RegistryFixedCodec.create(WotrRegistries.Keys.MOB_VARIANTS))
                    .decode(level().registryAccess().createSerializationContext(NbtOps.INSTANCE), tag.get("variant"))
                    .ifSuccess(pair -> {
                        setVariant(pair.getFirst());
                    });
        }
    }

    default void initializeVariantData(
            SynchedEntityData.Builder builder,
            EntityDataAccessor<Holder<MobVariantData>> dataVariantAccessor) {
        ResourceLocation defaultId = WanderersOfTheRift.id(getMobType() + "/default_" + getMobType());
        Registry<MobVariantData> registry = level().registryAccess().lookupOrThrow(WotrRegistries.Keys.MOB_VARIANTS);

        Holder<MobVariantData> defaultVariant = registry.get(defaultId)
                .orElse(registry.getAny().orElseThrow(() -> new IllegalStateException("No mob variants registered")));

        builder.define(dataVariantAccessor, defaultVariant);
    }
}