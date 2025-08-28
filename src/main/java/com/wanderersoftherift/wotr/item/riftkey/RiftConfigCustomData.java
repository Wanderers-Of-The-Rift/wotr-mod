package com.wanderersoftherift.wotr.item.riftkey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.DispatchedMapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
 * By default has no implementation, intended as an option for 3rd parties to extend the riftConfig
 */
public interface RiftConfigCustomData {

    Codec<Map<Holder<MapCodec<? extends RiftConfigCustomData>>, RiftConfigCustomData>> DISPATCHED_MAP_CODEC = new DispatchedMapCodec<Holder<MapCodec<? extends RiftConfigCustomData>>, RiftConfigCustomData>(
            WotrRegistries.RIFT_CONFIG_CUSTOM_DATA_TYPES.holderByNameCodec(), it -> it.value().codec())
            .xmap(HashMap::new, Function.identity());
    StreamCodec<RegistryFriendlyByteBuf, Map<Holder<MapCodec<? extends RiftConfigCustomData>>, RiftConfigCustomData>> DISPATCHED_MAP_STREAM_CODEC = ByteBufCodecs
            .fromCodecWithRegistries(DISPATCHED_MAP_CODEC);

}
