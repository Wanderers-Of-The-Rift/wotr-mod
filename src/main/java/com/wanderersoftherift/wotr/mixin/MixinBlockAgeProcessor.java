package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockAgeProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(BlockAgeProcessor.class)
public abstract class MixinBlockAgeProcessor implements RiftTemplateProcessor {
    @Shadow @Nullable protected abstract BlockState maybeReplaceStairs(RandomSource random, BlockState state);

    @Shadow @Nullable protected abstract BlockState maybeReplaceSlab(RandomSource random);

    @Shadow @Nullable protected abstract BlockState maybeReplaceWall(RandomSource random);

    @Shadow @Nullable protected abstract BlockState maybeReplaceObsidian(RandomSource random);

    @Shadow @Nullable protected abstract BlockState maybeReplaceFullStoneBlock(RandomSource random);

    @Override
    public BlockState processBlockState(BlockState currentState, int x, int y, int z, ServerLevelAccessor world, BlockPos structurePos, CompoundTag nbt, boolean isVisible) {
        BlockPos blockpos = new BlockPos(x,y,z);
        RandomSource randomsource = ProcessorUtil.getRandom(StructureRandomType.PIECE,null, blockpos,null, world,0xab46158bL);
        BlockState blockstate1 = null;
        if (!currentState.is(Blocks.STONE_BRICKS) && !currentState.is(Blocks.STONE) && !currentState.is(Blocks.CHISELED_STONE_BRICKS)) {
            if (currentState.is(BlockTags.STAIRS)) {
                blockstate1 = this.maybeReplaceStairs(randomsource, currentState);
            } else if (currentState.is(BlockTags.SLABS)) {
                blockstate1 = this.maybeReplaceSlab(randomsource);
            } else if (currentState.is(BlockTags.WALLS)) {
                blockstate1 = this.maybeReplaceWall(randomsource);
            } else if (currentState.is(Blocks.OBSIDIAN)) {
                blockstate1 = this.maybeReplaceObsidian(randomsource);
            }
        } else {
            blockstate1 = this.maybeReplaceFullStoneBlock(randomsource);
        }

        return blockstate1 != null ? blockstate1 : currentState;
    }
}
