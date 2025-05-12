package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Collection;

public interface RiftGeneratable {

    void processAndPlace(RiftProcessedRoom destination, ServerLevelAccessor world, Vec3i placementShift, TripleMirror mirror);

    Collection<StructureTemplate.JigsawBlockInfo> jigsaws();

    Vec3i size();

    String identifier();


    public static void generate(RiftGeneratable generatable, RiftProcessedRoom destination, ServerLevelAccessor world, Vec3i placementShift, TripleMirror mirror, MinecraftServer server, RandomSource random){

        for (var jigsaw:generatable.jigsaws()){
            var pool = jigsaw.pool();
            if (isPoolBlacklisted(pool)) {
                continue;
            }
            var next = RiftTemplates.random(server,pool,random);
            if(next==null) {
                continue;
            }

            var simplifiedDirection1 = simplifiedDirection(jigsaw,mirror);
            var simplifiedDirection1Opposite = simplifiedDirection1.getOpposite();

            var jigsaw2List = next.jigsaws().stream().filter(
                    (otherJigsaw)->{
                        var otherSimplifiedDirection = simplifiedDirection(otherJigsaw, TripleMirror.NONE);
                        return otherJigsaw.name().equals(jigsaw.target()) && (simplifiedDirection1Opposite==otherSimplifiedDirection || (simplifiedDirection1.getStepY()==0 && otherSimplifiedDirection.getStepY()==0 /*checks if both directions are horizontal*/));
                    }).toList();

            if(jigsaw2List.isEmpty()) {
                continue;//todo possibly multiple attempts
            }
            var jigsaw2 = jigsaw2List.get(random.nextInt(jigsaw2List.size()));
            var simplifiedDirection2 = simplifiedDirection(jigsaw2,TripleMirror.NONE);
            var nextMirrorInt = random.nextInt(8);
            if(simplifiedDirection1.getAxis() == Direction.Axis.Y) {
                if (simplifiedDirection2!=simplifiedDirection1Opposite){
                    continue;
                }
                if(jigsaw.jointType()== JigsawBlockEntity.JointType.ALIGNED && jigsaw2.jointType()== JigsawBlockEntity.JointType.ALIGNED){
                    nextMirrorInt = mirrorCorrection(auxiliaryDirection(jigsaw,mirror), auxiliaryDirection(jigsaw2,TripleMirror.NONE),nextMirrorInt,false);
                }
            } else {
                nextMirrorInt = mirrorCorrection(simplifiedDirection1,simplifiedDirection2,nextMirrorInt,true);
            }
            var nextMirror = new TripleMirror(nextMirrorInt);
            var newPlacementShift = placementShift.offset(mirror.applyToPosition(jigsaw.info().pos(), generatable.size().getX()-1, generatable.size().getZ()-1)).offset(nextMirror.applyToPosition(jigsaw2.info().pos(),next.size().getX()-1,next.size().getZ()-1).multiply(-1));

            generate(next,destination, world, newPlacementShift.relative(simplifiedDirection1), nextMirror, server, random);
        }
        generatable.processAndPlace(destination, world, placementShift, mirror);
    }


    private static int setOrClearBit(int value, int bit, boolean set){
        value &=~bit;
        if(set){
            value|=bit;
        }
        return value;
    }

    private static int mirrorCorrection(Direction d1, Direction d2,int oldMirrorInt, boolean targetOpposite){
        return setOrClearBit(
                setOrClearBit(oldMirrorInt, 0b100, d1.getAxis()!=d2.getAxis()),
                d2.getAxis()== Direction.Axis.Z?0b010:0b001,
                ((d1.getStepX()+d1.getStepZ()) > 0 == (d2.getStepX()+d2.getStepZ()) > 0) == targetOpposite
        );
    }

    private static Direction simplifiedDirection(StructureTemplate.JigsawBlockInfo jigsaw,TripleMirror mirror) {
        return mirror.applyToBlockState(jigsaw.info().state()).getValue(JigsawBlock.ORIENTATION).front();
    }

    private static Direction auxiliaryDirection(StructureTemplate.JigsawBlockInfo jigsaw, TripleMirror mirror) {
        return mirror.applyToBlockState(jigsaw.info().state()).getValue(JigsawBlock.ORIENTATION).top();
    }

    private static boolean isPoolBlacklisted(ResourceLocation pool) {
        return pool.getPath().contains("rift/ring_") && pool.getNamespace().equals("wotr");
    }

}
