package com.wanderersoftherift.wotr.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import org.spongepowered.asm.mixin.Mixin;

//todo also port other vanilla processors

@Mixin(JigsawReplacementProcessor.class)
public class MixinJigsawReplacementProcessor implements RiftTemplateProcessor {
    @Override
    public BlockState processBlockState(BlockState currentState, int x, int y, int z, ServerLevel world, BlockPos structurePos, CompoundTag nbt, boolean isVisible) {

        if (currentState.is(Blocks.JIGSAW)) {
            if (nbt == null) {
                return currentState;
            } else {
                String s = nbt.getString("final_state");

                BlockState blockstate1;
                try {
                    BlockStateParser.BlockResult blockstateparser$blockresult = BlockStateParser.parseForBlock(world.holderLookup(Registries.BLOCK), s, true);
                    blockstate1 = blockstateparser$blockresult.blockState();
                } catch (CommandSyntaxException commandsyntaxexception) {
                    return null;
                }


                var added = nbt.getAllKeys().toArray();
                for (var key:added) {
                    nbt.remove((String) key);
                }
                return blockstate1.is(Blocks.STRUCTURE_VOID) ? null : blockstate1;
            }
        } else {
            return currentState;
        }
    }
}
