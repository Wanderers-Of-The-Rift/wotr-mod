package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpaceCorridor;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftTemplates;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.lang.ref.PhantomReference;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RoomRandomizerImpl implements RoomRandomizer {

    private static Pair<PhantomReference<MinecraftServer>, EnumMap<RoomRiftSpace.RoomType, RiftSpaceHolder>> cache;
    private final MinecraftServer server;

    public RoomRandomizerImpl(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public RoomRiftSpace randomSpace(RoomRiftSpace.RoomType roomType, RandomSource randomSource, Vec3i maximumSize) {
        return getOrCreateSpaceHolder(roomType).random(maximumSize,randomSource);
    }

    private RiftSpaceHolder getOrCreateSpaceHolder(RoomRiftSpace.RoomType roomType){
        var lastCache = cache;
        if(lastCache!=null && lastCache.getA().refersTo(server)){
            return lastCache.getB().get(roomType);
        }
        var map = new EnumMap<RoomRiftSpace.RoomType, RiftSpaceHolder>(RoomRiftSpace.RoomType.class);
        for(var type : RoomRiftSpace.RoomType.values()){
            map.computeIfAbsent(type, this::createSpaceHolder);
        }
        cache = new Pair<>(new PhantomReference<>(server, null), map);
        return map.get(roomType);
    }

    private RiftSpaceHolder createSpaceHolder(RoomRiftSpace.RoomType roomType){
        var templates = RiftTemplates.all(server, WanderersOfTheRift.id("rift/room_"+roomType.toString().toLowerCase()));
        if (roomType == RoomRiftSpace.RoomType.CHAOS){
            return new MultiSizeRiftSpaceRandomList(templates,((generatable, desiredTemplateSize) -> convertRoom(generatable,desiredTemplateSize, roomType)));
        } else {
            return new MonoSizeRiftSpaceRandomList(templates,((generatable, desiredTemplateSize) -> convertRoom(generatable,desiredTemplateSize, roomType)));
        }
    }


    private static Stream<RoomRiftSpace> convertRoom(RiftGeneratable generatable, @Nullable Vec3i desiredTemplateSize, RoomRiftSpace.RoomType type){ //todo maybe double weight if only one diagonal mirror is applicable
        var sizeBlocks = generatable.size();
        var sizeChunks = new Vec3i((sizeBlocks.getX() + 1)/16, (sizeBlocks.getY() + 1)/16, (sizeBlocks.getZ() + 1)/16);
        var baseStream = IntStream.range(0, 8).mapToObj((mirrorPermutation) -> {
            var mirror = new TripleMirror(mirrorPermutation);
            var modifiedSize = new TripleMirror(false, false, mirror.diagonal()).applyToPosition(sizeChunks, 0, 0);
            return new RoomRiftSpace(modifiedSize,
                    new Vec3i(modifiedSize.getX()/2, modifiedSize.getY()/2, modifiedSize.getZ()/2),
                    computeCorridors(generatable.jigsaws(), mirror, sizeChunks),
                    type, generatable, mirror
            );
        });
        if(desiredTemplateSize!=null) {
            return baseStream.filter((it) ->
                    desiredTemplateSize.getX() >= it.size().getX() &&
                    desiredTemplateSize.getY() >= it.size().getY() &&
                    desiredTemplateSize.getZ() >= it.size().getZ()
            );
        } else {
            return baseStream;
        }
    }

    private static List<RiftSpaceCorridor> computeCorridors(Collection<StructureTemplate.JigsawBlockInfo> jigsaws, TripleMirror mirror, Vec3i sizeChunks) {
        return jigsaws.stream()
                .filter((it)->it.pool().toString().contains("wotr:rift/ring"))
                .map(jigsaw->new RiftSpaceCorridor(
                        mirror.applyToPosition(
                                new Vec3i(jigsaw.info().pos().getX()/16, jigsaw.info().pos().getY()/16, jigsaw.info().pos().getZ()/16),
                                sizeChunks.getX() - 1, sizeChunks.getZ() - 1
                        ),
                        mirror.applyToDirection(JigsawBlock.getFrontFacing(jigsaw.info().state())))
                ).toList();
    }

    private interface RoomConverter {
        Stream<RoomRiftSpace> convertRoom(RiftGeneratable generatable,  @Nullable Vec3i desiredTemplateSize);
    }

    private interface RiftSpaceHolder {
        RoomRiftSpace random(Vec3i maxSize, RandomSource random);
    }

    private static class MultiSizeRiftSpaceRandomList implements RiftSpaceHolder {
        private final FastWeightedList<RoomRiftSpace>[] weightedListForSize = new FastWeightedList[64];

        public MultiSizeRiftSpaceRandomList(List<RiftGeneratable> templates, RoomConverter converter) {
            for (int i = 0; i < 64; i++) {
                var desiredTemplateSize = new Vec3i((i & 0b11) + 1, ((i >> 2) & 0b11) + 1, ((i >> 4) & 0b11) + 1);
                weightedListForSize[i]= FastWeightedList.byCountingDuplicates(templates.stream().flatMap(template -> converter.convertRoom(template, desiredTemplateSize)).toList(), space -> new Pair(space.template().identifier(),space.templateTransform()));
            }
        }

        public RoomRiftSpace random(Vec3i maxSize, RandomSource random){
            var i = (maxSize.getX() - 1) + 4 * (maxSize.getY() - 1) + 16 * (maxSize.getZ() - 1);
            return weightedListForSize[i].random(random);
        }
    }

    private static class MonoSizeRiftSpaceRandomList implements RiftSpaceHolder {
        private final FastWeightedList<RoomRiftSpace> weightedList;

        public MonoSizeRiftSpaceRandomList(List<RiftGeneratable> templates, RoomConverter converter) {
            weightedList = FastWeightedList.byCountingDuplicates(templates.stream().flatMap(template -> converter.convertRoom(template,null)).toList(),space -> new Pair(space.template().identifier(),space.templateTransform()));
        }

        public RoomRiftSpace random(Vec3i maxSize, RandomSource random){
            return weightedList.random(random);
        }
    }
}
