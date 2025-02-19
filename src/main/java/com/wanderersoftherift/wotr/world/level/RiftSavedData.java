package com.wanderersoftherift.wotr.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class RiftSavedData extends SavedData {
        private final ResourceKey<Level> portalDimension;
        private final BlockPos portalPos;


    public static SavedData.Factory<RiftSavedData> factory(ResourceKey<Level> portalDimension, BlockPos portalPos) {
            return new SavedData.Factory<>(() -> new RiftSavedData(portalDimension, portalPos), RiftSavedData::load);
        }

        private RiftSavedData(ResourceKey<Level> portalDimension, BlockPos portalPos) {
            this.portalDimension = portalDimension;
            this.portalPos = portalPos;
        }

        public static RiftSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
            ResourceLocation portalDimensionLocation = ResourceLocation.parse(tag.getString("PortalDimension"));
            ResourceKey<Level> portalDimension = ResourceKey.create(Registries.DIMENSION, portalDimensionLocation);
            return new RiftSavedData(portalDimension, BlockPos.of(tag.getLong("PortalPos")));
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            tag.putString("PortalDimension", this.portalDimension.location().toString());
            tag.putLong("PortalPos", this.portalPos.asLong());
            return tag;
        }

        public BlockPos getPortalPos() {
            return this.portalPos;
        }

        public ResourceKey<Level> getPortalDimension() {
            return this.portalDimension;
        }
    }
