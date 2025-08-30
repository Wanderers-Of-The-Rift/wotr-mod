package com.wanderersoftherift.wotr.core.rift;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.DispatchedMapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public interface RiftConfigDataType<T> {
    Codec<Map<Holder<RiftConfigDataType<?>>, Object>> DISPATCHED_MAP_CODEC = new DispatchedMapCodec<>(
            WotrRegistries.RIFT_CONFIG_DATA_TYPES.holderByNameCodec(), it -> it.value().codec());
    StreamCodec<RegistryFriendlyByteBuf, Map<Holder<RiftConfigDataType<?>>, Object>> DISPATCHED_MAP_STREAM_CODEC = ByteBufCodecs
            .fromCodecWithRegistries(DISPATCHED_MAP_CODEC);

    Codec<T> codec();

    T initialize(ItemStack stack, long seed, RegistryAccess registries);

    public static <T> RiftConfigDataType<T> create(
            Codec<T> codec,
            Function3<ItemStack, Long, RegistryAccess, T> initializer) {
        return new GenericRiftConfigDataType<>(codec, initializer);
    }

    record GenericRiftConfigDataType<T>(Codec<T> codec, Function3<ItemStack, Long, RegistryAccess, T> initializer)
            implements RiftConfigDataType<T> {

        @Override
        public T initialize(ItemStack stack, long seed, RegistryAccess registries) {
            return initializer.apply(stack, seed, registries);
        }
    }
}
