package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.world.level.TemporaryLevel;
import com.wanderersoftherift.wotr.world.level.TemporaryLevelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RiftPortalBlock extends Block {
    public RiftPortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull
    Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (level instanceof TemporaryLevel temporaryLevel) {
                if (player instanceof ServerPlayer serverPlayer){
                    var respawnPos = serverPlayer.getRespawnPosition();
                    if (respawnPos == null){
                        respawnPos = level.getSharedSpawnPos();
                    }
                    TemporaryLevelManager.unregisterLevel(temporaryLevel);
                    player.teleportTo(level.getServer().overworld(),respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), Set.of(), player.getYRot(), player.getXRot(), false);
                } else {
                    player.displayClientMessage(Component.literal("Failed to create rift"), true);
                }

                return InteractionResult.SUCCESS;
            }
            var lvl = TemporaryLevelManager.createRiftLevel();
            if (lvl == null) {
                player.displayClientMessage(Component.literal("Failed to create rift"), true);
                return InteractionResult.FAIL;
            }
            player.displayClientMessage(Component.literal("Created rift with id: " + lvl.getId()), true);
            player.teleportTo(lvl, 0.5, -63, 0.5, Set.of(), player.getYRot(), player.getXRot(), false);
            NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerChangedDimensionEvent(player, level.dimension(), lvl.dimension()));

            return InteractionResult.SUCCESS;
        }
    }
}
