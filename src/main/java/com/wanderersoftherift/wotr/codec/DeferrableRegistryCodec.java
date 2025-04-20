package com.wanderersoftherift.wotr.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;

/**
 * A registry codec that handles the case where the registry is not available (e.g. datapack registries during data gen)
 *
 * @param <E>
 */
public class DeferrableRegistryCodec<E> implements Codec<Holder<E>> {
    private final ResourceKey<? extends Registry<E>> registryKey;
    private final RegistryFixedCodec<E> registryCodec;

    private DeferrableRegistryCodec(ResourceKey<? extends Registry<E>> registryKey) {
        this.registryKey = registryKey;
        this.registryCodec = RegistryFixedCodec.create(registryKey);
    }

    public static <T> DeferrableRegistryCodec<T> create(ResourceKey<? extends Registry<T>> registryKey) {
        return new DeferrableRegistryCodec<>(registryKey);
    }

    public <T> DataResult<T> encode(Holder<E> holder, DynamicOps<T> ops, T value) {
        if (ops instanceof RegistryOps<?> registryops) {
            Optional<HolderOwner<E>> optional = registryops.owner(this.registryKey);
            if (optional.isEmpty()) {
                return holder.unwrap()
                        .map(key -> ResourceLocation.CODEC.encode(key.location(), ops, value),
                                item -> DataResult.error(() -> "Resource location not available for " + this.registryKey
                                        + " so cannot be serialized"));
            }
        }

        return registryCodec.encode(holder, ops, value);
    }

    @Override
    public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> ops, T value) {
        if (ops instanceof RegistryOps<?> registryops) {
            Optional<HolderGetter<E>> optional = registryops.getter(this.registryKey);
            if (optional.isEmpty()) {
                return ResourceLocation.CODEC.decode(ops, value)
                        .flatMap(pair -> DataResult
                                .success(Pair.of(DeferredHolder.create(registryKey, pair.getFirst()), null)));
            }
        }

        return registryCodec.decode(ops, value);
    }

    @Override
    public String toString() {
        return "DeferrableRegistryCodec[" + this.registryKey + "]";
    }
}
