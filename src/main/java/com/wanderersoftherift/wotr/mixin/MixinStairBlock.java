package com.wanderersoftherift.wotr.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
* Fix stair block mirroring since MOJANK apparently can't
* */

@Mixin(StairBlock.class)
public class MixinStairBlock {

    @SuppressWarnings("StaticVariableName") @Shadow @Final public static EnumProperty<Direction> FACING;

    @SuppressWarnings("StaticVariableName") @Shadow @Final public static EnumProperty<StairsShape> SHAPE;

    @Inject(method = "mirror",at = @At("HEAD"),cancellable = true)
    private void fixMirror(BlockState state, Mirror mirror, CallbackInfoReturnable<BlockState> cir){

        Direction direction = state.getValue(FACING);
        StairsShape stairsshape = state.getValue(SHAPE);
        switch (mirror) {
            case FRONT_BACK:
                if (direction.getAxis() == Direction.Axis.Z) {
                    switch (stairsshape) {
                        case INNER_LEFT -> {
                            cir.setReturnValue(state.setValue(SHAPE, StairsShape.INNER_RIGHT));
                        }
                        case INNER_RIGHT -> {
                            cir.setReturnValue(state.setValue(SHAPE, StairsShape.INNER_LEFT));
                        }
                        case OUTER_LEFT -> {
                            cir.setReturnValue(state.setValue(SHAPE, StairsShape.OUTER_RIGHT));
                        }
                        case OUTER_RIGHT -> {
                            cir.setReturnValue(state.setValue(SHAPE, StairsShape.OUTER_LEFT));
                        }
                    }
                }
                break;
            case LEFT_RIGHT:
                if (direction.getAxis() == Direction.Axis.X){
                    switch (stairsshape) {
                        case INNER_LEFT -> {
                            cir.setReturnValue(state.setValue(SHAPE, StairsShape.INNER_RIGHT));
                        }
                        case INNER_RIGHT -> {
                            cir.setReturnValue(state.setValue(SHAPE, StairsShape.INNER_LEFT));
                        }
                        case OUTER_LEFT -> {
                            cir.setReturnValue(state.setValue(SHAPE, StairsShape.OUTER_RIGHT));
                        }
                        case OUTER_RIGHT -> {
                            cir.setReturnValue(state.setValue(SHAPE, StairsShape.OUTER_LEFT));
                        }
                    }
                }
        }
    }
}
