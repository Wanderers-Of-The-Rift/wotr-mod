package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.RandomState;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ChaoticRiftLayout implements RiftLayout{

    //private final ChaoticLayoutRegion region = new ChaoticLayoutRegion(new Vec3i(-7,-ChaoticLayoutRegion.LAYERS/2,-7));

    private final ConcurrentHashMap<Vector2i, ChaoticLayoutRegion> regions = new ConcurrentHashMap<>();
    private final int layerCount;

    public ChaoticRiftLayout(int layerCount) {
        this.layerCount = layerCount;
    }

    private ChaoticLayoutRegion getOrCreateRegion(ChunkPos position){
        var regionX = Math.floorDiv(position.x+7,15);
        var regionZ = Math.floorDiv(position.z+7,15);

        return regions.computeIfAbsent(new Vector2i(regionX,regionZ),(unused)->new ChaoticLayoutRegion(new Vec3i(regionX*15-7,-layerCount /2,regionZ*15-7)));
    }

    @Override
    public List<RiftSpace> getChunkSpaces(ChunkPos chunkPos, RandomState randomState) {
        var region = getOrCreateRegion(chunkPos);
        if(randomState!=null)region.tryGenerate(randomState.getOrCreateRandomFactory(WanderersOfTheRift.id("rift_layout")).at(region.origin.getX(),0,region.origin.getZ()));
        var result = new ArrayList<RiftSpace>(layerCount);
        for (int i = 0; i < layerCount; i++) {
            result.add(region.getSpaceAt(new Vec3i(chunkPos.x,i- layerCount /2,chunkPos.z)));
        }
        return result;
    }
}
