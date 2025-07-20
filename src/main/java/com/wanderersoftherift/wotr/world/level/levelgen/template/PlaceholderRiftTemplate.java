package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.util.Ref;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.ThemeProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.ThemePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Collection;
import java.util.List;

@Deprecated
public class PlaceholderRiftTemplate implements RiftGeneratable {
    private static final BlockState blockState = WotrBlocks.PROCESSOR_BLOCK_1.getBlock().get().defaultBlockState();
    private final Vec3i size;

    public PlaceholderRiftTemplate(Vec3i size) {
        this.size = size;
    }

    @Override
    public MapCodec<? extends RiftGeneratable> codec() {
        return null;
    }

    @Override
    public void processAndPlace(
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            Vec3i placementShift,
            TripleMirror mirror) {
        var themeProcessor = new ThemeProcessor(ThemePieceType.ROOM);
        var offset = new BlockPos(destination.space.origin().multiply(16)).offset(placementShift);
        var mutablePosition = new BlockPos.MutableBlockPos();
        var xLastChunkPosition = 0;
        var yLastChunkPosition = 0;
        var zLastChunkPosition = 0;
        RiftProcessedChunk roomChunk = null;
        for (int x = 0; x < size.getX(); x++) {
            for (int z = 0; z < size.getZ(); z++) {
                for (int y = 0; y < size.getY(); y++) {
                    var xm = x % 16;
                    var ym = y % 16;
                    var zm = z % 16;
                    var f1 = xm >= 6 && xm < 9;
                    var f2 = ym >= 6 && ym < 9;
                    var f3 = zm >= 6 && zm < 9;
                    if ((f1 && f2) || (f2 && f3) || (f3 && f1)) {
                        continue;
                    }
                    var blockPos = new Vec3i(x, y, z);

                    mutablePosition.set(offset)
                            .move(mirror.applyToPosition(blockPos, size.getX() - 1, size.getZ() - 1));

                    var nbt = new CompoundTag();
                    var info = new StructureTemplate.StructureBlockInfo(mutablePosition, blockState, nbt);
                    var entity = new Ref<BlockEntity>(null);
                    var newBlockState = themeProcessor.processBlockState(blockState, mutablePosition.getX(),
                            mutablePosition.getY(), mutablePosition.getZ(), world, offset, entity, true);
                    if (newBlockState == null) {
                        continue;
                    }

                    var finalPos = info.pos();
                    var xChunkPosition = finalPos.getX() >> 4;
                    var yChunkPosition = finalPos.getY() >> 4;
                    var zChunkPosition = finalPos.getZ() >> 4;

                    if (xLastChunkPosition != xChunkPosition || yLastChunkPosition != yChunkPosition
                            || zLastChunkPosition != zChunkPosition || roomChunk == null) {
                        xLastChunkPosition = xChunkPosition;
                        yLastChunkPosition = yChunkPosition;
                        zLastChunkPosition = zChunkPosition;
                        roomChunk = destination
                                .getOrCreateChunk(new Vec3i(xChunkPosition, yChunkPosition, zChunkPosition));
                    }
                    var chunkX = finalPos.getX() & 0xf;
                    var chunkY = finalPos.getY() & 0xf;
                    var chunkZ = finalPos.getZ() & 0xf;
                    roomChunk.blocks[(chunkX) | ((chunkZ) << 4) | ((chunkY) << 8)] = info.state();
                }
            }
        }
    }

    @Override
    public Collection<StructureTemplate.JigsawBlockInfo> jigsaws() {
        return List.of();
    }

    @Override
    public Vec3i size() {
        return size;
    }

    @Override
    public String identifier() {
        return "wotr:builtin:placeholder_" + size.getX() + "x" + size.getY() + "x" + size.getZ();
    }
}
