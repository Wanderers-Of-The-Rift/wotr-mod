package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;

public class RoomRandomizerImpl implements RoomRandomizer {

    @Override
    public RoomRiftSpace randomSpace(RoomRiftSpace.RoomType roomType, RandomSource source, Vec3i maximumSize) {
        return switch (roomType){
            case CHAOS ->
                RoomRiftSpace.chaoticRiftSpace(new Vec3i(0,0,0),maximumSize);

            case UNSTABLE ->
                switch (source.nextInt(4)){
                    case 3->RoomRiftSpace.basicRiftSpace(new Vec3i(0,0,0),1,0, roomType);
                    case 2->RoomRiftSpace.basicRiftSpace(new Vec3i(0,0,0),2,0, roomType);
                    case 1->RoomRiftSpace.basicRiftSpace(new Vec3i(0,0,0),2,1, roomType);
                    default -> RoomRiftSpace.basicRiftSpace(new Vec3i(0,0,0),3,1, roomType);
                };

            case STABLE, PORTAL ->
                RoomRiftSpace.basicRiftSpace(new Vec3i(0,0,0),3,1, roomType);
        };
    }
}
