package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.entity.mob.VariedRiftMob;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Inject into the summon command to apply variant stats when a mob is /summoned
@Mixin(SummonCommand.class)
public class MixinSummonCommand {
    @Inject(method = "createEntity", at = @At("RETURN"), cancellable = true)
    private static void onSummon(
            CommandSourceStack source,
            Holder.Reference<?> type,
            Vec3 pos,
            CompoundTag tag,
            boolean randomizeProperties,
            CallbackInfoReturnable<Entity> cir) {
        Entity entity = cir.getReturnValue();
        if (entity instanceof VariedRiftMob mvi) {
            mvi.applyVariantStats();
        }
    }
}