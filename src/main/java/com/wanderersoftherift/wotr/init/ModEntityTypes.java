package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.portal.RiftPortalEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(WanderersOfTheRift.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<RiftPortalEntity>> RIFT_ENTRANCE = ENTITIES.registerEntityType(
            "rift_entrance",
            RiftPortalEntity::new,
            MobCategory.MISC,
            builder -> builder.sized(0.5f, 3f));

}
