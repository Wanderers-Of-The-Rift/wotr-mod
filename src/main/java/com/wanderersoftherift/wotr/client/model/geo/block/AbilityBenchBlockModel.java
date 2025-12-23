package com.wanderersoftherift.wotr.client.model.geo.block;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.AbilityBenchBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class AbilityBenchBlockModel extends DefaultedBlockGeoModel<AbilityBenchBlockEntity> {

    public AbilityBenchBlockModel() {
        super(WanderersOfTheRift.id("ability_bench"));
    }
}
