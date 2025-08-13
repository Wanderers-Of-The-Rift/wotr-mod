package com.wanderersoftherift.wotr.mixin;

import com.mojang.authlib.GameProfile;
import com.wanderersoftherift.wotr.entity.player.PlayerAttributeChangedEvent;
import com.wanderersoftherift.wotr.modifier.ModifierHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player {

    private MixinServerPlayer(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(method = "setGameMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;runLocationChangedEffects(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void onSetGameModeRunLocationChangedEffects(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ModifierHelper.enableModifier(player);
    }

    @Inject(method = "setGameMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;stopLocationBasedEffects(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void onSetGameModeStopLocationBasedEffects(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ModifierHelper.disableModifier(player);
    }

    @Override
    protected void onAttributeUpdated(@NotNull Holder<Attribute> attribute) {
        super.onAttributeUpdated(attribute);
        NeoForge.EVENT_BUS.post(new PlayerAttributeChangedEvent(this, attribute));
    }
}
