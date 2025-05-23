package com.wanderersoftherift.wotr.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;

/**
 * A registry codec that handles the case where the registry is not available or doesn't contain the holder reference
 *
 * @param <E>
 */
public class LaxRegistryCodec<E> implements Codec<Holder<E>> {
    private final ResourceKey<? extends Registry<E>> registryKey;
    private final RegistryFixedCodec<E> registryCodec;

    private LaxRegistryCodec(ResourceKey<? extends Registry<E>> registryKey) {
        this.registryKey = registryKey;
        this.registryCodec = RegistryFixedCodec.create(registryKey);
    }

    public static <T> LaxRegistryCodec<T> create(ResourceKey<? extends Registry<T>> registryKey) {
        return new LaxRegistryCodec<>(registryKey);
    }

    public <T> DataResult<T> encode(Holder<E> holder, DynamicOps<T> ops, T value) {
        if (holder == null) {
            return DataResult.error(
                    () -> "Holder is null for " + this.registryKey + " so cannot be serialized");
        }

        return holder.unwrap()
                .map(key -> ResourceLocation.CODEC.encode(key.location(), ops, value), item -> DataResult.error(
                        () -> "Resource location not available for " + this.registryKey + " so cannot be serialized"));
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
        return "LaxRegistryCodec[" + this.registryKey + "]";
    }
}
