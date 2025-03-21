package com.wanderersoftherift.wotr.item.riftkey;

import com.wanderersoftherift.wotr.block.RiftSpawnerBlock;
import com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation;
import com.wanderersoftherift.wotr.entity.portal.RiftPortalEntity;
import com.wanderersoftherift.wotr.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * Rift key is an item that when used on a rift spawner will generate a rift portal. It also can close an existing rift without being consumed.
 */
public class RiftKey extends Item {
    public RiftKey(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (!(blockstate.getBlock() instanceof RiftSpawnerBlock spawnerBlock)) {
            return InteractionResult.PASS;
        } else if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            Optional<PortalSpawnLocation> spawnLocation = spawnerBlock.getSpawnLocation(level, blockpos, context.getClickedFace());
            if (spawnLocation.isPresent()) {
                PortalSpawnLocation loc = spawnLocation.get();
                List<RiftPortalEntity> existingRifts = getExistingRifts(level, loc.position());
                if (!existingRifts.isEmpty()) {
                    for (RiftPortalEntity entrance : existingRifts) {
                        entrance.remove(Entity.RemovalReason.DISCARDED);
                    }
                    return InteractionResult.SUCCESS;
                }

                spawnRift(level, loc.position(), loc.direction());
                context.getItemInHand().shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private List<RiftPortalEntity> getExistingRifts(Level level, Vec3 pos) {
        return level.getEntities(EntityTypeTest.forClass(RiftPortalEntity.class), new AABB(BlockPos.containing(pos)), x -> true);
    }

    private void spawnRift(Level level, Vec3 pos, Direction dir) {
        RiftPortalEntity rift = new RiftPortalEntity(ModEntityTypes.RIFT_ENTRANCE.get(), level);
        rift.setPos(pos);
        rift.setYRot(dir.toYRot());
        rift.setBillboard(dir.getAxis().isVertical());
        level.addFreshEntity(rift);
    }
}
