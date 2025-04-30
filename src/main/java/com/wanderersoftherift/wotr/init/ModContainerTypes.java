package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.containers.BundleContainerType;
import com.wanderersoftherift.wotr.core.inventory.containers.ComponentContainerType;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ModContainerTypes {
    public static final ResourceKey<Registry<ContainerType>> CONTAINER_TYPE_KEY = ResourceKey
            .createRegistryKey(WanderersOfTheRift.id("container_type"));

    public static final Registry<ContainerType> CONTAINER_TYPE_REGISTRY = new RegistryBuilder<>(CONTAINER_TYPE_KEY)
            .create();

    public static final DeferredRegister<ContainerType> CONTAINER_TYPES = DeferredRegister.create(CONTAINER_TYPE_KEY,
            WanderersOfTheRift.MODID);

    public static final DeferredHolder<ContainerType, ComponentContainerType> COMPONENT_CONTAINER = CONTAINER_TYPES
            .register("component_container", ComponentContainerType::new);
    public static final DeferredHolder<ContainerType, BundleContainerType> BUNDLE_CONTAINER = CONTAINER_TYPES
            .register("bundle_container", BundleContainerType::new);
}
