package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.core.npc.NpcEvent;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
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
        InteractionResult result = cir.getReturnValue();
        if (result == InteractionResult.PASS) {
            NpcIdentity.Attachment identityAttachment = getData(WotrAttachments.NPC_IDENTITY);
            result = identityAttachment.identity().map(identity -> {
                NpcEvent.OnInteract event = new NpcEvent.OnInteract(identity, player, (Mob) (Object) this);
                NeoForge.EVENT_BUS.post(event);
                return event.getResult();
            }).orElse(InteractionResult.PASS);
        }
        if (result == InteractionResult.PASS) {
            result = this.getExistingData(WotrAttachments.NPC_INTERACT)
                    .map(
                            data -> data.interactAsMob((Mob) (LivingEntity) this, player, hand))
                    .orElse(InteractionResult.PASS);
        }
        cir.setReturnValue(result);
    }

}
