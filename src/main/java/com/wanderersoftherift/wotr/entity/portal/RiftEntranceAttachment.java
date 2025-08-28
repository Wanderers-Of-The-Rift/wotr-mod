package com.wanderersoftherift.wotr.entity.portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftEntryState;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public final class RiftEntranceAttachment {
    public static final Codec<RiftEntranceAttachment> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ItemStack.CODEC.fieldOf("key_item").forGetter(RiftEntranceAttachment::keyItem),
                    ResourceKey.codec(Registries.DIMENSION)
                            .fieldOf("target_dimension")
                            .forGetter(RiftEntranceAttachment::target),
                    Codec.BOOL.fieldOf("is_generated").forGetter(RiftEntranceAttachment::generated)
            ).apply(instance, RiftEntranceAttachment::new)
    );
    private ItemStack keyItem;
    private ResourceKey<Level> target;
    private boolean generated;

    public RiftEntranceAttachment(ItemStack keyItem, ResourceKey<Level> target, boolean generated) {
        this.keyItem = keyItem;
        this.target = target;
        this.generated = generated;
    }

    public RiftEntranceAttachment() {
        this(ItemStack.EMPTY,
                ResourceKey.create(Registries.DIMENSION, WanderersOfTheRift.id("rift_" + UUID.randomUUID())), false);
    }

    public ItemStack keyItem() {
        return keyItem;
    }

    public ResourceKey<Level> target() {
        return target;
    }

    public void keyItem(ItemStack keyItem) {
        this.keyItem = keyItem;
    }

    public void target(ResourceKey<Level> target) {
        this.target = target;
    }

    public boolean generated() {
        return generated;
    }

    public void generated(boolean generated) {
        this.generated = generated;
    }

    public void teleportPlayer(ServerPlayer player, ServerLevel level, Vec3i currentPosition) {
        var riftId = target;
        var plDir = player.getDirection().getOpposite();

        ServerLevel lvl = RiftLevelManager.getOrCreateRiftLevel(riftId.location(), level.dimension(),
                currentPosition.offset(plDir.getStepX() * 3, plDir.getStepY() * 3, plDir.getStepZ() * 3), keyItem(),
                player);
        if (lvl == null) {
            player.displayClientMessage(Component.translatable(WanderersOfTheRift.MODID + ".rift.create.failed"), true);
            return;
        }
        generated(true);
        var riftData = RiftData.get(lvl);
        if (riftData.isBannedFromRift(player)) {
            return;
        }

        riftData.addPlayer(player.getUUID());

        RiftEntryState.pushEntryState(player, lvl.dimension());

        var riftSpawnCoords = getRiftSpawnCoords();
        player.teleportTo(lvl, riftSpawnCoords.x, riftSpawnCoords.y, riftSpawnCoords.z, Set.of(), player.getYRot(), 0,
                false);
    }

    private static Vec3 getRiftSpawnCoords() {
        var random = new Random();
        double x = random.nextDouble(2, 4);
        double y = 0;
        double z = random.nextDouble(2, 4);
        if (random.nextBoolean()) {
            x = -x;
        }
        if (random.nextBoolean()) {
            z = -z;
        }

        return new Vec3(PortalSpawnLocation.DEFAULT_RIFT_EXIT_POSITION).add(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (RiftEntranceAttachment) obj;
        return Objects.equals(this.keyItem, that.keyItem) && Objects.equals(this.target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyItem, target);
    }

    @Override
    public String toString() {
        return "RiftEntranceAttachment[" + "key=" + keyItem + ", " + "target=" + target + ']';
    }
}
