package com.wanderersoftherift.wotr.client.model.geo.block;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.RiftSpawnerBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class RiftSpawnerBlockModel extends DefaultedBlockGeoModel<RiftSpawnerBlockEntity> {

    public RiftSpawnerBlockModel() {
        super(WanderersOfTheRift.id("rift_spawner"));
    }
}
