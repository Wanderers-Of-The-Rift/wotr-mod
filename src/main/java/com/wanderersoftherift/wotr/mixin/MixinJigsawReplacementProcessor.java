package com.wanderersoftherift.wotr.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wanderersoftherift.wotr.util.Ref;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import org.spongepowered.asm.mixin.Mixin;

//todo also port other vanilla processors

@Mixin(JigsawReplacementProcessor.class)
public class MixinJigsawReplacementProcessor implements RiftTemplateProcessor {
    @Override
    public BlockState processBlockState(
            BlockState currentState,
            int x,
            int y,
            int z,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Ref<BlockEntity> nbt,
            boolean isVisible) {

        if (currentState.is(Blocks.JIGSAW)) {
            var entity = nbt.getValue();
            if (entity instanceof JigsawBlockEntity jigsaw) {
                String finalState = jigsaw.getFinalState();

                BlockState blockstate1;
                try {
                    BlockStateParser.BlockResult blockResult = BlockStateParser
                            .parseForBlock(world.holderLookup(Registries.BLOCK), finalState, true);
                    blockstate1 = blockResult.blockState();
                } catch (CommandSyntaxException commandsyntaxexception) {
                    return null;
                }

                nbt.setValue(null);

                if (blockstate1.is(Blocks.STRUCTURE_VOID)) {
                    return null;
                } else {
                    return blockstate1;
                }
            }
        }
        return currentState;
    }
}
