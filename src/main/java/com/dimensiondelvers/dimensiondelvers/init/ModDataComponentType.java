package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.item.runegem.Runegem;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSockets;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponentType {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, DimensionDelvers.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GearSockets>> GEAR_SOCKETS  = register("gear_sockets", GearSockets.CODEC, GearSockets.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RunegemData>> RUNEGEM_DATA  = register("runegem_data", RunegemData.CODEC, null);

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, final Codec<T> codec, final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        if (streamCodec == null) {
            return DATA_COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).build());
        } else {
            return DATA_COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
        }
    }
}
