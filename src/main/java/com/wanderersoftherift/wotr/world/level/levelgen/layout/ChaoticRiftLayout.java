package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpaceCorridor;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.RandomState;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class ChaoticRiftLayout implements RiftLayout{

    private final ConcurrentHashMap<Vector2i, Region> regions = new ConcurrentHashMap<>();
    private final int layerCount;

    public ChaoticRiftLayout(int layerCount) {
        this.layerCount = layerCount;
    }

    private Region getOrCreateRegion(ChunkPos position){
        var regionX = Math.floorDiv(position.x+7,15);
        var regionZ = Math.floorDiv(position.z+7,15);

        return regions.computeIfAbsent(new Vector2i(regionX,regionZ),(unused)->new Region(new Vec3i(regionX*15-7,-layerCount /2,regionZ*15-7)));
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

    private class Region {

        private static final RiftSpace VOID_SPACE = new VoidRiftSpace();

        private final RiftSpace[] spaces = new RiftSpace[15*15* layerCount];

        public final Vec3i origin;
        private final AtomicReference<Thread> generatorThread = new AtomicReference<>(null);
        private final CompletableFuture<Unit> generationCompletion = new CompletableFuture<>();

        public Region(Vec3i origin) {
            this.origin = origin;
        }

        public void generate(RandomSource random){
            var currentSpaces = Collections.<RiftSpace>emptyList();
            var nextSpaces = generateNonChaotic(random);
            while (!nextSpaces.isEmpty()){
                currentSpaces=nextSpaces;
                nextSpaces=new ArrayList<>();
                for (var space:currentSpaces){

                    for (var corridor:space.corridors()){
                        var possibleNextSpaces = getNextSpacesChaotic(corridor,space.origin());
                        var randomSpace = getRandomSpace(possibleNextSpaces);
                        if(randomSpace!=null && tryPlaceSpace(randomSpace)) {
                            nextSpaces.add(randomSpace);
                        }
                    }
                }
            }

            generationCompletion.complete(Unit.INSTANCE);
        }

        private List<RiftSpace> generateNonChaotic(RandomSource random) {

            var rooms  = new ArrayList<RiftSpace>();
            for (int x = 0; x < 5; x++) {

                for (int z = 0; z < 5; z++) {
                    var roomCenter = new Vector2i(x*3+origin.getX()+1,z*3+origin.getZ()+1);
                    var category = categorize(new Vector2d(roomCenter.x+.5,roomCenter.y+.5));
                    if(category==0){
                        var room = RoomRiftSpace.basicRiftSpace(new Vec3i(roomCenter.x(), 0, roomCenter.y()),3,1,(roomCenter.x()==0 && roomCenter.y() == 0)? RoomRiftSpace.RoomType.PORTAL: RoomRiftSpace.RoomType.STABLE);
                        if(tryPlaceSpace(room))rooms.add(room);
                    }else if (category == 1){
                        var room = switch (random.nextInt(4)){
                            case 1->RoomRiftSpace.basicRiftSpace(new Vec3i(roomCenter.x(), 1, roomCenter.y()),2,0, RoomRiftSpace.RoomType.UNSTABLE);
                            case 2->RoomRiftSpace.basicRiftSpace(new Vec3i(roomCenter.x(), 0, roomCenter.y()),2,1, RoomRiftSpace.RoomType.UNSTABLE);
                            case 3->RoomRiftSpace.basicRiftSpace(new Vec3i(roomCenter.x(), 0, roomCenter.y()),1,0, RoomRiftSpace.RoomType.UNSTABLE);
                            default->RoomRiftSpace.basicRiftSpace(new Vec3i(roomCenter.x(), 0, roomCenter.y()),3,1, RoomRiftSpace.RoomType.UNSTABLE);
                        };
                        if(tryPlaceSpace(room))rooms.add(room);
                    }
                }
            }
            if (rooms.isEmpty()){
                var roomCenter = new Vector2i(origin.getX()+1,origin.getZ()+1);
                rooms.add(RoomRiftSpace.chaoticRiftSpace(new Vec3i(roomCenter.x(), 0, roomCenter.y()),new Vec3i(1,1,1)));
            }
            return rooms;
        }

        private double chaosiveness(Vector2d position){
            return 1.5*Math.cosh(0.055*position.length());
        }

        //2 = chaotic, 1 = unstable, 0 = stable
        private int categorize(Vector2d position){
            var chaosiveness = chaosiveness(position);
            return chaosiveness>2.5?2:chaosiveness>1.75?1:0;
        }


        private RiftSpace getRandomSpace(List<RiftSpace> possibleNextSpaces) {
            if(possibleNextSpaces.isEmpty()) return null;
            var n = (int)(possibleNextSpaces.size()*Math.random());
            return possibleNextSpaces.get(n);
        }

        private List<RiftSpace> getNextSpacesChaotic(RiftSpaceCorridor corrider, Vec3i origin) {
            var nextSpaces = new ArrayList<RiftSpace>(32);
            for (int variation = 0; variation < 27; variation++) {
                var width = 1+variation%3;
                var depth = 1+(variation/3)%3;
                var height = 1+(variation/9)%3;
                var baseSpace = RoomRiftSpace.chaoticRiftSpace(new Vec3i(0,0,0), new Vec3i(width,height,depth));
                for (var otherCorridor:baseSpace.corridors()) {
                    if (otherCorridor.direction()==corrider.direction().getOpposite()){
                        var position = corrider.position().offset(origin);
                        var delta = position.relative(corrider.direction(), 1).subtract(otherCorridor.position().offset(baseSpace.origin()));
                        var offsetedSpace = baseSpace.offset(delta.getX(), delta.getY(), delta.getZ());
                        if(canPlaceSpace(offsetedSpace))nextSpaces.add(offsetedSpace);
                    }
                }
            }
            return nextSpaces;
        }

        public RiftSpace getSpaceAt(Vec3i position){
            if(chaosiveness(new Vector2d(position.getX(), position.getZ()))< Math.abs(position.getY())) return VOID_SPACE;
            if (isOutsideThisRegion(position.getX(), position.getY(), position.getZ())){
                return VOID_SPACE;
            }
            return spaces[(position.getX()-origin.getX())+(position.getZ()-origin.getZ())*15+(position.getY()-origin.getY())*15*15];
        }

        public void setSpaceAt(Vec3i position, RiftSpace space){
            if (isOutsideThisRegion(position.getX(), position.getY(), position.getZ())){
                return;
            }
            spaces[(position.getX()-origin.getX())+(position.getZ()-origin.getZ())*15+(position.getY()-origin.getY())*15*15]=space;
        }


        private boolean isOutsideThisRegion(int x, int y, int z){
            return x<origin.getX() || x>=origin.getX()+15 ||
                    y<origin.getY() || y>=origin.getY()+ layerCount ||
                    z<origin.getZ() || z>=origin.getZ()+15;
        }

        private boolean canPlaceSpace(RiftSpace space){
            for (int x = 0; x < space.size().getX(); x++) {
                for (int y = 0; y < space.size().getY(); y++) {
                    for (int z = 0; z < space.size().getZ(); z++) {
                        var position = space.origin().offset(x,y,z);
                        if(getSpaceAt(position)!=null)return false;
                    }
                }
            }
            return true;
        }


        private boolean tryPlaceSpace(RiftSpace space){
            if(!canPlaceSpace(space)) {
                return false;
            }
            for (int x = 0; x < space.size().getX(); x++) {
                for (int y = 0; y < space.size().getY(); y++) {
                    for (int z = 0; z < space.size().getZ(); z++) {
                        var position = space.origin().offset(x,y,z);
                        setSpaceAt(position,space);
                    }
                }
            }
            return true;
        }

        public void tryGenerate(RandomSource random) {
            if(generatorThread.get()==null && random!=null){
                if(generatorThread.compareAndSet(null,Thread.currentThread())){
                    generate(random);
                }
            }
            try {
                generationCompletion.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
