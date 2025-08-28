package com.wanderersoftherift.wotr.entity.portal;

import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static com.wanderersoftherift.wotr.core.rift.RiftLevelManager.levelExists;

/**
 * This entity provides the entrance into a rift.
 */
public class RiftPortalEntranceEntity extends RiftPortalEntity {

    public RiftPortalEntranceEntity(EntityType<? extends RiftPortalEntranceEntity> entityType, Level level) {
        super(entityType, level);
        blocksBuilding = true;
    }

    private RiftEntranceAttachment attachment() {
        return getData(WotrAttachments.RIFT_ENTRANCE);
    }

    public ResourceKey<Level> getRiftDimensionId() {
        return attachment().target();
    }

    public boolean isGenerated() {
        return attachment().generated();
    }

    @Override
    public void tick() {
        if (isGenerated()) {
            if (!levelExists(getRiftDimensionId())) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }
        }
        super.tick();
    }

    @Override
    protected void onPlayerInPortal(ServerPlayer player, ServerLevel level) {
        attachment().teleportPlayer(player, level, blockPosition());
    }

    public ItemStack getKeyItem() {
        return attachment().keyItem();
    }

    public void setKeyItem(ItemStack riftKey) {
        attachment().keyItem(riftKey);
    }
}
