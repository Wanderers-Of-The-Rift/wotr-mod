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

/* copied from VaultFaster
 *
 * fixes a bug in mojank code that causes multithreading crash
 * original code loaded empty map to rotatedDirections and then populate it
 * this would cause a crash where if two threads tried to access it simoultaneously, one could read the map before it was fully populated.
 * this modified code fixes this issue by loading empty map to a local variable, then populating it and then moving it to rotatedDirections so that rotatedDirections never has a nonnull map that's not fully populated
 */
@Mixin(OctahedralGroup.class)
public abstract class MixinOctahedralGroup {

    @Shadow
    @Nullable private Map<Direction, Direction> rotatedDirections;

    @Shadow
    @Final
    private SymmetricGroup3 permutation;

    @Shadow
    public abstract boolean inverts(Direction.Axis axis);

    @Inject(method = "rotate(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/Direction;", at = @At("HEAD"), cancellable = true)
    private void fixRotateMultithreaded(Direction baseDirection, CallbackInfoReturnable<Direction> cir) {
        var privateRotatedDirections = this.rotatedDirections;
        if (privateRotatedDirections == null) {
            privateRotatedDirections = Maps.newEnumMap(Direction.class);

            var axes = Direction.Axis.values();
            for (Direction direction : Direction.values()) {
                Direction.Axis axis = direction.getAxis();
                Direction.AxisDirection axisDirection = direction.getAxisDirection();
                Direction.Axis otherAxis = axes[this.permutation.permutation(axis.ordinal())];
                Direction.AxisDirection otherAxisDirection;
                if (this.inverts(otherAxis)) {
                    otherAxisDirection = axisDirection.opposite();
                } else {
                    otherAxisDirection = axisDirection;
                }
                Direction direction1 = Direction.fromAxisAndDirection(otherAxis, otherAxisDirection);
                privateRotatedDirections.put(direction, direction1);
            }
        }

        cir.setReturnValue((this.rotatedDirections = privateRotatedDirections).get(baseDirection));

    }
}
