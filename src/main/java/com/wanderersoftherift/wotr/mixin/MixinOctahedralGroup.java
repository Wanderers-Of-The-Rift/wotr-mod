package com.wanderersoftherift.wotr.mixin;

import com.google.common.collect.Maps;
import com.mojang.math.OctahedralGroup;
import com.mojang.math.SymmetricGroup3;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;


/*copied from VaultFaster*/
@Mixin(OctahedralGroup.class)
public abstract class MixinOctahedralGroup {

    @Shadow @Nullable private Map<Direction, Direction> rotatedDirections;

    @Shadow @Final private SymmetricGroup3 permutation;

    @Shadow public abstract boolean inverts(Direction.Axis p_56527_);

    @Inject(method = "rotate(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/Direction;",at=@At("HEAD"),remap = true,cancellable = true)
    private void fixRotateMultithreaded(Direction p_56529_, CallbackInfoReturnable<Direction> cir){
        var privateRotatedDirections = this.rotatedDirections;
        if (privateRotatedDirections == null) {
            privateRotatedDirections = Maps.newEnumMap(Direction.class);

            var axes = Direction.Axis.values();
            for(Direction direction : Direction.values()) {
                Direction.Axis direction$axis = direction.getAxis();
                Direction.AxisDirection direction$axisdirection = direction.getAxisDirection();
                Direction.Axis direction$axis1 = axes[this.permutation.permutation(direction$axis.ordinal())];
                Direction.AxisDirection direction$axisdirection1 = this.inverts(direction$axis1) ? direction$axisdirection.opposite() : direction$axisdirection;
                Direction direction1 = Direction.fromAxisAndDirection(direction$axis1, direction$axisdirection1);
                privateRotatedDirections.put(direction, direction1);
            }
        }

        cir.setReturnValue((this.rotatedDirections = privateRotatedDirections).get(p_56529_));

    }
}
