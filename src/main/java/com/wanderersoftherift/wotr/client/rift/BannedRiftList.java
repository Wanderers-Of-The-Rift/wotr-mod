package com.wanderersoftherift.wotr.client.rift;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class BannedRiftList {

    private final Set<ResourceLocation> bannedRifts;

    public BannedRiftList() {
        this.bannedRifts = new HashSet<>();
    }

    public BannedRiftList(Collection<ResourceLocation> bannedRifts) {
        this.bannedRifts = new HashSet<>(bannedRifts);
    }

    public boolean isBannedFrom(ResourceLocation location) {
        return bannedRifts.contains(location);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BannedRiftList other) {
            return Objects.equals(bannedRifts, other.bannedRifts);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bannedRifts);
    }

    @Override
    public String toString() {
        return "BannedRiftList[" + "bannedRifts=" + bannedRifts + ']';
    }

}
