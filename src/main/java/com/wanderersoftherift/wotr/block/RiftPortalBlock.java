package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.core.rift.RiftLevel;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftPortalBlock extends Block {
    public RiftPortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level,BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (level instanceof RiftLevel riftLevel) {
                if (player instanceof ServerPlayer serverPlayer) {

                    ResourceKey<Level> respawnKey = riftLevel.getPortalDimension();
                    if (respawnKey == level.dimension()) {
                        respawnKey = Level.OVERWORLD;
                    }
                    ServerLevel respawnDimension = level.getServer().getLevel(respawnKey);
                    if (respawnDimension == null) {
                        respawnDimension = level.getServer().overworld();
                    }
                    var respawnPos = riftLevel.getPortalPos().above();
                    player.teleportTo(respawnDimension, respawnPos.getCenter().x(), respawnPos.getY(), respawnPos.getCenter().z(), Set.of(), serverPlayer.getRespawnAngle(), 0, true);
                    riftLevel.removePlayer(player.getUUID());
                    RiftLevelManager.unregisterAndDeleteLevel(riftLevel);
                } else {
                    player.displayClientMessage(Component.literal("Failed to create rift"), true);
                }

                return InteractionResult.SUCCESS;
            }
            if (level.dimension().location().getNamespace().equals("wotr")) {
                if (player instanceof ServerPlayer serverPlayer) {
                    player.displayClientMessage(Component.literal("You're in inactive rift - teleporting to spawn"), true);

                    ServerLevel respawnDimension = level.getServer().overworld();
                    var respawnPos = respawnDimension.getSharedSpawnPos();
                    player.teleportTo(respawnDimension, respawnPos.getCenter().x(), respawnPos.getY(), respawnPos.getCenter().z(), Set.of(),
                        serverPlayer.getRespawnAngle(), 0, true);
                }
                return InteractionResult.SUCCESS;
            }
            RiftLevel lvl = RiftLevelManager.createRiftLevel(level.dimension(), pos);
            if (lvl == null) {
                player.displayClientMessage(Component.literal("Failed to create rift"), true);
                return InteractionResult.FAIL;
            }
            lvl.addPlayer(player.getUUID());
            player.displayClientMessage(Component.literal("Created rift with id: " + lvl.getId()), true);
            player.teleportTo(lvl, 0.5, 0, 0.5, Set.of(), player.getYRot(), player.getXRot(), false);
            NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerChangedDimensionEvent(player, level.dimension(), lvl.dimension()));

        }
        return InteractionResult.SUCCESS;
    }
}
