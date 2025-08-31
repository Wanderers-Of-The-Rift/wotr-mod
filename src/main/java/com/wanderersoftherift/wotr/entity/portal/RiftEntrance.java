package com.wanderersoftherift.wotr.entity.portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftConfigInitialization;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftEntryState;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

public record RiftEntrance(ItemStack keyItem, ResourceKey<Level> target, boolean generated) {

    public static final Codec<RiftEntrance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ItemStack.CODEC.fieldOf("key_item").forGetter(RiftEntrance::keyItem),
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("target_dimension").forGetter(RiftEntrance::target),
                    Codec.BOOL.fieldOf("is_generated").forGetter(RiftEntrance::generated)
            ).apply(instance, RiftEntrance::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, RiftEntrance> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, RiftEntrance::keyItem, ResourceKey.streamCodec(Registries.DIMENSION),
            RiftEntrance::target, ByteBufCodecs.BOOL, RiftEntrance::generated, RiftEntrance::new);

    public static RiftEntrance create() {
        return new RiftEntrance(ItemStack.EMPTY,
                ResourceKey.create(Registries.DIMENSION, WanderersOfTheRift.id("rift_" + UUID.randomUUID())), false);
    }

    public static RiftEntrance loadRiftEntrance(CompoundTag tag, RegistryAccess registries) {
        var dataResult = RiftEntrance.CODEC.decode(registries.createSerializationContext(NbtOps.INSTANCE),
                tag.get("entrance_data"));
        return dataResult.getOrThrow().getFirst();
    }

    public void saveRiftEntrance(CompoundTag tag, RegistryAccess registries) {
        var encodedResult = RiftEntrance.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE),
                this);
        tag.put("entrance_data", encodedResult.getOrThrow());
    }

    public boolean teleportPlayer(ServerPlayer player, ServerLevel level, Vec3i currentPosition) {
        var riftId = target();
        var plDir = player.getDirection().getOpposite();

        ServerLevel lvl = RiftLevelManager.getOrCreateRiftLevel(riftId.location(), level.dimension(),
                currentPosition.offset(plDir.getStepX() * 3, plDir.getStepY() * 3, plDir.getStepZ() * 3),
                RiftConfigInitialization.initializeConfig(keyItem(), level.registryAccess()), player);
        if (lvl == null) {
            player.displayClientMessage(Component.translatable(WanderersOfTheRift.MODID + ".rift.create.failed"), true);
            return generated;
        }
        var riftData = RiftData.get(lvl);
        if (riftData.isBannedFromRift(player)) {
            return true;
        }

        riftData.addPlayer(player.getUUID());

        RiftEntryState.pushEntryState(player, lvl.dimension());

        var riftSpawnCoords = getRiftSpawnCoords();
        player.teleportTo(lvl, riftSpawnCoords.x, riftSpawnCoords.y, riftSpawnCoords.z, Set.of(), player.getYRot(), 0,
                false);
        return true;
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
}
