package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.containers.BundleContainerType;
import com.wanderersoftherift.wotr.core.inventory.containers.ComponentContainerType;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrContainerTypes {

    public static final DeferredRegister<ContainerType> CONTAINER_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.CONTAINER_TYPES, WanderersOfTheRift.MODID);

    public static final DeferredHolder<ContainerType, ComponentContainerType> COMPONENT_CONTAINER = CONTAINER_TYPES
            .register("component_container", ComponentContainerType::new);
    public static final DeferredHolder<ContainerType, BundleContainerType> BUNDLE_CONTAINER = CONTAINER_TYPES
            .register("bundle_container", BundleContainerType::new);
}
