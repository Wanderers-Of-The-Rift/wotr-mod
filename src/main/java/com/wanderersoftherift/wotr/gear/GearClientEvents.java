package com.wanderersoftherift.wotr.gear;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.gear.AbstractGearAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GearClientEvents {

    @SubscribeEvent
    public static void useBasic(InputEvent.InteractionKeyMappingTriggered input) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.gameMode == null
                || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (input.isAttack() && player.getWeaponItem().has(WotrDataComponentType.GEAR_BASIC)) {
            AbstractGearAbility basic = player.getWeaponItem().get(WotrDataComponentType.GEAR_BASIC).value();
        }
    }
}
