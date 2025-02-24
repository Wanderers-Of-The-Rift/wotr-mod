package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel serverLevel) {
            if (RiftData.isRift(serverLevel)) {
                return tpHome(serverPlayer, serverLevel);
            }
            return tpToRift(serverPlayer, serverLevel, pos);
        }
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult tpToRift(ServerPlayer player, ServerLevel level, BlockPos pos) {
        ResourceLocation riftId = WanderersOfTheRift.id("rift_" + pos.getX() + "_" + pos.getY() + "_" + pos.getZ());
        ServerLevel lvl = RiftLevelManager.getOrCreateRiftLevel(riftId, level.dimension(), pos);
        if (lvl == null) {
            player.displayClientMessage(Component.literal("Failed to create rift"), true);
            return InteractionResult.FAIL;
        }
        RiftData.get(lvl).addPlayer(player.getUUID());
        player.displayClientMessage(Component.literal("Created rift with id: " + lvl.dimension().location()), true);
        player.teleportTo(lvl, 0.5, 0, 0.5, Set.of(), player.getYRot(), player.getXRot(), false);
        NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerChangedDimensionEvent(player, level.dimension(), lvl.dimension()));
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult tpHome(ServerPlayer serverPlayer, ServerLevel riftLevel) {
            ResourceKey<Level> respawnKey = RiftData.get(riftLevel).getPortalDimension();
            if (respawnKey == riftLevel.dimension()) {
                respawnKey = Level.OVERWORLD;
            }
            ServerLevel respawnDimension = riftLevel.getServer().getLevel(respawnKey);
            if (respawnDimension == null) {
                respawnDimension = riftLevel.getServer().overworld();
            }
            var respawnPos = RiftData.get(riftLevel).getPortalPos().above();
            serverPlayer.teleportTo(respawnDimension, respawnPos.getCenter().x(), respawnPos.getY(), respawnPos.getCenter().z(), Set.of(), serverPlayer.getRespawnAngle(), 0, true);
            RiftData.get(riftLevel).removePlayer(serverPlayer.getUUID());
            RiftLevelManager.unregisterAndDeleteLevel(riftLevel);
            return InteractionResult.SUCCESS;
    }
}
