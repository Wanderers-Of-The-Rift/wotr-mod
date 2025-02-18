package com.dimensiondelvers.dimensiondelvers.item.socket;

import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import static com.dimensiondelvers.dimensiondelvers.init.ModTags.Items.SOCKETABLE;

public record GearSockets(List<GearSocket> sockets) {
    public static Codec<GearSockets> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            GearSocket.CODEC.listOf().fieldOf("sockets").forGetter(GearSockets::sockets)
    ).apply(inst, GearSockets::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GearSockets> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, GearSocket.STREAM_CODEC),
            GearSockets::sockets,
            GearSockets::new
    );

    public boolean isEmpty() {
        return sockets.isEmpty();
    }

    public static GearSockets randomSockets(int maxSockets, RandomSource random) {
        List<GearSocket> sockets = new ArrayList<>();
        int actualSockets = random.nextInt(maxSockets)+1;
        for (int i = 0; i < actualSockets; i++) {
            GearSocket socket = GearSocket.getRandomSocket(random);
            if (socket != null) {
                sockets.add(socket);
            }
        }
        return new GearSockets(sockets);
    }

    public static GearSockets emptySockets() {
        return new GearSockets(new ArrayList<>());
    }

    public static void generateForItem(ItemStack itemStack, Level level, Player player) {
        if(level.isClientSide() || !itemStack.is(SOCKETABLE)) return;
        GearSockets sockets = GearSockets.randomSockets(3, level.random);
        itemStack.set(ModDataComponentType.GEAR_SOCKETS, sockets);
    }
}
