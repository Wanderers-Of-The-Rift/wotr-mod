package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.levelgen.RoomRandomizer;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpaceCorridor;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.level.levelgen.RandomState;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class ChaoticRiftLayout implements RiftLayout{

    private final ConcurrentHashMap<Vector2i, Region> regions = new ConcurrentHashMap<>();
    private final int layerCount;
    private final RoomRandomizer roomRandomizer;

    public ChaoticRiftLayout(int layerCount, RoomRandomizer roomRandomizer) {
        this.layerCount = layerCount;
        this.roomRandomizer = roomRandomizer;
    }

    private Region getOrCreateRegion(int x, int z){
        var regionX = Math.floorDiv(x+7,15);
        var regionZ = Math.floorDiv(z+7,15);

        return regions.computeIfAbsent(new Vector2i(regionX,regionZ),(unused)->new Region(new Vec3i(regionX*15-7,-layerCount /2,regionZ*15-7)));
    }

    @Override
    public RiftSpace getChunkSpace(Vec3i chunkPos, RandomState randomState) {
        var region = getOrCreateRegion(chunkPos.getX(), chunkPos.getZ());
        if(randomState!=null)region.tryGenerate(randomState.getOrCreateRandomFactory(WanderersOfTheRift.id("rift_layout")).at(region.origin.getX(),0,region.origin.getZ()));
        return region.getSpaceAt(chunkPos);
    }

    private class Region {

        private static final RiftSpace VOID_SPACE = new VoidRiftSpace();
        private static final IntList MASKS;

        private final RiftSpace[] spaces = new RiftSpace[15*15* layerCount];
        private final long[] emptySpaces = new long[15*15];

        public final Vec3i origin;
        private final AtomicReference<Thread> generatorThread = new AtomicReference<>(null);
        private final CompletableFuture<Unit> generationCompletion = new CompletableFuture<>();

        public Region(Vec3i origin) {
            this.origin = origin;
            for (int x = 0; x < 15; x++) {
                for (int z = 0; z < 15; z++) {
                    var idx = z*15+x;
                    var c = (int)Double.min(chaosiveness(new Vector2d(x+origin.getX(), z+origin.getZ())),layerCount/2.0);
                    emptySpaces[idx] = ((1L<<(2*c))-1)<<(layerCount/2-c);
                }

            }
            //Arrays.fill(emptySpaces, (1L << layerCount) - 1);
        }

        public void generate(RandomSource randomSource){
            var currentSpaces = Collections.<RiftSpace>emptyList();
            var nextSpaces = generateNonChaotic(randomSource);
            while (!nextSpaces.isEmpty()){
                currentSpaces=nextSpaces;
                nextSpaces=new ArrayList<>();
                for (var space:currentSpaces){

                    for (var corridor:space.corridors()){
                        var nextSpace = nextChaoticSpace(corridor,randomSource,space.origin());
                        if(nextSpace!=null /*&& tryPlaceSpace(nextSpace)*/) {
                            placeSpace(nextSpace);
                            nextSpaces.add(nextSpace);
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
                    var roomOrigin = new Vector2i(x*3+origin.getX(),z*3+origin.getZ());
                    var category = categorize(new Vector2d(roomOrigin.x+1.5,roomOrigin.y+1.5));
                    if(category==0){

                        var room = roomRandomizer.randomSpace((roomOrigin.x()==-1 && roomOrigin.y() == -1)? RoomRiftSpace.RoomType.PORTAL: RoomRiftSpace.RoomType.STABLE, random, new Vec3i(3,3,3));
                        room=room.offset(roomOrigin.x,-room.corridors().getFirst().position().getY(),roomOrigin.y);
                        if(tryPlaceSpace(room))rooms.add(room);
                    }else if (category == 1){
                        var room = roomRandomizer.randomSpace(RoomRiftSpace.RoomType.UNSTABLE, random, new Vec3i(3,3,3));
                        room=room.offset(roomOrigin.x,-room.corridors().getFirst().position().getY(),roomOrigin.y);
                        if(tryPlaceSpace(room))rooms.add(room);
                    }
                }
            }
            if (rooms.isEmpty()){
                rooms.add(RoomRiftSpace.chaoticRiftSpace(new Vec3i(origin.getX()+1, 0, origin.getZ()+1),new Vec3i(1,1,1)));
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

        private RiftSpace nextChaoticSpace(RiftSpaceCorridor corridor, RandomSource randomSource, Vec3i roomPosition){
            var slices = new int[]{
                    sliceBitmap(corridor,0,roomPosition),
                    sliceBitmap(corridor,1,roomPosition),
                    sliceBitmap(corridor,2,roomPosition)
            };
            if((slices[0]&0b00000_00000_00100_00000_00000) == 0) return null;//corridor is blocked so no room can be placed here
            for (int combination = 16+randomSource.nextInt(240); combination >= 0; combination--) {
                var mask = MASKS.getInt(combination);
                if(mask<0)continue;
                int depth = 0;
                for (; depth < 3 && ((slices[depth] & mask) == mask); depth++) {}
                if(depth!=0) {

                    var position = combination & 0b1111;
                    var size = combination >> 4;
                    var x = (position&0b11)-2;
                    var y = (position>>2)-2;
                    var width = ((size&0b1) | ((size&0b100)>>1))+1;
                    var height = (((size&0b10)>>1) | ((size&0b1000)>>2))+1;

                    var tangentDirection = corridor.direction().getClockWise();
                    var roomSize = new Vec3i(depth*Math.abs(corridor.direction().getStepX())+width*Math.abs(tangentDirection.getStepX()), height, depth*Math.abs(corridor.direction().getStepZ())+width*Math.abs(tangentDirection.getStepZ()));
                    var space = roomRandomizer.randomSpace(RoomRiftSpace.RoomType.CHAOS, randomSource, roomSize);
                    var spaceOffsetX = roomPosition.getX()+corridor.position().getX();
                    var spaceOffsetY = roomPosition.getY()+corridor.position().getY();
                    var spaceOffsetZ = roomPosition.getZ()+corridor.position().getZ();
                    if(tangentDirection.getStepX()>0){
                        spaceOffsetX += x;
                    }else if (tangentDirection.getStepX()<0){
                        spaceOffsetX -= width-1+x;
                    }
                    spaceOffsetY += y;
                    if(tangentDirection.getStepZ()>0){
                        spaceOffsetZ += x;
                    }else if (tangentDirection.getStepZ()<0){
                        spaceOffsetZ -= x+width-1;
                    }
                    if(corridor.direction().getStepX()>0){
                        spaceOffsetX+=1;
                    }else if (corridor.direction().getStepX()<0){
                        spaceOffsetX-=roomSize.getX();
                    }
                    if(corridor.direction().getStepZ()>0){
                        spaceOffsetZ+=1;
                    }else if (corridor.direction().getStepZ()<0){
                        spaceOffsetZ-=roomSize.getZ();
                    }
                    var result = space.offset(spaceOffsetX,spaceOffsetY,spaceOffsetZ);
                    return result;
                }
            }
            return null;
        }

        private int sliceBitmap(RiftSpaceCorridor corridor, int offset, Vec3i roomPosition){
            var result = 0;
            var tangentDirection = corridor.direction().getClockWise();
            var corridorX = corridor.position().getX()+roomPosition.getX();
            var sliceStartY = corridor.position().getY()+roomPosition.getY()-2-origin.getY();
            var corridorZ = corridor.position().getZ()+roomPosition.getZ();
            var z = 1+offset;
            for (int x = -2; x <= 2; x++) {
                var positionX = corridorX+corridor.direction().getStepX()*z+tangentDirection.getStepX()*x;
                var positionZ = corridorZ+corridor.direction().getStepZ()*z+tangentDirection.getStepZ()*x;
                if (positionX<origin.getX() || positionX>=origin.getX()+15 ||
                        positionZ<origin.getZ() || positionZ>=origin.getZ()+15){
                    continue;
                }
                positionX -= origin.getX();
                positionZ -= origin.getZ();
                var emptySpacesColumn = emptySpaces[positionX + positionZ*15];
                var shiftedColumn = sliceStartY<0?(emptySpacesColumn<<-sliceStartY):(emptySpacesColumn>>sliceStartY);
                result |= (int) ((0b11111&shiftedColumn)<<((x+2)*5));
            }
            return result;
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
            emptySpaces[(position.getX()-origin.getX())+(position.getZ()-origin.getZ())*15] &= ~(1L<<(position.getY()-origin.getY()));
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

        private void placeSpace(RiftSpace space){
            for (int x = 0; x < space.size().getX(); x++) {
                for (int y = 0; y < space.size().getY(); y++) {
                    for (int z = 0; z < space.size().getZ(); z++) {
                        var position = space.origin().offset(x,y,z);
                        setSpaceAt(position,space);
                    }
                }
            }
        }

        private boolean tryPlaceSpace(RiftSpace space){
            if(!canPlaceSpace(space)) { return false; }
            placeSpace(space);
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

        static {
            var protoMasks = new int[256];
            for (int size = 0; size < 16; size++) {
                var width = size&0b11;
                var height = size>>2;
                var baseMask = 1;
                for (int i = 0; i < height; i++) {
                    baseMask |= (baseMask<<1);
                }
                for (int i = 0; i < width; i++) {
                    baseMask |= (baseMask<<5);
                }

                for (int position = 0; position < 16; position++) {
                    var x = position&0b11;
                    var y = position>>2;
                    var index = position | (((width & 1) | ((height&1)<<1) | ((width & 2)<<1) | ((height&2)<<2))<<4);
                    protoMasks[index] = baseMask<<(y+5*x);
                    if((protoMasks[index]&0b00000_00000_00100_00000_00000)==0 || x==3 || y==3 || width==3 || height==3)protoMasks[index]=-1;
                }
            }
            MASKS = IntImmutableList.of(protoMasks);
        }
    }

}
