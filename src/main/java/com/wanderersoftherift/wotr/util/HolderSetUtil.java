package com.wanderersoftherift.wotr.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

public final class HolderSetUtil {

    private HolderSetUtil() {
    }

    public static <T> HolderSet<T> registryToHolderSet(Registry<T> registry) {
        return HolderSet.direct(ImmutableList.copyOf(registry.asHolderIdMap()));
    }

    public static <T> HolderSet<T> registryToHolderSet(RegistryAccess registryAccess, ResourceKey<Registry<T>> key) {
        return registryToHolderSet(registryAccess.lookupOrThrow(key));
    }
}
