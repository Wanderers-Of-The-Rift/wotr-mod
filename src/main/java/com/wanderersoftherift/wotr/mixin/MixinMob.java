package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MixinMob extends LivingEntity {
    protected MixinMob(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "mobInteract", at = @At(value = "RETURN"), cancellable = true)
    protected void wotrMobInteractExtension(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() == InteractionResult.PASS) {
            this.getExistingData(WotrAttachments.NPC_INTERACT)
                    .ifPresent(
                            data -> cir.setReturnValue(data.interactAsMob((Mob) (LivingEntity) this, player, hand)));

        }
    }

}
