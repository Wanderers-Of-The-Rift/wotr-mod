package com.wanderersoftherift.wotr.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.map.MapRoom;
import com.wanderersoftherift.wotr.gui.screen.RiftMapScreen;
import com.wanderersoftherift.wotr.network.map.S2CRiftMapperRoomPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;

public class RiftMapCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal(WanderersOfTheRift.MODID + ":riftmap")
                        .requires(source -> true)
                        .then(Commands.argument("rd", IntegerArgumentType.integer()).executes((ctx) -> {
                            Minecraft.getInstance()
                                    .execute(() -> Minecraft.getInstance()
                                            .setScreen(new RiftMapScreen(Component.literal("test"),
                                                    IntegerArgumentType.getInteger(ctx, "rd"))));
                            return 1;
                        })));

        dispatcher.register(Commands.literal(WanderersOfTheRift.MODID + ":riftmap:sendroom")
                        .requires(source -> true)
                        .executes((ctx) -> {
                            MapRoom room = new MapRoom(0, 0, 0, 10, 10, 10, new ArrayList<>(), 0);

                            PacketDistributor.sendToAllPlayers(new S2CRiftMapperRoomPacket(room));

                            return 1;
                        })
                );
    }
}
